package com.mindex.challenge.data;

import java.time.LocalDate;

public class Compensation {
    //float because a salary might have cents
    private float salary ;
    //not really a need for the time as well as date
    private LocalDate effectiveDate;

    public Compensation() {
    }

    public float getSalary() {
        return salary ;
    }

    public void setSalary(int salary ) {
        this.salary  = salary ;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
