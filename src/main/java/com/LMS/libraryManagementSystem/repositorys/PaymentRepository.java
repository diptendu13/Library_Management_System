package com.LMS.libraryManagementSystem.repositorys;

import com.LMS.libraryManagementSystem.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}
