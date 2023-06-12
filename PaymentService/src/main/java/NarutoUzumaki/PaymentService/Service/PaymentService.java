package NarutoUzumaki.PaymentService.Service;

import NarutoUzumaki.PaymentService.Model.PaymentRequest;
import NarutoUzumaki.PaymentService.Model.PaymentResponse;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);
    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
