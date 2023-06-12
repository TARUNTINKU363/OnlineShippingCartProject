package NarutoUzumaki.PaymentService.Repository;
import NarutoUzumaki.PaymentService.Entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long> {

    // this is custom method to add in Repository !!
    TransactionDetails findByOrderId(long orderId);

}
