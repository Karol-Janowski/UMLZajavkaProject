package pl.zajavka.mortgage;

import pl.zajavka.mortgage.model.InputData;
import pl.zajavka.mortgage.model.MortgageType;
import pl.zajavka.mortgage.model.Overpayment;
import pl.zajavka.mortgage.services.AmountsCalculationServiceImpl;
import pl.zajavka.mortgage.services.ConstantAmountsCalculationServiceImpl;
import pl.zajavka.mortgage.services.DecreasingAmountsCalculationServiceImpl;
import pl.zajavka.mortgage.services.MortgageCalculationService;
import pl.zajavka.mortgage.services.MortgageCalculationServiceImpl;
import pl.zajavka.mortgage.services.OverpaymentCalculationServiceImpl;
import pl.zajavka.mortgage.services.PrintingService;
import pl.zajavka.mortgage.services.PrintingServiceImpl;
import pl.zajavka.mortgage.services.RateCalculationService;
import pl.zajavka.mortgage.services.RateCalculationServiceImpl;
import pl.zajavka.mortgage.services.ReferenceCalculationServiceImpl;
import pl.zajavka.mortgage.services.ResidualCalculationServiceImpl;
import pl.zajavka.mortgage.services.SummaryServiceFactory;
import pl.zajavka.mortgage.services.TimePointCalculationServiceImpl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

public class MortgageCalculator {

    public static void main(String[] args) {

        Map<Integer, BigDecimal> overpaymentSchema = new TreeMap<>();
        overpaymentSchema.put(5, BigDecimal.valueOf(12000));
        overpaymentSchema.put(19, BigDecimal.valueOf(10000));
        overpaymentSchema.put(28, BigDecimal.valueOf(11000));
        overpaymentSchema.put(64, BigDecimal.valueOf(16000));
        overpaymentSchema.put(78, BigDecimal.valueOf(18000));

        InputData defaultInputData = InputData.defaultInputData()
            .withAmount(new BigDecimal("296192.11"))
            .withMonthsDuration(BigDecimal.valueOf(360))
            .withOverpaymentReduceWay(Overpayment.REDUCE_RATE)
            .withRateType(MortgageType.CONSTANT)
            .withOverpaymentSchema(overpaymentSchema);

        PrintingService printingService = new PrintingServiceImpl();
        RateCalculationService rateCalculationService = new RateCalculationServiceImpl(
            new TimePointCalculationServiceImpl(),
            new OverpaymentCalculationServiceImpl(),
            new AmountsCalculationServiceImpl(
                new ConstantAmountsCalculationServiceImpl(),
                new DecreasingAmountsCalculationServiceImpl()
            ),
            new ResidualCalculationServiceImpl(),
            new ReferenceCalculationServiceImpl()
        );

        MortgageCalculationService mortgageCalculationService = new MortgageCalculationServiceImpl(
            rateCalculationService,
            printingService,
            SummaryServiceFactory.create()
        );

        mortgageCalculationService.calculate(defaultInputData);
    }
}
