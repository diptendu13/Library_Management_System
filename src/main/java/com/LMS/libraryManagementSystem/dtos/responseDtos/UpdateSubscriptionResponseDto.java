package com.LMS.libraryManagementSystem.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubscriptionResponseDto {
    private String studentName;
    private String cardStatus;
    private String subscriptionType;
}
