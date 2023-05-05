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
public class BookResponseDto {

    private String bookTitle;
    private String uniqueTransactionCode;
    private String transactionDate;
    private String transactionType;
    private String transactionStatus;
}
