package com.example.rotta.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.rotta.dto.RegisterRequestDTO;
import com.example.rotta.enums.UserRole;
import com.example.rotta.models.Mechanic;
import com.example.rotta.models.Rider;
import com.example.rotta.models.User;
import com.example.rotta.repositories.MechanicRepository;
import com.example.rotta.repositories.RiderRepository;
import com.example.rotta.repositories.UserRepository;

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

        if (dto.role().equals(UserRole.RIDER)) {
            Rider rider = new Rider();
            rider.setUser(user);
            rider.setDriverLicense(dto.driverLicense());
            riderRepository.save(rider);
        } else {
            Mechanic mechanic = new Mechanic();
            mechanic.setUser(user);
            mechanic.setSpeciality(dto.speciality());
            mechanic.setWorkShopName(dto.workShopName());
            mechanicRepository.save(mechanic);
        }
        return true;

    }

    public List<User> findNearOnline(Double lat, Double lng, Double radius, Integer excludeId, UserRole role) {
        return userRepository.findNearbyOnlineByRole(lat, lng, radius, excludeId, role);
    }

    public User findById(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void markOffline(String login) {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        userRepository.markOffline(user.getId());
    }

    public User findByLogin(String login){
        return userRepository.findByLogin(login).orElseThrow(()-> new RuntimeException("User não encontrado")); 
    }

}
