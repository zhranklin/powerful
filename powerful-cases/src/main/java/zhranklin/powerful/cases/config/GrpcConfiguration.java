package zhranklin.powerful.cases.config;


import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.service.GrpcClientInterceptor;
import zhranklin.powerful.invoker.GrpcRemoteInvoker;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "grpc", havingValue = "true")
public class GrpcConfiguration {

    @GrpcClient("grpc-a")
    private EchoGrpc.EchoBlockingStub grpcAEchoBlockingStub;
    @GrpcClient("grpc-a")
    private EchoGrpc.EchoStub grpcAEchoStub;

    @GrpcClient("grpc-b")
    private EchoGrpc.EchoBlockingStub grpcBEchoBlockingStub;
    @GrpcClient("grpc-b")
    private EchoGrpc.EchoStub grpcBEchoStub;

    @Bean
    public GrpcClientInterceptor grpcClientInterceptor() {
        return new GrpcClientInterceptor();
    }

    @Bean
    public GlobalClientInterceptorConfigurer globalInterceptorConfigurerAdapter(GrpcClientInterceptor grpcClientInterceptor) {
        return registry -> registry.addClientInterceptors(grpcClientInterceptor());
    }

    @Bean
    public GrpcRemoteInvoker grpcPowerfulService() {
        GrpcRemoteInvoker grpcRemoteInvoker = new GrpcRemoteInvoker();
        grpcRemoteInvoker.setGrpcAEchoStub(grpcAEchoStub);
        grpcRemoteInvoker.setGrpcAEchoBlockingStub(grpcAEchoBlockingStub);
        grpcRemoteInvoker.setGrpcBEchoStub(grpcBEchoStub);
        grpcRemoteInvoker.setGrpcBEchoBlockingStub(grpcBEchoBlockingStub);
        return grpcRemoteInvoker;
    }

}
