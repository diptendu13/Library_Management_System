package com.LMS.libraryManagementSystem.models;

import com.LMS.libraryManagementSystem.enums.PaymentMode;
import com.LMS.libraryManagementSystem.enums.PaymentStatus;
import com.LMS.libraryManagementSystem.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String uniquePaymentCode;

    @CreationTimestamp
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String vpa;
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    // [a-zA-Z0-9.\-_]{2,256}@[a-zA-Z]{2,64}   -   regex for vpa
    // [1-9][0-9]{15}  -  regex for cardNumber
    // 0[1-9]/[0-9]{2}|1[0-2]/[0-9]{2}  -  regex for expiry date
    // [0-9]{3}   -  regex for cvv

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String additionalInfo;

    @ManyToOne
    @JoinColumn
    private Card card;
}
