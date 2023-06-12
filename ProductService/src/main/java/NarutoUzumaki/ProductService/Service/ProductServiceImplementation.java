package NarutoUzumaki.ProductService.Service;
import NarutoUzumaki.ProductService.Entity.Product;
import NarutoUzumaki.ProductService.Exception.ProductServiceCustomException;
import NarutoUzumaki.ProductService.Model.ProductRequest;
import NarutoUzumaki.ProductService.Model.ProductResponse;
import NarutoUzumaki.ProductService.Repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImplementation implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {

        log.info("Adding Product...!");

        // NOTE : We are using Builder Pattern to inject the model data into Entity then we will save into Repository.

        Product product = Product.builder()
                .productName(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();

        productRepository.save(product);

        log.info("Product Created...!");

        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {

        log.info("Get the product for product ID : {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product with given ID is not found...!", "PRODUCT_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);

        return productResponse;
    }

    // NOTE :: Reduce the Quantity...!

    @Override
    public void reduceQuantity(long productId, long quantity) {

        log.info("Reduce Quantity {} for Id: {}",quantity,productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceCustomException("Product with given ID is not found...!", "PRODUCT_NOT_FOUND"));


        if (product.getQuantity() < quantity){
            throw new ProductServiceCustomException(
                    "Product does not have Sufficient 'Quantity'","INSUFFICIENT_QUANTITY"
            );
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        log.info("Product Quantity Updated Successfully...!");

    }
}
