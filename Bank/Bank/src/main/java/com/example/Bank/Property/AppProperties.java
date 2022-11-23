package com.example.Bank.Property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")

public class AppProperties {

    @Value("${card.credit.income}")
    int cardIncome;

    public int getCardIncome() {
        return cardIncome;
    }

    public void setCardIncome(int cardIncome) {
        this.cardIncome = cardIncome;
    }
}
