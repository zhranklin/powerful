package zhranklin.powerful.configuration;

import zhranklin.powerful.service.HttpPowerfulService;
import zhranklin.powerful.service.StringRenderer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
//@ConditionalOnExpression("${framew.type:http} || ${framew.type:springboot2.1}|| ${framew.type:springboot2}")
@ConditionalOnExpression(" '${framew.type}'=='http' || '${framew.type}'=='springboot2.1' || '${framew.type}'=='springboot2' || '${framew.type}'=='mvc'")
@ComponentScan(basePackages = {"zhranklin.powerful.controllers", "zhranklin.powerful.configuration"})
public class HttpConfiguration {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate() {{
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse clientHttpResponse) {
                    return true;
                }

                @Override
                public void handleError(ClientHttpResponse clientHttpResponse) {

                }
            });
        }};
    }

    @Bean
    HttpPowerfulService httpPowerfulService(@Qualifier("stringRenderer") StringRenderer stringRenderer) {
        return new HttpPowerfulService(stringRenderer, restTemplate());
    }

}
