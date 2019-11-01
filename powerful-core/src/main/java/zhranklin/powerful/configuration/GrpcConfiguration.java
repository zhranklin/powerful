package zhranklin.powerful.configuration;

import zhranklin.powerful.service.GrpcClientInterceptor;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "framew.type", havingValue = "grpc")
@ComponentScan(basePackages = {"zhranklin.powerful.grpc"})
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
