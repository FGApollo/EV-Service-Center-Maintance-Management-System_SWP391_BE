package com.example.Ev.System.service;

import com.example.Ev.System.config.PaymentConfig;
import com.example.Ev.System.config.VNpayConfig;
import com.example.Ev.System.dto.PaymentDto;
import com.example.Ev.System.dto.PaymentResponse;
import com.example.Ev.System.entity.Invoice;
import com.example.Ev.System.entity.Payment;
import com.example.Ev.System.repository.InvoiceRepository;
import com.example.Ev.System.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class PaymentService implements  PaymentServiceI {

    private String vnp_SecretKey = PaymentConfig.secretKey;

    private String payUrl = PaymentConfig.vnp_payurl;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private VNpayConfig vnpayConfig;
    @Autowired
    private InvoiceService invoiceService;


    public void savePayment(Integer invoiceId, BigDecimal amount, String referenceNo) {
         Payment payment = new Payment();
         Invoice invoice = invoiceRepository.findById(invoiceId).orElse(null);
         payment.setInvoice(invoice);
         payment.setAmount(amount);
         payment.setMethod("online");
         payment.setPaymentDate(LocalDateTime.now());
         payment.setReferenceNo(referenceNo);
         paymentRepository.save(payment);

    }

    @Override
    public PaymentResponse createPaymentUrl(PaymentDto paymentDto) {
        var invoiceInfo = invoiceRepository.findById(paymentDto.getInvoiceId()).
                orElseThrow(() -> new RuntimeException("Invoice not found"));
        BigDecimal totalPrice = invoiceInfo.getTotalAmount();
        Long amount = totalPrice.multiply(BigDecimal.valueOf(100)).longValue();
        Payment payment = Payment.builder()
                .invoice(invoiceInfo)
                .amount(totalPrice)
                .method(paymentDto.getMethod())
                .paymentDate(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        Map<String, String> vnp_ParamsMap = vnpayConfig.getVnPayConfig();
        vnp_ParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnp_ParamsMap.put("vnp_IpAddr", paymentDto.getClientIp());
        vnp_ParamsMap.put("vnp_TxnRef", payment.getId().toString());
        vnp_ParamsMap.put("vnp_OrderInfo", "Pay invoice " + invoiceInfo.getId());
        vnp_ParamsMap.put("vnp_OrderType", "billpayment");
        vnp_ParamsMap.put("vnp_Locale", "vn");
        vnp_ParamsMap.put("vnp_ReturnUrl", PaymentConfig.vnp_returnurl);

        String queryUrl = PaymentConfig.getPaymentUrl(vnp_ParamsMap, true);
        String hashdata = PaymentConfig.getPaymentUrl(vnp_ParamsMap, false);
        String vnp_SecureHash = PaymentConfig.hmacSHA512(vnp_SecretKey, hashdata);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = payUrl + "?" + queryUrl;

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .invoiceId(invoiceInfo.getId())
                .amount(totalPrice)
                .method(payment.getMethod())
                .message("create VnpayUrl successfully")
                .paymentUrl(paymentUrl)
                .paymentType("VNPAY")
                .build();
    }

    @Override
    public PaymentResponse handlePaymentCallback(Map<String, String> allParams) {
        String responseCode = allParams.get("vnp_ResponseCode");
        String invoiceId = allParams.get("vnp_OrderInfo").replaceAll("\\D+", "");
        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(allParams.get("vnp_Amount"))).divide(BigDecimal.valueOf(100));
        String vnp_TxnRef = allParams.get("vnp_TxnRef");
        Integer paymentId = Integer.parseInt(vnp_TxnRef);
        Payment payment = paymentRepository.findById(paymentId).
                orElseThrow(() -> new RuntimeException("Payment not found with id: " + vnp_TxnRef));
        if(responseCode.equals("00")) {
            savePayment(Integer.parseInt(invoiceId), amount, vnp_TxnRef);
            invoiceService.MarkInvoiceAsPaid(Integer.parseInt(invoiceId));
        }else {
            System.out.println("Payment failed with response code: " + responseCode);
        }
        return PaymentResponse.builder()
                .paymentId(paymentId)
                .invoiceId(Integer.parseInt(invoiceId))
                .amount(amount)
                .method(payment.getMethod())
                .message("00".equals(responseCode) ? "Payment successfully" : "Payment failed with response: " + responseCode)
                .paymentType("VNPAY")
                .build();
    }
}
