package com.example.rotta.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rider")
public class Rider {

    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "driverlicense")
    private String driverLicense;

    @OneToMany(mappedBy = "rider")
    private List<Motorcycle> motorcycles;

    @OneToMany(mappedBy = "rider")
    private List<HelpRequest> helpRequests;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public List<Motorcycle> getMotorcycles() {
        return motorcycles;
    }

    public void setMotorcycles(List<Motorcycle> motorcycles) {
        this.motorcycles = motorcycles;
    }

    public Rider() {
    }

}
