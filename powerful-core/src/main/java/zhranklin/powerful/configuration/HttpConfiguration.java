package zhranklin.powerful.configuration;

import zhranklin.powerful.invoker.HttpRemoteInvoker;
import zhranklin.powerful.service.StringRenderer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Configuration
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
	HttpRemoteInvoker httpRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer, RestTemplate restTemplate) {
        return new HttpRemoteInvoker(stringRenderer, restTemplate);
    }

}
