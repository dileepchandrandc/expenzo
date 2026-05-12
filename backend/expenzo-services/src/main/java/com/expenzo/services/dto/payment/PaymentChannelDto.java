package com.expenzo.services.dto.payment;

import com.expenzo.services.enums.PaymentChannel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentChannelDto {

    private PaymentChannel channelType;
    private Integer channelId;
    private String channelName;
    private String bankName;
    private String bankAccountNickName;
}
