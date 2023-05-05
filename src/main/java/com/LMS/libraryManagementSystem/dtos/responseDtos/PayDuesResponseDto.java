package com.LMS.libraryManagementSystem.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayDuesResponseDto {
    private String uniquePaymentCode;
    private String paymentDate;
    private String paymentType;
    private String paymentMode;
    private String paymentStatus;

}
