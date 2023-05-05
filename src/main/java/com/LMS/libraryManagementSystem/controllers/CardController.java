package com.LMS.libraryManagementSystem.controllers;

import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateSubscriptionRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateSubscriptionResponseDto;
import com.LMS.libraryManagementSystem.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card")
public class CardController {
    @Autowired
    CardService cardService;

    @PutMapping("/update-subscription-info")
    public ResponseEntity updateSubscriptionInfo(@RequestBody UpdateSubscriptionRequestDto updateSubscriptionRequestDto){
        try{
            UpdateSubscriptionResponseDto updatedSubscriptionInfo = cardService.updateSubscriptionInfo(updateSubscriptionRequestDto);
            return new ResponseEntity<>(updatedSubscriptionInfo, HttpStatus.ACCEPTED);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/block-card")
    public ResponseEntity blockCard(@RequestParam int cardId){
        try{
           String message = cardService.blockCard(cardId);
           return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/refresh-card/{cardId}")
    public ResponseEntity refreshCard(@PathVariable int cardId){
        try {
            String message = cardService.refreshCard(cardId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/unblock-card")
    public ResponseEntity unblockCard(@RequestParam int cardId){
        try{
            String message = cardService.unblockCard(cardId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/issued-booklist")
    public ResponseEntity issuedBookList(@RequestParam int cardId){
        try{
            List<FindAllBooksResponseDto> bookList = cardService.issuedBooklist(cardId);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
