package com.LMS.libraryManagementSystem.services.impl;

import com.LMS.libraryManagementSystem.customExceptions.CardNotActiveException;
import com.LMS.libraryManagementSystem.customExceptions.CardNotBlockedException;
import com.LMS.libraryManagementSystem.customExceptions.InvalidIdException;
import com.LMS.libraryManagementSystem.dtos.requestDtos.UpdateSubscriptionRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.FindAllBooksResponseDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.UpdateSubscriptionResponseDto;
import com.LMS.libraryManagementSystem.enums.CardStatus;
import com.LMS.libraryManagementSystem.enums.SubscriptionType;
import com.LMS.libraryManagementSystem.models.Book;
import com.LMS.libraryManagementSystem.models.Card;
import com.LMS.libraryManagementSystem.repositorys.CardRepository;
import com.LMS.libraryManagementSystem.services.CardService;
import com.LMS.libraryManagementSystem.utilities.AmountConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    CardRepository cardRepository;

    @Override
    public UpdateSubscriptionResponseDto updateSubscriptionInfo(UpdateSubscriptionRequestDto updateSubscriptionRequestDto) throws Exception {
        Card card;
        try{
            card = cardRepository.findById(updateSubscriptionRequestDto.getCardId()).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }

        // automatically refresh the card before subscription update operation
        try{
            refreshCard(card.getId());
        }
        catch (Exception e){
        }

        if (card.getCardStatus() == CardStatus.BLOCKED){
            throw new CardNotActiveException("Card is Blocked. Cannot Update Subscription-Type.");
        }
        if (card.getCardStatus() == CardStatus.ACTIVATED){
            for (SubscriptionType sub : SubscriptionType.values()){
                if (sub.name().equalsIgnoreCase(updateSubscriptionRequestDto.getNewSubscriptionType())){
                    card.setSubscriptionType(sub);
                    break;
                }
            }
        }
        else{
            for (SubscriptionType sub : SubscriptionType.values()){
                if (sub.name().equalsIgnoreCase(updateSubscriptionRequestDto.getNewSubscriptionType())){
                    int oldSubscriptionAmount = AmountConverter.getAmountOfSubscription(card);
                    int totalDues = card.getDues();
                    int fineAmount = totalDues - oldSubscriptionAmount;
                    card.setSubscriptionType(sub);
                    cardRepository.save(card);
                    int newSubscriptionAmount = AmountConverter.getAmountOfSubscription(card);
                    card.setDues(newSubscriptionAmount + fineAmount);
                    break;
                }
            }
        }
        cardRepository.save(card);

        // make response dto
        UpdateSubscriptionResponseDto updateSubscriptionResponseDto = UpdateSubscriptionResponseDto.builder()
                .studentName(card.getStudent().getName())
                .cardStatus(card.getCardStatus().name())
                .subscriptionType(card.getSubscriptionType().name())
                .build();

        return updateSubscriptionResponseDto;
    }

    @Override
    public String blockCard(int cardId) throws Exception {
        Card card;
        try{
            card = cardRepository.findById(cardId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }
        if (card.getCardStatus() == CardStatus.BLOCKED){
            throw new CardNotActiveException("Card Already Blocked!");
        }

        // automatically refresh the card before block operation
        try{
            refreshCard(cardId);
        }
        catch (Exception e){
        }

        // store card state -> [0(false) -> expired], [1(true) -> active]
        if (card.getCardStatus() == CardStatus.EXPIRED){
            card.setPreviousState(false);
        }
        else{
            card.setPreviousState(true);
        }

        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

        return "Card has been blocked.";
    }

    @Override
    public String refreshCard(int cardId) throws Exception {
        Card card;
        try{
            card = cardRepository.findById(cardId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }

        // check if card is blocked
        if (card.getCardStatus() == CardStatus.BLOCKED){
            throw new CardNotActiveException("Card is Blocked! Cannot Refresh Card.");
        }

        // check if card is expired
        if (card.getCardStatus() == CardStatus.EXPIRED){
            cardRepository.save(card);
            return "Card has been refreshed successfully!";
        }

        // if we reach here means card is definitely active
        LocalDateTime todayDate = LocalDateTime.now();
        if (card.getValidTill().isBefore(todayDate)){
            card.setCardStatus(CardStatus.EXPIRED);
            int renewAmount = AmountConverter.getAmountOfSubscription(card);
            card.setDues(card.getDues() + renewAmount);
        }
        cardRepository.save(card);

        return "Card has been refreshed successfully!";
    }

    @Override
    public String unblockCard(int cardId) throws Exception {
        Card card;
        try{
            card = cardRepository.findById(cardId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }

        // check if card is blocked
        if (card.getCardStatus() != CardStatus.BLOCKED){
            throw new CardNotBlockedException("Card is not blocked.");
        }

        // if we reach here means card is definitely blocked
        LocalDateTime todayDate = LocalDateTime.now();
        if (card.getValidTill().isBefore(todayDate) && !card.isPreviousState()){
            card.setCardStatus(CardStatus.EXPIRED);
        }
        else if (card.getValidTill().isBefore(todayDate) && card.isPreviousState()){
            card.setCardStatus(CardStatus.EXPIRED);
            int renewAmount = AmountConverter.getAmountOfSubscription(card);
            card.setDues(card.getDues() + renewAmount);
        }
        else{
            card.setCardStatus(CardStatus.ACTIVATED);
        }
        cardRepository.save(card);

        return "Card has been unblocked!";
    }

    @Override
    public List<FindAllBooksResponseDto> issuedBooklist(int cardId) throws Exception {
        Card card;
        try {
            card = cardRepository.findById(cardId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }

        List<FindAllBooksResponseDto> bookList = new ArrayList<>();
        for (Book book : card.getBookList()){
            FindAllBooksResponseDto bookDetails = FindAllBooksResponseDto.builder()
                    .bookTitle(book.getTitle())
                    .publication(book.getPublication())
                    .authorName(book.getAuthor().getName())
                    .genre(book.getGenre().name())
                    .price(book.getPrice())
                    .build();

                    bookList.add(bookDetails);
        }
        return bookList;
    }
}

// expired -> block -> expired -> no amount added
// activated -> block -> activated -> no amount added
// activated -> block -> expired -> add subscription amount