package com.LMS.libraryManagementSystem.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayDuesRequestDto {

    private int cardId;
    private String paymentType;
    private String paymentMode;
    private String vpa;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private int paidAmount;
}
