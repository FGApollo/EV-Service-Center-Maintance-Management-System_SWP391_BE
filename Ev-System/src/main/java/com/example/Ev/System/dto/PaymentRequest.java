package com.example.Ev.System.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    private String vnp_TmnCode;
    private Long vnp_Amount;
    private String vnp_BankCode;
    private String vnp_BankTranNo;
    private String vnp_CardType;
    private String vnp_PayDate;
    private String vnp_TransactionNo;
    private String vnp_OrderInfo;
    private String vnp_TxnRef;
    private String vnp_TransactionStatus;
    private String vnp_SecureHash;
}
