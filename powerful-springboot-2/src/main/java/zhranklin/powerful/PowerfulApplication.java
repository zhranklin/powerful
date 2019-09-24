package zhranklin.powerful;

import zhranklin.powerful.service.PowerfulService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(PowerfulService.class)
public class PowerfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerfulApplication.class, args);
	}

}
