package NarutoUzumaki.OrderService.External.ErrorDecoder;
import NarutoUzumaki.OrderService.External.Exception.CustomException;
import NarutoUzumaki.OrderService.External.Response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        // Object Mapper :: help to serialize java object into "JSON"
        // and deserialize "JSON" String into Java Objects

        ObjectMapper objectMapper = new ObjectMapper();

        log.info("::{}", response.request().url());
        log.info("::{}", response.request().headers());

        try {
            ErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);

            return new CustomException(errorResponse.getErrorMessage(), errorResponse.getErrorCode(), response.status());

        } catch (IOException e) {
            throw new CustomException("INTERNAL SERVER ERROR","INTERNAL_SERVER_ERROR", 500);
        }

    }
}
