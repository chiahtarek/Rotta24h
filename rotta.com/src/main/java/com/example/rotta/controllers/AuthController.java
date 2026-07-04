package com.example.rotta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.example.rotta.dto.RegisterRequestDTO;
import com.example.rotta.dto.RegisterResponseDTO;
import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;
import com.example.rotta.services.UserService;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @GetMapping("/register")
    public ModelAndView registerGet(){
        ModelAndView mv = new ModelAndView("register"); 
        return mv;
    }

    @PostMapping("/register")
    public ModelAndView registerPost(@ModelAttribute RegisterRequestDTO dto) {
        ModelAndView mv = new ModelAndView("register"); 
        User user = new User();
        user.setLogin(dto.login());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        userService.register(user);
    
        mv.addObject("successMessage", "Registrado com Sucesso ! "); 
        return mv;
    }

}
