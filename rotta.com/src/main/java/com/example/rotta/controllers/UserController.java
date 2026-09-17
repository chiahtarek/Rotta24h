package com.example.rotta.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rotta.models.User;
import com.example.rotta.services.UserService;

@Controller 
@RequestMapping("/user")
public class UserController {

    @Autowired 
    private UserService userService; 

    @GetMapping("/profile")
    public ModelAndView profileGet(Principal principal){
        ModelAndView mv = new ModelAndView("user/info");
        String userName = principal.getName(); 
        User user = userService.findByLogin(userName); 
        mv.addObject("user", user); 
        return mv;
    }
}
