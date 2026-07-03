package com.example.rotta.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.rotta.dto.RegisterRequestDTO;
import com.example.rotta.models.User;
import com.example.rotta.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired 
    UserRepository userRepository; 

    public User register(User user){
        return userRepository.save(user); 
    } 

}
