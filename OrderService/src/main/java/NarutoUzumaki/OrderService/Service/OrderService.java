package NarutoUzumaki.OrderService.Service;

import NarutoUzumaki.OrderService.Model.OrderRequest;
import NarutoUzumaki.OrderService.Model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderDetails(long orderId);
}
