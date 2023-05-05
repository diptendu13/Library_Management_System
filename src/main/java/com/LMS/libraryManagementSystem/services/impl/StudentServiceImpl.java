package com.LMS.libraryManagementSystem.services.impl;

import com.LMS.libraryManagementSystem.customExceptions.InvalidIdException;
import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateStudentMobRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.CardDetailsResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.StudentDetailsResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateStudentMobResponseDto;
import com.LMS.libraryManagementSystem.enums.CardStatus;
import com.LMS.libraryManagementSystem.enums.SubscriptionType;
import com.LMS.libraryManagementSystem.models.Card;
import com.LMS.libraryManagementSystem.models.Student;
import com.LMS.libraryManagementSystem.repositorys.CardRepository;
import com.LMS.libraryManagementSystem.repositorys.StudentRepository;
import com.LMS.libraryManagementSystem.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CardRepository cardRepository;

    @Override
    public void addStudent(Student student) throws Exception{

        // Generate a new Card & set the default card details, whenever a new Student is added.
        Card card = new Card();
        card.setCardStatus(CardStatus.ACTIVATED);
        card.setSubscriptionType(SubscriptionType.QUARTERLY);
        card.setDues(300);
        card.setStudent(student);

        // Set the card to the corresponding student object.
        student.setCard(card);

        // Save the student object in order to get the creationTimestamp of issueDate attribute
        studentRepository.save(student);

        // Set the validTill attribute of the card object
        card.setValidTill(card.getIssueDate().plusDays(1));
        cardRepository.save(card);
    }

    @Override
    public StudentDetailsResponseDto findStudentById(int studentId) throws Exception{

        Student student = studentRepository.findById(studentId).get();

        // make the studentDetailsResponseDto
        StudentDetailsResponseDto studentDetailsResponseDto = new StudentDetailsResponseDto();
        studentDetailsResponseDto.setName(student.getName());
        studentDetailsResponseDto.setAge(student.getAge());
        studentDetailsResponseDto.setDepartment(student.getDepartment().name());
        studentDetailsResponseDto.setMobNo(student.getMobNo());
        studentDetailsResponseDto.setEmail(student.getEmail());
        studentDetailsResponseDto.setCardStatus(student.getCard().getCardStatus().name());
        studentDetailsResponseDto.setCardIssueDate(student.getCard().getIssueDate().toString());
        studentDetailsResponseDto.setCardValidTill(student.getCard().getValidTill().toString());

        return studentDetailsResponseDto;
    }

    @Override
    public UpdateStudentMobResponseDto updateMobileNumber(UpdateStudentMobRequestDto updateStudentMobRequestDto) throws Exception {

        // get the student object from student repository, using the student id from request dto
        Student student;
        try{
            student = studentRepository.findById(updateStudentMobRequestDto.getStudentId()).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Student Id");
        }
        // set the new mobile number to the corresponding student object
        student.setMobNo(updateStudentMobRequestDto.getNewMobNo());

        // save changes to student repository
        Student updatedStudent = studentRepository.save(student);

        // make the response dto using builder
        UpdateStudentMobResponseDto updateStudentMobResponseDto = UpdateStudentMobResponseDto.builder()
                .message("New mobile number successfully updated!")
                .name(updatedStudent.getName())
                .newMobNo(updatedStudent.getMobNo())
                .build();

        return updateStudentMobResponseDto;
    }

    @Override
    public CardDetailsResponseDto findCardDetailsByStudentId(int studentId) throws Exception {

        Student student;
        try{
            student = studentRepository.findById(studentId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Student Id");
        }

        Card card = student.getCard();

        // make response dto
        CardDetailsResponseDto cardDetailsResponseDto = new CardDetailsResponseDto();
        cardDetailsResponseDto.setStudentName(student.getName());
        cardDetailsResponseDto.setCardIssueDate(card.getIssueDate().toString());
        cardDetailsResponseDto.setCardStatus(card.getCardStatus().name());
        cardDetailsResponseDto.setCardValidTill(card.getValidTill().toString());
        cardDetailsResponseDto.setCardSubscriptionType(card.getSubscriptionType().name());
        cardDetailsResponseDto.setCardDues(card.getDues());

        return cardDetailsResponseDto;
    }

}
