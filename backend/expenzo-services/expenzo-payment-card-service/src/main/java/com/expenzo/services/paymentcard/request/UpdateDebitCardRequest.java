package com.expenzo.services.paymentcard.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDebitCardRequest {

    private String bankAccountId;

    @Size(max = 19, message = "Card number must not exceed 19 characters")
    private String cardNumber;

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Expiry must be in YYYY-MM format")
    private String validTo;

    @Size(max = 50, message = "Nick name must not exceed 50 characters")
    private String nickName;
}
