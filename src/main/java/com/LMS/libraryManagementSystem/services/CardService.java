package com.LMS.libraryManagementSystem.services;

import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateSubscriptionRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateSubscriptionResponseDto;

import java.util.List;

public interface CardService {

    public UpdateSubscriptionResponseDto updateSubscriptionInfo(UpdateSubscriptionRequestDto updateSubscriptionRequestDto) throws Exception;

    public String blockCard(int cardId) throws Exception;

    public String refreshCard(int cardId) throws Exception;

    public String unblockCard(int cardId) throws Exception;

    List<FindAllBooksResponseDto> issuedBooklist(int cardId) throws Exception;
}
