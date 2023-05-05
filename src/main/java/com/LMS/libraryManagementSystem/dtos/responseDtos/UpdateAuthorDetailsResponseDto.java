package com.LMS.libraryManagementSystem.dtos.responseDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAuthorDetailsResponseDto {

    private String name;
    private int age;
    private String email;
}
