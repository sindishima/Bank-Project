package com.example.Bank;


import com.example.Bank.Others.IBAN;
import org.junit.jupiter.api.Test;

public class TestIBANGeneration {

    @Test
    public void testIban() throws Exception {

        //src/main/resources/application.properties
        IBAN iban = new IBAN();
        String ibanString = iban.generateIban();

        System.out.println(ibanString);
    }


}
