package zhranklin.powerful;

import zhranklin.powerful.service.PowerfulService;
import zhranklin.powerful.service.StringRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = "zhranklin.powerful.controllers")
public class PowerfulAutoConfiguration {

	@Bean
	PowerfulService powerfulService() {
		return new PowerfulService(stringRenderer(), restTemplate());
	}

	@Bean
	StringRenderer stringRenderer() {
		return new StringRenderer();
	}

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


}
