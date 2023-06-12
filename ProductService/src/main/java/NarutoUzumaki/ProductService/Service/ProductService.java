package NarutoUzumaki.ProductService.Service;

import NarutoUzumaki.ProductService.Model.ProductRequest;
import NarutoUzumaki.ProductService.Model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);
    ProductResponse getProductById(long productId);
    void reduceQuantity(long productId, long quantity);
}
