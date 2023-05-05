package com.LMS.libraryManagementSystem.dtos.requestDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAuthorDetailsRequestDto {

    private int authorId;
    private String newName;
    private int newAge;
    private String newEmail;
}
