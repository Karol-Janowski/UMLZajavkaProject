package pl.zajavka.mortgage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;

@With
@Value
@Builder
@AllArgsConstructor
public class TimePoint {

    BigDecimal year;
    BigDecimal month;
    LocalDate date;

}
