package zhranklin.powerful;

import zhranklin.powerful.service.StringRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = {"zhranklin.powerful"})
public class PowerfulAutoConfiguration {
    @Bean
    StringRenderer stringRenderer() {
        return new StringRenderer();
    }

}
