package zhranklin.powerful.configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="framew.type", havingValue="dubbo")
//@EnableDubbo(scanBasePackages = "zhranklin.powerful.dubbo")
public class DubboConfiguration {

    @Value("${framew.zk}")
    public String zk;

    @Value("#{systemProperties['framew.application.name']}")
    //@Value("${framew.app}")
    public String app;

    @Value("${framew.port}")
    public int port;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName(app);
        return application;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(zk);
        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(port);
        return protocolConfig;
    }


}
