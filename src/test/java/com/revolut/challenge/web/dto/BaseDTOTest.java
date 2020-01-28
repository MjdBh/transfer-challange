package com.revolut.challenge.web.dto;


import org.junit.jupiter.api.BeforeAll;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseDTOTest {
    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    protected static Validator validator;

    @BeforeAll
    public static void loadValidator() {
        validator = factory.getValidator();
    }

    public String getMessagesOfViolations(Set<ConstraintViolation<Object>> violations){
        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(System.lineSeparator()));
    }
}
