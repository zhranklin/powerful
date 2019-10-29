package zhranklin.powerful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(PowerfulAutoConfiguration.class)
public class PowerfulApplication {

	public static void main(String[] args) {
		SpringApplication.run(PowerfulApplication.class, args);
	}

}
