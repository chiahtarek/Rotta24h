package com.example.rotta.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.rotta.dto.RegisterRequestDTO;
import com.example.rotta.models.Mechanic;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.MechanicRepository;
import com.example.rotta.repositories.RiderRepository;
import com.example.rotta.repositories.UserRepository;
import com.example.rotta.roles.UserRole;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MechanicRepository mechanicRepository; 

    @Autowired 
    RiderRepository riderRepository; 

    @Autowired
    PasswordEncoder passwordEncoder;

    public boolean register(RegisterRequestDTO dto) {

        User user = new User();
        user.setLogin(dto.login());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setFullName(dto.fullName());
        userRepository.save(user);

        System.out.println(dto.role());

        if(dto.role().equals(UserRole.RIDER)){
            Rider rider = new Rider(); 
            rider.setUser(user);
            rider.setDriverLicense(dto.driverLicense());
            riderRepository.save(rider); 
        }
        else{
            Mechanic mechanic = new Mechanic(); 
            mechanic.setUser(user);
            mechanic.setSpeciality(dto.speciality());
            mechanic.setWorkShopName(dto.workShopName());
            mechanicRepository.save(mechanic); 
        }
        return true; 

    }

}
