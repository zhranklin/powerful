package zhranklin.powerful;

import zhranklin.powerful.invoker.DubboRemoteInvoker;
import zhranklin.powerful.invoker.GrpcRemoteInvoker;
import zhranklin.powerful.invoker.HttpRemoteInvoker;
import zhranklin.powerful.service.PowerfulService;
import zhranklin.powerful.service.StringRenderer;
import org.springframework.beans.factory.annotation.Autowired;
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
}
