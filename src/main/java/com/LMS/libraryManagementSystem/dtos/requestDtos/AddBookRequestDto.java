package com.LMS.libraryManagementSystem.dtos.requestDtos;

import com.LMS.libraryManagementSystem.enums.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBookRequestDto {

    private int authorId;
    private String title;
    private Genre genre;
    private int numberOfPages;
    private String publication;
    private int price;

}
