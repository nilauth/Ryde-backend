package com.cosmos.usersmanagementsystem.dto;

import com.cosmos.usersmanagementsystem.entity.OurUsers;
import com.cosmos.usersmanagementsystem.entity.Villes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class OffresDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This specifies auto-increment
    private  String id;
    private int statusCode;
    private String error;
    private String message;
    private int driverId;
    private String villeDepart;
    private String villeArriv;
    private String heureDepart;
    private String heureArriv;
    private Date date;
    private Double prix;
    private int placeDispo;
    private int placeInitiale;
    private Boolean status;
}
