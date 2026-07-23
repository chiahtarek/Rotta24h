package com.example.rotta.dto;

import com.example.rotta.enums.ProblemType;

public record HelpRequestDTO(Double latitude, Double longitude, ProblemType problemType){

}
