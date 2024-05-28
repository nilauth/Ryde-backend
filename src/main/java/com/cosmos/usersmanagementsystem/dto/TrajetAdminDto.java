package com.cosmos.usersmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class TrajetAdminDto {
    private int statusCode;
    private String error;
    private String message;
    private String id;
    private String driverName;
    private String clientName;
    private String villeDepart;
    private String villeArriv;
    private Date date;
    private Double prix;
}
