package com.example.Bank.Repostory;

import com.example.Bank.Enum.CardStatus;
import com.example.Bank.Model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {
    @Query("select c from Card as c where c.cardStatus=:status")
    List<Card> findWaitingForApprovalCards(CardStatus status);
}
