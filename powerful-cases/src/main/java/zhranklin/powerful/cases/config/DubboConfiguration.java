package zhranklin.powerful.cases.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import zhranklin.powerful.dubbo.DubboAService;
import zhranklin.powerful.dubbo.DubboBService;
import zhranklin.powerful.invoker.DubboRemoteInvoker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name="dubbo", havingValue="true")
//@EnableDubbo(scanBasePackages = "zhranklin.e2e.controller")
@DubboComponentScan(basePackages = "zhranklin.powerful.cases.controller")
public class DubboConfiguration {

    @Value("${framew.zk}")
    public String zk;

    @Value("${framew.app:framew-e2e}")
    public String app;

    @Value("${framew.port}")
    public int port;


    @Reference
    private DubboAService dubboAService;

    @Reference
    private DubboBService dubboBService;

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

    @Bean
    public DubboRemoteInvoker dubboRemoteInvoker() {
        System.out.println("DubboAService :" +dubboAService+"  DubboBService:"+dubboBService);
        return new DubboRemoteInvoker(dubboAService, dubboBService);
    }

}
