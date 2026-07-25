package com.example.rotta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rotta.dto.HelpRequestDTO;
import com.example.rotta.models.HelpRequest;
import com.example.rotta.services.HelpRequestService;

@Controller
@RequestMapping("help")
public class HelpRequestController {

    @Autowired
    HelpRequestService helpRequestService;
    
    @GetMapping("/request")
    public ModelAndView helpGet(){
        ModelAndView mv = new ModelAndView("helprequest/add"); 
        return mv;
    }
    
    @PostMapping("/request")
    public ModelAndView helpPost(@RequestBody HelpRequestDTO dto ){
        ModelAndView mv = new ModelAndView("helprequest/add");
        helpRequestService.save(dto); 
        mv.addObject("successMessage", "Help Request with Success");
        return mv;
    }
}
