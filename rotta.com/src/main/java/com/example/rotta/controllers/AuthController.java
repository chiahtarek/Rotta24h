package com.example.rotta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import com.example.rotta.config.TokenConfig;
import com.example.rotta.dto.LoginRequestDTO;
import com.example.rotta.dto.RegisterRequestDTO;
import com.example.rotta.dto.RegisterResponseDTO;
import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;
import com.example.rotta.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenConfig tokenConfig;

    @Autowired
    UserService userService;

    

    @Autowired
    AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public ModelAndView registerGet() {
        ModelAndView mv = new ModelAndView("auth/register");
        return mv;
    }

    @PostMapping("/register")
    public ModelAndView registerPost(@ModelAttribute RegisterRequestDTO dto) {
        ModelAndView mv = new ModelAndView("auth/register");
        userService.register(dto);

        mv.addObject("successMessage", "Registrado com Sucesso ! ");
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView loginGet() {
        ModelAndView mv = new ModelAndView("auth/login");
        return mv;
    }

    @PostMapping("/login")
    public ModelAndView loginPost(@RequestParam String login, @RequestParam String password,
            HttpServletResponse response) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));

                   User user = (User) authentication.getPrincipal();  

                   String token = tokenConfig.generateToken(user);

                    Cookie cookie = new Cookie("JWT", token);
                    cookie.setHttpOnly(true);
                    cookie.setPath("/");
                    cookie.setMaxAge(60 * 60);
                    response.addCookie(cookie);   
        } catch (BadCredentialsException ex) {
            ModelAndView mv = new ModelAndView("/auth/login");
            mv.addObject("Error", "Credentials Invalid"); 
            return mv; 
        }

        ModelAndView mv = new ModelAndView("dashboard");
        return mv;
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); 
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        String login = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null;
        if (login != null) {
            userService.markOffline(login);
        }

        return new ModelAndView("redirect:/auth/login");
    }

}
