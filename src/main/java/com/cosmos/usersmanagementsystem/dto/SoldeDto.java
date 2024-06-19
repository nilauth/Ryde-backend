package com.cosmos.usersmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class SoldeDto {
    private Integer id;
    private int statusCode;
    private String error;
    private String message;
    private String nomClient;
    private String cardNumber;
    private String dateExpiration;
    private String cvv;
    private Integer clientId;
    private Integer solde;
}
