package com.LMS.libraryManagementSystem.controllers;

import com.LMS.libraryManagementSystem.dtos.requestDtos.PayDuesRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.PayDuesResponseDto;
import com.LMS.libraryManagementSystem.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping("/pay-dues")
    public ResponseEntity payDues(@RequestBody PayDuesRequestDto payDuesRequestDto){
        try{
            PayDuesResponseDto invoice = paymentService.payDues(payDuesRequestDto);
            return new ResponseEntity<>(invoice, HttpStatus.ACCEPTED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-pending-dues")
    public ResponseEntity getPendingDues(@RequestParam int cardId){
        try{
            int pendingDues = paymentService.getPendingDues(cardId);
            return new ResponseEntity<>(pendingDues, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-payments-list")
    public ResponseEntity findPaymentList(@RequestParam int cardId){
        try{
            List<PayDuesResponseDto> paymentList = paymentService.findPaymentList(cardId);
            return new ResponseEntity<>(paymentList, HttpStatus.ACCEPTED);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
