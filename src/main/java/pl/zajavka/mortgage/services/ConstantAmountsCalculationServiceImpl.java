package pl.zajavka.mortgage.services;

import lombok.extern.slf4j.Slf4j;
import pl.zajavka.mortgage.model.InputData;
import pl.zajavka.mortgage.model.Overpayment;
import pl.zajavka.mortgage.model.Rate;
import pl.zajavka.mortgage.model.RateAmounts;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class ConstantAmountsCalculationServiceImpl implements ConstantAmountsCalculationService {

    @Override
    public RateAmounts calculate(final InputData inputData, final Overpayment overpayment) {
        BigDecimal interestPercent = inputData.getInterestPercent();
        log.debug("InterestPercent: [{}]", interestPercent);
        BigDecimal q = AmountsCalculationService.calculateQ(interestPercent);
        log.trace("Q: [{}]", q);

        BigDecimal residualAmount = inputData.getAmount();
        log.info("ResidualAmount: [{}]", residualAmount);

        BigDecimal interestAmount = AmountsCalculationService.calculateInterestAmount(residualAmount, interestPercent);
        log.info("InterestAmount: [{}]", interestAmount);
        BigDecimal rateAmount = calculateConstantRateAmount(q, interestAmount, residualAmount, inputData.getAmount(), inputData.getMonthsDuration());
        log.info("RateAmount: [{}]", rateAmount);
        BigDecimal capitalAmount = AmountsCalculationService.compareCapitalWithResidual(rateAmount.subtract(interestAmount), residualAmount);
        log.info("CapitalAmount: [{}]", capitalAmount);

        return new RateAmounts(rateAmount, interestAmount, capitalAmount, overpayment);
    }

    @Override
    public RateAmounts calculate(final InputData inputData, final Overpayment overpayment, final Rate previousRate) {
        BigDecimal interestPercent = inputData.getInterestPercent();
        log.debug("InterestPercent: [{}]", interestPercent);
        BigDecimal q = AmountsCalculationService.calculateQ(interestPercent);
        log.trace("Q: [{}]", q);

        BigDecimal residualAmount = previousRate.getMortgageResidual().getResidualAmount();
        log.info("ResidualAmount: [{}]", residualAmount);
        BigDecimal referenceAmount = previousRate.getMortgageReference().getReferenceAmount();
        log.info("ReferenceAmount: [{}]", referenceAmount);
        BigDecimal referenceDuration = previousRate.getMortgageReference().getReferenceDuration();
        log.info("ReferenceDuration: [{}]", referenceDuration);

        BigDecimal interestAmount = AmountsCalculationService.calculateInterestAmount(residualAmount, interestPercent);
        log.info("InterestAmount: [{}]", interestAmount);
        BigDecimal rateAmount = calculateConstantRateAmount(q, interestAmount, residualAmount, referenceAmount, referenceDuration);
        log.info("RateAmount: [{}]", rateAmount);
        BigDecimal capitalAmount = AmountsCalculationService.compareCapitalWithResidual(rateAmount.subtract(interestAmount), residualAmount);
        log.info("CapitalAmount: [{}]", capitalAmount);

        return new RateAmounts(rateAmount, interestAmount, capitalAmount, overpayment);
    }

    private BigDecimal calculateConstantRateAmount(
        final BigDecimal q,
        final BigDecimal interestAmount,
        final BigDecimal residualAmount,
        final BigDecimal referenceAmount,
        final BigDecimal referenceDuration
    ) {
        BigDecimal rateAmount = referenceAmount
            .multiply(q.pow(referenceDuration.intValue()))
            .multiply(q.subtract(BigDecimal.ONE))
            .divide(q.pow(referenceDuration.intValue()).subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        log.info("RateAmount: [{}]", rateAmount);
        return compareRateWithResidual(rateAmount, interestAmount, residualAmount);
    }

    private BigDecimal compareRateWithResidual(final BigDecimal rateAmount, final BigDecimal interestAmount, final BigDecimal residualAmount) {
        if (rateAmount.subtract(interestAmount).compareTo(residualAmount) >= 0) {
            return residualAmount.add(interestAmount);
        }
        return rateAmount;
    }

}
