package com.LMS.libraryManagementSystem.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBookResponseDto {

    private String message;
    private String bookTitle;
    private String publication;
    private String authorName;
    private int price;

}
