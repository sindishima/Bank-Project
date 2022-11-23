package com.example.Bank.Others;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.Random;


public class IBAN {
    private Integer beggining;

    private static final String PROPERTY_NAME = "iban.beggining";
    private static final String FILE_PATH = "C:\\Users\\ccc\\Desktop\\Bank\\Bank\\src\\main\\resources\\application.properties";

    private static final Logger log = LoggerFactory.getLogger(IBAN.class);

    public IBAN() {
        try {
            beggining = getPropertyValue();
        } catch (Exception e) {
            log.error(e.toString());
            e.printStackTrace();
        }
    }

    private Integer getPropertyValue() throws Exception {
        FileReader fileReader = new FileReader(FILE_PATH);
        Properties properties = new Properties();
        properties.load(fileReader);
        Integer value = Integer.parseInt(properties.getProperty(PROPERTY_NAME));
        return value;
    }

    private void updateValue(Integer beggining) throws Exception{
        FileReader fileReader = new FileReader(FILE_PATH);
        Properties properties = new Properties();
        properties.load(fileReader);
        properties.setProperty(PROPERTY_NAME, beggining.toString());
        FileWriter fileWriter = new FileWriter(FILE_PATH);
        properties.store(fileWriter, "OK");
    }

    public String generateIban() throws Exception {
        Random rand = new Random();

        String countryCode = "AL";
        String bankCode = rand.ints(65, 90)
                .limit(4)
                .mapToObj(c -> (char)c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();

        Integer checkCode = (int)Math.floor(Math.random()*(100)+0);

        String iban = countryCode+checkCode+bankCode+beggining;
        System.out.println(iban);
        beggining++;
        updateValue(beggining);
        return iban;
    }
}
