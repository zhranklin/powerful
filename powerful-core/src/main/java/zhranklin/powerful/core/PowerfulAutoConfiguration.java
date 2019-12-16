package zhranklin.powerful.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import zhranklin.powerful.core.cases.StaticResources;
import zhranklin.powerful.core.invoker.DubboRemoteInvoker;
import zhranklin.powerful.core.invoker.GrpcRemoteInvoker;
import zhranklin.powerful.core.invoker.HttpRemoteInvoker;
import zhranklin.powerful.core.service.GrpcClientInterceptor;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.core.service.TestingMethodService;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = {"zhranklin.powerful.core.controllers"})
public class PowerfulAutoConfiguration {
    @Bean
    StringRenderer stringRenderer() {
        return new StringRenderer();
    }

    @Bean
    PowerfulService powerfulService(HttpRemoteInvoker http,
                                    @Autowired(required = false) DubboRemoteInvoker dubbo,
                                    @Autowired(required = false) GrpcRemoteInvoker grpc) {
        PowerfulService powerful = new PowerfulService(stringRenderer());
        powerful.setInvoker("http", http);
        powerful.setInvoker("dubbo", dubbo);
        powerful.setInvoker("grpc", grpc);
        if (dubbo != null) {
            dubbo.setPowerful(powerful);
        }
        if (grpc != null) {
            grpc.setPowerful(powerful);
        }
        return powerful;
    }

    @Bean
    TestingMethodService testingMethodService() {
        return new TestingMethodService();
    }

    @Bean
    StaticResources staticResources() {
        return new StaticResources();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate() {{
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse clientHttpResponse) {
                    return true;
                }

                @Override
                public void handleError(ClientHttpResponse clientHttpResponse) {

                }
            });
        }};
    }

    @Bean
    HttpRemoteInvoker httpRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer, RestTemplate restTemplate) {
        return new HttpRemoteInvoker(stringRenderer, restTemplate);
    }

    @Bean
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilderCustomizer() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        return builder;
    }

    @ConditionalOnProperty(name = "framew.type", havingValue = "grpc")
    @ComponentScan(basePackages = {"zhranklin.powerful.rpc.grpc"})
    public class GrpcConfiguration {

        @Bean
        public GrpcClientInterceptor grpcClientInterceptor() {
            return new GrpcClientInterceptor();
        }
        @Bean
        public GlobalClientInterceptorConfigurer globalInterceptorConfigurerAdapter() {
            return registry -> registry.addClientInterceptors(grpcClientInterceptor());
        }
    }

    @ConditionalOnProperty(name="framew.type", havingValue="dubbo")
//@EnableDubbo(scanBasePackages = "zhranklin.powerful.dubbo")//不生效
    public static class DubboConfiguration {

        @Value("${framew.zk}")
        public String zk;

        @Value("#{systemProperties['framew.application.name']}")
        //@Value("${framew.app}")
        public String app;

        @Value("${framew.port}")
        public int port;

        @Value("${framew.app.version:0.0.1}")
        public String version;

        @Bean
        public ApplicationConfig applicationConfig() {
            ApplicationConfig application = new ApplicationConfig();
            application.setName(app);
            application.setVersion(version);
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

        @ConditionalOnProperty(name="framew.application.name", havingValue="dubbo-a")
        @DubboComponentScan(basePackages = "zhranklin.powerful.rpc.dubboa")
        public static class DubboA {}

        @ConditionalOnProperty(name="framew.application.name", havingValue="dubbo-b")
        @DubboComponentScan(basePackages = "zhranklin.powerful.rpc.dubbob")
        public static class DubboB {}
    }

}
