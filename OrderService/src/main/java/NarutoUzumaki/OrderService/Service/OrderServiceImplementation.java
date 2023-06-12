package NarutoUzumaki.OrderService.Service;
import NarutoUzumaki.OrderService.Entity.Order;
import NarutoUzumaki.OrderService.External.Client.PaymentService;
import NarutoUzumaki.OrderService.External.Client.ProductService;
import NarutoUzumaki.OrderService.External.Exception.CustomException;
import NarutoUzumaki.OrderService.External.Request.PaymentRequest;
import NarutoUzumaki.OrderService.Model.OrderRequest;
import NarutoUzumaki.OrderService.Model.OrderResponse;
import NarutoUzumaki.OrderService.Model.PaymentResponse;
import NarutoUzumaki.OrderService.Model.ProductResponse;
import NarutoUzumaki.OrderService.Repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImplementation implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    // Defining REST Template here..!

    // As we have defined the bean, we can get that bean here as a load Balanced..!

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        // 1.  Order Entity -> Save the data with Status Order Created.
        // 2. Product Service -> Block the Products [ Reduce the Quantity ]
        // 3. Payment Service -> Payment :: Success -> COMPLETE :: else -> CANCELLED.

        log.info("Placing Order Request : {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());

        log.info("Creating Order with status 'CREATED'");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        orderRepository.save(order);

        // Calling Payment Service to complete the payment !!!

        log.info("Calling Payment Service to Complete the Payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus = null;

        try {

            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully..! Changing the order to 'PLACED' ");
            orderStatus = "PLACED";

        } catch (Exception e) {

            log.info("Error Occurred in Payment..! Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";

        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order Places Successfully with Order Id: {}", order.getId());

        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {

        log.info("Get Order Details for Order ID : {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order Id : {}" + orderId , "NOT_FOUND",404));

        log.info("Invoking Product service to fetch the product for Id : {}",order.getProductId());

        ProductResponse productResponse
                = restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),ProductResponse.class);

        log.info("Getting Payment information from the payment service");

        PaymentResponse paymentResponse
                = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),PaymentResponse.class);

       OrderResponse.PaymentDetails paymentDetails
               = OrderResponse.PaymentDetails.builder()
               .paymentId(paymentResponse.getPaymentId())
               .orderId(paymentResponse.getOrderId())
               .status(paymentResponse.getStatus())
               .paymentMode(paymentResponse.getPaymentMode())
               .amount(paymentResponse.getAmount())
               .paymentDate(paymentResponse.getPaymentDate())
               .build();

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails.builder()
                        .productName(productResponse.getProductName())
                        .productId(productResponse.getProductId())
                        .price(productResponse.getPrice())
                        .quantity(productResponse.getQuantity())
                        .build();


        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderData(order.getOrderDate())
                // Adding new one
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse;

    }
}
