package com.revolut.challenge.exception;


import javax.validation.ConstraintViolation;
import java.util.Set;

public class ConstraintViolationException  extends  RuntimeException {
    private Set<ConstraintViolation<Object> >violations;

    public ConstraintViolationException(Set<ConstraintViolation<Object>> violations) {
        this.violations = violations;
    }

    public Set<ConstraintViolation<Object>> getViolations() {
        return violations;
    }
}
