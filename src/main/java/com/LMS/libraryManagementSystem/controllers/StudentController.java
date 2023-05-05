package com.LMS.libraryManagementSystem.controllers;

import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateStudentMobRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.CardDetailsResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.StudentDetailsResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateStudentMobResponseDto;
import com.LMS.libraryManagementSystem.models.Student;
import com.LMS.libraryManagementSystem.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentService studentService;

    @PostMapping("/add")
    public ResponseEntity addStudent(@RequestBody Student student) {
        try{
            studentService.addStudent(student);
            return new ResponseEntity<>("Student Added Successfully!", HttpStatus.CREATED);
        }
        catch (Exception e){
            return new ResponseEntity<>("Unable To Add! Try Again Later.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/find-student-by-id")
    public ResponseEntity findStudentById(@RequestParam int studentId){
        try{
            StudentDetailsResponseDto studentDetails = studentService.findStudentById(studentId);
            return new ResponseEntity<>(studentDetails, HttpStatus.FOUND);
        }
        catch (Exception e){
            return new ResponseEntity("Invalid Student ID", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-mobile-number")
    public ResponseEntity updateMobileNumber(@RequestBody UpdateStudentMobRequestDto updateStudentMobRequestDto){
        try{
            UpdateStudentMobResponseDto updatedInfo = studentService.updateMobileNumber(updateStudentMobRequestDto);
            return new ResponseEntity(updatedInfo, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity("Invalid Student ID", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-card-details-by-student-id")
    public ResponseEntity findCardDetailsByStudentId(@RequestParam int studentId){
        try{
            CardDetailsResponseDto cardDetails = studentService.findCardDetailsByStudentId(studentId);
            return new ResponseEntity<>(cardDetails, HttpStatus.FOUND);
        }
        catch (Exception e){
            return new ResponseEntity("Invalid Student ID", HttpStatus.BAD_REQUEST);
        }
    }



}
