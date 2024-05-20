package pl.zajavka.mortgage.services;

import pl.zajavka.mortgage.model.Rate;

import java.math.BigDecimal;

@FunctionalInterface
public interface Function {

    BigDecimal calculate(Rate rate);
}
