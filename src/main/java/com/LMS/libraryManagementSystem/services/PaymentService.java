package com.LMS.libraryManagementSystem.services;

import com.LMS.libraryManagementSystem.dtos.requestDtos.PayDuesRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.PayDuesResponseDto;

import java.util.List;

public interface PaymentService {

    public PayDuesResponseDto payDues(PayDuesRequestDto payDuesRequestDto) throws Exception;

    public int getPendingDues(int cardId) throws Exception;

    public List<PayDuesResponseDto> findPaymentList(int cardId) throws Exception;
}
