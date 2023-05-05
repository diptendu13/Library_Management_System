package com.LMS.libraryManagementSystem.services;

import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateStudentMobRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.CardDetailsResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.StudentDetailsResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateStudentMobResponseDto;
import com.LMS.libraryManagementSystem.models.Student;

public interface StudentService {
    public void addStudent(Student student) throws Exception;

    public StudentDetailsResponseDto findStudentById(int studentId) throws Exception;

    public UpdateStudentMobResponseDto updateMobileNumber(UpdateStudentMobRequestDto updateStudentMobRequestDto) throws Exception;

    public CardDetailsResponseDto findCardDetailsByStudentId(int studentId) throws Exception;
}
