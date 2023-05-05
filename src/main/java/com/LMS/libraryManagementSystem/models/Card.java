package com.LMS.libraryManagementSystem.models;

import com.LMS.libraryManagementSystem.enums.CardStatus;
import com.LMS.libraryManagementSystem.enums.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @CreationTimestamp
    private LocalDateTime issueDate;

    @Enumerated(EnumType.STRING)
    private CardStatus cardStatus;

    @UpdateTimestamp
    private LocalDateTime updatedOn;

    private LocalDateTime validTill;

    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;

    private int dues;

    private boolean previousState;

    @OneToOne
    @JoinColumn
    private Student student;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Transaction> transactionList = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Book> bookList = new ArrayList<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Payment> paymentList = new ArrayList<>();
}
