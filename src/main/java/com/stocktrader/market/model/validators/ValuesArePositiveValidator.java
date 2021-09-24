package com.stocktrader.market.model.validators;

import com.stocktrader.market.model.dto.PortfolioInfo;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigInteger;
import java.util.Map;

@Component
public class ValuesArePositiveValidator implements ConstraintValidator<ValuesArePositiveConstraint, Map<String, PortfolioInfo>> {

    public boolean isValid(Map<String, PortfolioInfo> value, ConstraintValidatorContext context) {
        return value.values().stream()
                .map(PortfolioInfo::getTotalValue)
                .noneMatch(num -> num.compareTo(BigInteger.ZERO) < 0);
    }
}
