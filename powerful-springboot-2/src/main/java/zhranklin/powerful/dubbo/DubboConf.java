package zhranklin.powerful.dubbo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="framew.type", havingValue="dubbo")
@EnableDubbo(scanBasePackages = "zhranklin.powerful.dubbo")
public class DubboConf {
}
