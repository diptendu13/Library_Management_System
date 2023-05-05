package com.LMS.libraryManagementSystem.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSubscriptionRequestDto {

    private int cardId;
    private String newSubscriptionType;
}
