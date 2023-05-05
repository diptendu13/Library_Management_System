package com.LMS.libraryManagementSystem.utilities;

import com.LMS.libraryManagementSystem.enums.SubscriptionType;
import com.LMS.libraryManagementSystem.models.Card;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AmountConverter {
    public static int getAmountOfSubscription(Card card){
        int quarterlyAmount = 300;
        if (card.getSubscriptionType() == SubscriptionType.QUARTERLY){
            return quarterlyAmount;
        }
        else if (card.getSubscriptionType() == SubscriptionType.HALF_YEARLY){
            return quarterlyAmount * 2;
        }
        return quarterlyAmount * 4;
    }
}
