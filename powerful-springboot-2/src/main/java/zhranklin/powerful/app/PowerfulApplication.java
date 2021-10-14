package zhranklin.powerful.app;

import zhranklin.powerful.assist.Gen;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PowerfulApplication {

    public static void main(String[] args) throws InterruptedException {
        Gen.isStage0 = args.length > 0 && args[0].equals("stage0");
        if ("true".equals(System.getProperty("powerful.dubbo.enabled"))) {
            Gen.gen(System.getenv("APP"));
        }
        if (!Gen.isStage0) {
            SpringApplication.run(PowerfulApplication.class, args);
        }
    }

}
