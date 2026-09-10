package com.example.rotta.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rotta.models.Motorcycle;
import com.example.rotta.models.User;
import com.example.rotta.services.MotorcycleService;
import com.example.rotta.services.UserService;

@Controller
@RequestMapping("motorcycle")
public class MotorcycleController {

    @Autowired
    private UserService userService;

    @Autowired
    private MotorcycleService motorcycleService;

    @GetMapping("/add")
    public ModelAndView addGet() {
        ModelAndView mv = new ModelAndView("motorcycle/add");
        return mv;
    }

    @PostMapping("/add")
    public ModelAndView addPost(@ModelAttribute Motorcycle motorcycle) {
        ModelAndView mv = new ModelAndView("motorcycle/add");
        motorcycleService.save(motorcycle);
        mv.addObject("successMessage", "Motorcycle added with Success");
        return mv;
    }

    @GetMapping("/list")
    public ModelAndView list(Principal principal) {
        ModelAndView mv = new ModelAndView("motorcycle/list");
        String userName = principal.getName();
        List<Motorcycle> motorcycles = motorcycleService.motorcyclesByLogin(userName);
        mv.addObject("motorcycles", motorcycles);
        return mv;
    }
}
