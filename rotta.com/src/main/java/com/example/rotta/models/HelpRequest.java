package com.example.rotta.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.ManyToAny;

import com.example.rotta.enums.ProblemType;
import com.example.rotta.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "helprequest")
public class HelpRequest {


    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rider_id")
    private Rider rider;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitute")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "problemtype", nullable = true)
    private ProblemType problemType;

    public HelpRequest() {
    }

    public HelpRequest(Rider rider, LocalDateTime dateTime, Double latitude, Double longitude, ProblemType problemType) {
        this.rider = rider;
        this.dateTime = dateTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.problemType = problemType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Rider getRider() {
        return rider;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

}
