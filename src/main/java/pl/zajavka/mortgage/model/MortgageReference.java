package pl.zajavka.mortgage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor
public class MortgageReference {

    BigDecimal referenceAmount;
    BigDecimal referenceDuration;

}
