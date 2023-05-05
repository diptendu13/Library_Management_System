package com.LMS.libraryManagementSystem.services.impl;

import com.LMS.libraryManagementSystem.customExceptions.*;
import com.LMS.libraryManagementSystem.dtos.requestDtos.PayDuesRequestDto;
import com.LMS.libraryManagementSystem.dtos.responseDtos.PayDuesResponseDto;
import com.LMS.libraryManagementSystem.enums.*;
import com.LMS.libraryManagementSystem.models.Card;
import com.LMS.libraryManagementSystem.models.Payment;
import com.LMS.libraryManagementSystem.repositorys.CardRepository;
import com.LMS.libraryManagementSystem.repositorys.PaymentRepository;
import com.LMS.libraryManagementSystem.services.CardService;
import com.LMS.libraryManagementSystem.services.PaymentService;
import com.LMS.libraryManagementSystem.utilities.AmountConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CardRepository cardRepository2;

    @Autowired
    CardService cardService2;

    @Override
    public PayDuesResponseDto payDues(PayDuesRequestDto payDuesRequestDto) throws Exception {

        // automatically refresh the card before any type of payment operation
        try{
            cardService2.refreshCard(payDuesRequestDto.getCardId());
        }
        catch (Exception e){
        }

        boolean successfulPaymentFlag = false;
        // create new payment object
        Payment payment = new Payment();

        // set unique-payment-code
        payment.setUniquePaymentCode(String.valueOf(UUID.randomUUID()));

        // set payment-type
        for (PaymentType paymentType : PaymentType.values()){
            if (paymentType.name().equalsIgnoreCase(payDuesRequestDto.getPaymentType())){
                payment.setPaymentType(paymentType);
                break;
            }
        }

        // set payment-mode
        for (PaymentMode paymentMode : PaymentMode.values()){
            if (paymentMode.name().equalsIgnoreCase(payDuesRequestDto.getPaymentMode())){
                payment.setPaymentMode(paymentMode);
                break;
            }
        }

        // check if card id is valid
        Card card;
        try{
            card = cardRepository2.findById(payDuesRequestDto.getCardId()).get();
        }
        catch (Exception e){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Invalid_Card_Id");
            paymentRepository.save(payment);
            throw new InvalidIdException("Invalid Card Id");
        }

        // card is valid, so set card to payment object
        payment.setCard(card); // MANDATORY LINE FOR BI-DIRECTIONAL RELATIONSHIP AND SAVING IN DATABASE

        // check if card-status is blocked
        if (card.getCardStatus() == CardStatus.BLOCKED){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Card_Is_Blocked");
            paymentRepository.save(payment);
            throw new CardNotActiveException("Card is Blocked. Contact Administrator.");
        }

        // Possible Correct Combinations of Card-Status & Payment-Type :
        // if card status is "EXPIRED" & paymentType is "RENEWAL" OR "RENEWAL & FINE"
        // if card status is "ACTIVATED" & paymentType is "FINE"
        // if card status is "ACTIVATED" & paymentType is "ACTIVATION" & card.getPaymentList() is empty

        // The six if-conditions below are complementary checks for Card-Status & Payment-Type Mismatch Exception

        // first_check : (expired && (activation || fine))
        if (card.getCardStatus() == CardStatus.EXPIRED && (payment.getPaymentType() == PaymentType.ACTIVATION || payment.getPaymentType() == PaymentType.FINE)){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Type_Mismatch");
            paymentRepository.save(payment);
            throw new CardStatusAndPaymentTypeMismatchException("Card-Status is 'EXPIRED'. Payment-Type should either be 'RENEWAL' or 'RENEWAL_AND_FINE'");
        }

        // second_check : (activated && (renewal || renewal_and_fine))
        if (card.getCardStatus() == CardStatus.ACTIVATED && (payment.getPaymentType() == PaymentType.RENEWAL || payment.getPaymentType() == PaymentType.RENEWAL_AND_FINE)){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Type_Mismatch");
            paymentRepository.save(payment);
            throw new CardStatusAndPaymentTypeMismatchException("Card-Status is 'ACTIVATED'. Payment-Type should either be 'ACTIVATION' or 'FINE'");
        }

        // check for successful payments
        List<Payment> successfulPaymentList = card.getPaymentList().stream().filter(e -> e.getPaymentStatus() == PaymentStatus.SUCCESS).toList();
        if (successfulPaymentList.size() > 0){
            successfulPaymentFlag = true;
        }

        // third_check : (activated && paymentListEmpty && fine)
        if (card.getCardStatus() == CardStatus.ACTIVATED && payment.getPaymentType() == PaymentType.FINE && (card.getPaymentList().isEmpty() || !successfulPaymentFlag)){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Type_Mismatch");
            paymentRepository.save(payment);
            throw new CardStatusAndPaymentTypeMismatchException("Card-Status is 'ACTIVATED'. For First-Payment, Payment-Type should be 'ACTIVATION'");
        }

        // fourth_check : (activated && paymentListNotEmpty && activation)
        if (card.getCardStatus() == CardStatus.ACTIVATED && payment.getPaymentType() == PaymentType.ACTIVATION && successfulPaymentFlag){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Type_Mismatch");
            paymentRepository.save(payment);
            throw new CardStatusAndPaymentTypeMismatchException("Card-Status is 'ACTIVATED'. Since, this is not the First-Payment, Payment-Type should be 'FINE'");
        }

        // get the subscription-amount corresponding to subscription-type
        int subscriptionAmount = AmountConverter.getAmountOfSubscription(card);

        // fifth_check : (expired && dues > subscriptionAmount && renewal)
        if (card.getCardStatus() == CardStatus.EXPIRED && card.getDues() > subscriptionAmount && payment.getPaymentType() == PaymentType.RENEWAL){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Type_Mismatch");
            paymentRepository.save(payment);
            throw new CardStatusAndPaymentTypeMismatchException("Card-Status is 'EXPIRED'. Since, the due-amount is greater than the subscription-amount, Payment-Type should be 'RENEWAL_AND_FINE'");
        }

        // sixth_check : (expired && dues == subscriptionAmount && renewal_and_fine)
        if (card.getCardStatus() == CardStatus.EXPIRED && card.getDues() == subscriptionAmount && payment.getPaymentType() == PaymentType.RENEWAL_AND_FINE){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Type_Mismatch");
            paymentRepository.save(payment);
            throw new CardStatusAndPaymentTypeMismatchException("Card-Status is 'EXPIRED'. Since, the due-amount is exactly equal to the subscription-amount, Payment-Type should be 'RENEWAL'");
        }

        // Authenticate vpa
        if (payDuesRequestDto.getVpa() != null){
            Pattern vpaPattern = Pattern.compile("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$");
            Matcher vpaMatcher = vpaPattern.matcher(payDuesRequestDto.getVpa());
            if (vpaMatcher.find()){
                payment.setVpa(payDuesRequestDto.getVpa());
            }
            else{
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setAdditionalInfo("Vpa_Authentication_Failure");
                paymentRepository.save(payment);
                throw new AuthenticationException("VPA Not Authentic. Cannot Proceed With Payment.");
            }
        }

        // Authenticate card-number
        if (payDuesRequestDto.getCardNumber() != null){
            Pattern cardNumberPattern = Pattern.compile("^[1-9][0-9]{15}$");
            Matcher cardNumberMatcher = cardNumberPattern.matcher(payDuesRequestDto.getCardNumber());
            if (cardNumberMatcher.find()){
                payment.setCardNumber(payDuesRequestDto.getCardNumber());
            }
            else{
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setAdditionalInfo("Card_Number_Authentication_Failure");
                paymentRepository.save(payment);
                throw new AuthenticationException("Card-Number Not Authentic. Cannot Proceed With Payment.");
            }
        }

        // Authenticate expiry-date
        if (payDuesRequestDto.getExpiryDate() != null){
            Pattern expiryDatePattern = Pattern.compile("^0[1-9]/[0-9]{2}|1[0-2]/[0-9]{2}$");
            Matcher expiryDateMatcher = expiryDatePattern.matcher(payDuesRequestDto.getExpiryDate());
            if (expiryDateMatcher.find()){
                payment.setExpiryDate(payDuesRequestDto.getExpiryDate());
            }
            else{
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setAdditionalInfo("Expiry_Date_Authentication_Failure");
                paymentRepository.save(payment);
                throw new AuthenticationException("Expiry-Date Not Authentic. Cannot Proceed With Payment.");
            }
        }

        // Authenticate cvv
        if (payDuesRequestDto.getCvv() != null){
            Pattern cvvPattern = Pattern.compile("^[0-9]{3}$");
            Matcher cvvMatcher = cvvPattern.matcher(payDuesRequestDto.getCvv());
            if (cvvMatcher.find()){
                payment.setCvv(payDuesRequestDto.getCvv());
            }
            else{
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setAdditionalInfo("Cvv_Authentication_Failure");
                paymentRepository.save(payment);
                throw new AuthenticationException("Cvv Not Authentic. Cannot Proceed With Payment.");
            }
        }

        // Possible Correct Combinations of Payment-Mode & Credentials :
        // UPI - VPA
        // Card - Card_No, Expiry_Date, CVV
        // Cash - nothing

        // The four if-conditions below are complementary checks for Payment-Mode & Credentials Mismatch Exception

        // first_check : UPI && VPA not null && (Card_No || Expiry_Date || CVV) is not null
        if (payment.getPaymentMode() == PaymentMode.UPI && payDuesRequestDto.getVpa() != null && (payDuesRequestDto.getCardNumber() != null || payDuesRequestDto.getCvv() != null || payDuesRequestDto.getExpiryDate() != null)){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Mode_Mismatch");
            paymentRepository.save(payment);
            throw new PaymentModeAndCredentialMismatchException("Payment-Mode is 'UPI'. Only 'VPA' required.");
        }

        // second_check : Card && Card_No not null && Expiry_Date not null && CVV not null && VPA is not null
        if (payment.getPaymentMode() == PaymentMode.CARD && payDuesRequestDto.getCardNumber() != null && payDuesRequestDto.getExpiryDate() != null && payDuesRequestDto.getCvv() != null && payDuesRequestDto.getVpa() != null){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Mode_Mismatch");
            paymentRepository.save(payment);
            throw new PaymentModeAndCredentialMismatchException("Payment-Mode is 'CARD'. Only 'CARD_NUMBER','EXPIRY_DATE','CVV' required.");
        }

        // third_check : UPI && VPA is null
        if (payment.getPaymentMode() == PaymentMode.UPI && payDuesRequestDto.getVpa() == null){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Mode_Mismatch");
            paymentRepository.save(payment);
            throw new PaymentModeAndCredentialMismatchException("Payment-Mode is 'UPI', but 'VPA' not provided.");
        }

        // fourth_check : Card && (Card_No || Expiry_Date || CVV) is null
        if (payment.getPaymentMode() == PaymentMode.CARD && (payDuesRequestDto.getCardNumber() == null || payDuesRequestDto.getExpiryDate() == null || payDuesRequestDto.getCvv() == null)){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Payment_Mode_Mismatch");
            paymentRepository.save(payment);
            throw new PaymentModeAndCredentialMismatchException("Payment-Mode is 'CARD', but 'CARD_NUMBER' or 'EXPIRY_DATE' or 'CVV' not provided.");
        }

        if (card.getDues() == 0){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Dues_Already_Paid");
            paymentRepository.save(payment);
            throw new DuesPendingException("Dues already paid!");
        }

        // check if dueAmount = or > paid amount
        if (card.getDues() > payDuesRequestDto.getPaidAmount()){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setAdditionalInfo("Insufficient_Amount_Paid");
            paymentRepository.save(payment);
            throw new DuesPendingException("Insufficient amount to clear pending dues.");
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setAdditionalInfo("Dues_Cleared_Successfully");

        // save payment object
        Payment updatedPayment = paymentRepository.save(payment);

        card.setDues(0);

        if (card.getCardStatus() == CardStatus.EXPIRED){
            card.setCardStatus(CardStatus.ACTIVATED);
            Card updatedCard = cardRepository2.save(card);
            if (updatedCard.getSubscriptionType() == SubscriptionType.QUARTERLY){
                card.setValidTill(updatedCard.getUpdatedOn().plusDays(1));
            }
            else if (updatedCard.getSubscriptionType() == SubscriptionType.HALF_YEARLY){
                card.setValidTill(updatedCard.getUpdatedOn().plusDays(2));
            }
            else{
                card.setValidTill(updatedCard.getUpdatedOn().plusDays(3));
            }
        }
        card.getPaymentList().add(updatedPayment);
        cardRepository2.save(card);

        // make response dto
        PayDuesResponseDto payDuesResponseDto = PayDuesResponseDto.builder()
                .uniquePaymentCode(updatedPayment.getUniquePaymentCode())
                .paymentDate(updatedPayment.getPaymentDate().toString())
                .paymentType(updatedPayment.getPaymentType().name())
                .paymentMode(updatedPayment.getPaymentMode().name())
                .paymentStatus(updatedPayment.getPaymentStatus().name())
                .build();

        return payDuesResponseDto;
    }

    @Override
    public int getPendingDues(int cardId) throws Exception {
        // check if card id is valid
        Card card;
        try{
           card = cardRepository2.findById(cardId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }
        return card.getDues();
    }

    @Override
    public List<PayDuesResponseDto> findPaymentList(int cardId) throws Exception {
        Card card;
        try{
            card = cardRepository2.findById(cardId).get();
        }
        catch (Exception e){
            throw new InvalidIdException("Invalid Card Id");
        }
        // make response dto
        List<PayDuesResponseDto> paymentList = new ArrayList<>() ;
        for (Payment payment : card.getPaymentList()){
            PayDuesResponseDto pay = PayDuesResponseDto.builder()
                    .paymentStatus(payment.getPaymentStatus().name())
                    .paymentMode(payment.getPaymentMode().name())
                    .paymentType(payment.getPaymentType().name())
                    .uniquePaymentCode(payment.getUniquePaymentCode())
                    .paymentDate(payment.getPaymentDate().toString())
                    .build();

            paymentList.add(pay);
        }

        return paymentList;
    }
}

// expired - renewal, renewal & fine
// activated - fine, activation
// blocked - nothing

// UPI - VPA
// Card - Card_No, CVV
// Cash - nothing

// UPI && (Card No || CVV) is not null && VPA not null
// Card && VPA is not null && Card_No not null && CVV not null
// Card && (Card_No || CVV) is null && VPA is null
// UPI && VPA is null && (Card_No && CVV) is not null



// *********************************-----NOTE_(VVI)-----*************************************** :
// - A bidirectional mapping is established by "mapped by : ..." in the parent entity.
// - whenever there is bidirectional mapping, only one linked variable can't be set leaving the other unset. Either both gets set or none.
// - we need both the linked variables set before saving into database
// - In one-to-many relationship :
//      - Card Entity is Parent - contains "mapped by" & List<Payment> paymentList (list of Payment objects)
//      - Payment Entity is Child - contains @JoinColumn & Card card (a single object)
//      - Whenever, child obj. saves parent obj. that is,
//          - Payment payment = get payment obj. from payment-repo;
//          - set parent-object(Card) to this child-object(Payment) -> payment.setCard(card); & save the child to child repo.
//          - whenever, this is done, the primary key of this "card" object is assigned as a value to the column card_id of card table and gets stored in database,as soon as the child object payment is saved.
//          - also, whenever this entry is saved in child repo, the corresponding payment object gets added to the list(paymentList) which is mapped to the parent entity automatically, that is, card.getPaymentList().add(payment)
//          - so, when only the bi-directional-linked variable is getting updated in the parent-entity, we can just save the child-object to child-repo instead of saving the entire parent-object to parent-repo, letting the cascade operation save the child-object subsequently.
//          - however, when values of other variables also gets changed in parent-object, we must save the parent-object and not the child-object in order to save both updated-objects successfully
