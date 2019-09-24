package zhranklin.powerful;

import zhranklin.powerful.service.PowerfulService;
import zhranklin.powerful.service.StringRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = "zhranklin.powerful.controllers")
public class PowerfulSpringConfiguration {

	@Bean
	PowerfulService powerfulService() {
		return new PowerfulService(stringRenderer());
	}

	@Bean
	StringRenderer stringRenderer() {
		return new StringRenderer();
	}

}
