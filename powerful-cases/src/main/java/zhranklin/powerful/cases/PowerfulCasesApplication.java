package zhranklin.powerful.cases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PowerfulCasesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerfulCasesApplication.class, args);
	}

	@Bean
	StaticResources staticResources() {
		return new StaticResources();
	}

}
