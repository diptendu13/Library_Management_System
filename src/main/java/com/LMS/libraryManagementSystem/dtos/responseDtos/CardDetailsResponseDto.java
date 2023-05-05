package com.LMS.libraryManagementSystem.dtos.responseDtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level=AccessLevel.PRIVATE)
public class CardDetailsResponseDto {
    String studentName;
    String cardIssueDate;
    String cardStatus;
    String cardValidTill;
    String cardSubscriptionType;
    int cardDues;
}
