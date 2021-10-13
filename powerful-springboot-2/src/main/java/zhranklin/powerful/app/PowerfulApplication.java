package zhranklin.powerful.app;

import zhranklin.powerful.assist.Gen;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PowerfulApplication {

    public static void main(String[] args) throws InterruptedException {
        Gen.isStage0 = args.length > 0 && args[0].equals("stage0");
        Gen.gen(System.getenv("APP"), System.getenv("DUBBO_DEPENDS_ON"));
        if (!Gen.isStage0) {
            SpringApplication.run(PowerfulApplication.class, args);
        }
    }

}
