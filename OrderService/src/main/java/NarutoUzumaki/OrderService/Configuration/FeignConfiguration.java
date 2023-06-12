package NarutoUzumaki.OrderService.Configuration;
import NarutoUzumaki.OrderService.External.ErrorDecoder.CustomErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// NOTE :: this Configuration is for Spring to use the  "CustomErrorDecoder"

@Configuration
public class FeignConfiguration {

    @Bean
    ErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }

}
