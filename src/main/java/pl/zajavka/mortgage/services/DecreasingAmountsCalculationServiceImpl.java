package pl.zajavka.mortgage.services;

import lombok.extern.slf4j.Slf4j;
import pl.zajavka.mortgage.model.InputData;
import pl.zajavka.mortgage.model.Overpayment;
import pl.zajavka.mortgage.model.Rate;
import pl.zajavka.mortgage.model.RateAmounts;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class DecreasingAmountsCalculationServiceImpl implements DecreasingAmountsCalculationService {

    @Override
    public RateAmounts calculate(final InputData inputData, final Overpayment overpayment) {
        BigDecimal interestPercent = inputData.getInterestPercent();
        log.info("InterestPercent: [{}]", interestPercent);

        final BigDecimal residualAmount = inputData.getAmount();
        log.info("ResidualAmount: [{}]", residualAmount);
        final BigDecimal residualDuration = inputData.getMonthsDuration();
        log.info("ResidualDuration: [{}]", residualDuration);

        BigDecimal interestAmount = AmountsCalculationService.calculateInterestAmount(residualAmount, interestPercent);
        log.info("InterestAmount: [{}]", interestAmount);
        BigDecimal capitalAmount = AmountsCalculationService.compareCapitalWithResidual(calculateDecreasingCapitalAmount(residualAmount, residualDuration), residualAmount);
        log.info("CapitalAmount: [{}]", capitalAmount);
        BigDecimal rateAmount = capitalAmount.add(interestAmount);
        log.info("RateAmount: [{}]", rateAmount);

        return new RateAmounts(rateAmount, interestAmount, capitalAmount, overpayment);
    }

    @Override
    public RateAmounts calculate(final InputData inputData, final Overpayment overpayment, final Rate previousRate) {
        BigDecimal interestPercent = inputData.getInterestPercent();
        log.info("InterestPercent: [{}]", interestPercent);

        BigDecimal residualAmount = previousRate.getMortgageResidual().getResidualAmount();
        log.info("ResidualAmount: [{}]", residualAmount);
        BigDecimal referenceAmount = previousRate.getMortgageReference().getReferenceAmount();
        log.info("ReferenceAmount: [{}]", referenceAmount);
        BigDecimal referenceDuration = previousRate.getMortgageReference().getReferenceDuration();
        log.info("ReferenceDuration: [{}]", referenceDuration);

        BigDecimal interestAmount = AmountsCalculationService.calculateInterestAmount(residualAmount, interestPercent);
        log.info("InterestAmount: [{}]", interestAmount);
        BigDecimal capitalAmount = AmountsCalculationService.compareCapitalWithResidual(calculateDecreasingCapitalAmount(referenceAmount, referenceDuration), residualAmount);
        log.info("CapitalAmount: [{}]", capitalAmount);
        BigDecimal rateAmount = capitalAmount.add(interestAmount);
        log.info("RateAmount: [{}]", rateAmount);

        return new RateAmounts(rateAmount, interestAmount, capitalAmount, overpayment);
    }

    private BigDecimal calculateDecreasingCapitalAmount(final BigDecimal residualAmount, final BigDecimal residualDuration) {
        return residualAmount.divide(residualDuration, 2, RoundingMode.HALF_UP);
    }
}
