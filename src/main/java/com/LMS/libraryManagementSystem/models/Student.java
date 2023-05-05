package com.LMS.libraryManagementSystem.models;

import com.LMS.libraryManagementSystem.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Department department;

    private String mobNo;

    private String email;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL)
    private Card card;
}
