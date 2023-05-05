package com.LMS.libraryManagementSystem.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {

    private int cardId;
    private int bookId;
    private int durationInDays;
}
