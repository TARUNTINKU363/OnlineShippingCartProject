package NarutoUzumaki.PaymentService.Service;
import NarutoUzumaki.PaymentService.Entity.TransactionDetails;
import NarutoUzumaki.PaymentService.Model.PaymentMode;
import NarutoUzumaki.PaymentService.Model.PaymentRequest;
import NarutoUzumaki.PaymentService.Model.PaymentResponse;
import NarutoUzumaki.PaymentService.Repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImplementation implements PaymentService{

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public long doPayment(PaymentRequest paymentRequest) {

        log.info("Recording Payment Details : {}",paymentRequest);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .orderId(paymentRequest.getOrderId())
                .referenceNumber(paymentRequest.getReferenceNumber())
                .amount(paymentRequest.getAmount())
                .build();

        transactionDetailsRepository.save(transactionDetails);

        log.info("Transaction Completed with ID : {}",transactionDetails.getId());

        return transactionDetails.getId();
    }

    @Override
    public PaymentResponse getPaymentDetailsByOrderId(String orderId) {

        log.info("Getting payment details for the Order Id: {}", orderId);

        TransactionDetails transactionDetails
                = transactionDetailsRepository.findByOrderId(Long.parseLong(orderId));

        PaymentResponse paymentResponse =
                PaymentResponse.builder()
                        .paymentId(transactionDetails.getId())
                        .paymentMode(PaymentMode.valueOf(transactionDetails.getPaymentMode()))
                        .orderId(transactionDetails.getOrderId())
                        .amount(transactionDetails.getAmount())
                        .build();

        return paymentResponse;
    }
}
