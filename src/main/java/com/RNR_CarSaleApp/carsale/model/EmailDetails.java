package com.RNR_CarSaleApp.carsale.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailDetails {


    private EmailInfo fromAddress;
    private EmailInfo toAddress;
    private String subject;
    private String emailBody;

    public EmailDetails() {

    }
}
