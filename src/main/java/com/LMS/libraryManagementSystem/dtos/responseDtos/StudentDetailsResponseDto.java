package com.LMS.libraryManagementSystem.dtos.responseDtos;

import com.LMS.libraryManagementSystem.enums.Department;
import com.LMS.libraryManagementSystem.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailsResponseDto {

    private String name;
    private int age;
    private String department;
    private String mobNo;
    private String email;
    private String cardStatus;
    private String cardIssueDate;
    private String cardValidTill;
}
