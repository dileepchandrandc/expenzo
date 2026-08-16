package com.expenzo.services.paymentcard.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCreditCardRequest {

    private String bankAccountId;

    @Size(max = 19, message = "Card number must not exceed 19 characters")
    private String cardNumber;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Expiry must be in YYYY-MM format")
    private String validTo;

    @DecimalMin(value = "0.01", message = "Credit limit must be greater than 0")
    private BigDecimal creditLimit;

    @Min(value = 1, message = "Billing date must be between 1 and 28")
    @Max(value = 28, message = "Billing date must be between 1 and 28")
    private Integer billingDate;

    @Size(max = 50, message = "Nick name must not exceed 50 characters")
    private String nickName;
}
