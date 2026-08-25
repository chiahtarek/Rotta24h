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
@RequestMapping("dashboard")
public class DashboardController {

    @Autowired
    UserService userService; 
    
    @GetMapping("/test")
    public ModelAndView dashboard(Principal principal) {
        ModelAndView mv = new ModelAndView("dashboard");
        String login = principal.getName();
        User user = userService.findByLogin(login); 
        mv.addObject("user", user); 
        return mv;
    }
}
