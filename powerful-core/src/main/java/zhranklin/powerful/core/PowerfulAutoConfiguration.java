package zhranklin.powerful.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import zhranklin.powerful.assist.Gen;
import zhranklin.powerful.assist.RPCControllerAspect;
import zhranklin.powerful.core.cases.StaticResources;
import zhranklin.powerful.core.invoker.DubboRemoteInvoker;
import zhranklin.powerful.core.invoker.GrpcRemoteInvoker;
import zhranklin.powerful.core.invoker.HttpRemoteInvoker;
import zhranklin.powerful.core.service.GrpcClientInterceptor;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.core.service.TestingMethodService;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = {"zhranklin.powerful.core.controllers"})
public class PowerfulAutoConfiguration {

    @Value("${configPath:/etc/powerful-cases/config.yaml}")
	String configPath;

    @Bean
    public FilterRegistrationBean<Filter> filterRegist() {
        FilterRegistrationBean<Filter> frBean = new FilterRegistrationBean<>();
        frBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                servletRequest.setAttribute("realBody", StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8));
                filterChain.doFilter(servletRequest, servletResponse);
            }
            @Override public void init(FilterConfig filterConfig) { }
            @Override public void destroy() { }
        });
        frBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        frBean.addUrlPatterns("/y");
        frBean.addUrlPatterns("/b");
        return frBean;
    }

    @Bean
    StringRenderer stringRenderer() {
        return new StringRenderer();
    }

    @Bean
    PowerfulService powerfulService(HttpRemoteInvoker http, @Autowired(required = false) DubboRemoteInvoker dubbo) {
        PowerfulService powerful = new PowerfulService(stringRenderer());
        powerful.setInvoker("http", http);
        powerful.setInvoker("dubbo", dubbo);
        if (dubbo != null) {
            dubbo.setPowerful(powerful);
        }
        return powerful;
    }

    @Bean
    TestingMethodService testingMethodService() {
        return new TestingMethodService();
    }

    @Bean
    StaticResources staticResources() {
        return new StaticResources(configPath);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate() {{
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(@Nullable ClientHttpResponse clientHttpResponse) {
                    return true;
                }

                @Override
                public void handleError(@Nullable ClientHttpResponse clientHttpResponse) {

                }
            });
        }};
    }

    @Bean
    HttpRemoteInvoker httpRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer, RestTemplate restTemplate) {
        return new HttpRemoteInvoker(stringRenderer, restTemplate);
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapper() {
        return builder -> builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT, DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @ConditionalOnProperty(name = "powerful.grpc.enabled", havingValue = "true")
    @ComponentScan(basePackages = {"zhranklin.powerful.rpc.grpc"})
    public static class GrpcConfiguration {

        @Bean
        public GrpcClientInterceptor grpcClientInterceptor() {
            return new GrpcClientInterceptor();
        }
        @Bean
        public GlobalClientInterceptorConfigurer globalInterceptorConfigurerAdapter() {
            return registry -> registry.addClientInterceptors(grpcClientInterceptor());
        }

        @PostConstruct
        public void init(@Autowired PowerfulService powerful, @Autowired(required = false) GrpcRemoteInvoker grpc) throws IOException {
            powerful.setInvoker("grpc", grpc);
            if (grpc != null) {
                grpc.setPowerful(powerful);
            }
        }

    }

    @ConditionalOnClass(name="net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle")
    @ConditionalOnProperty(name="powerful.grpc.enabled", havingValue = "false", matchIfMissing = true)
    public static class GrpcDisableConfig {
        @Bean
        public GrpcServerLifecycle grpcServerLifecycle() {
            return new GrpcServerLifecycle(null) {
                @Override
                public void start() {}
                @Override
                public void stop() {}
                @Override
                public void stop(Runnable callback) {}
                @Override
                public boolean isRunning() {
                    return true;
                }
                @Override
                public boolean isAutoStartup() {
                    return true;
                }
            };
        }
    }

    @ConditionalOnProperty(name="powerful.dubbo.enabled", havingValue="true")
    @Configuration
    @EnableDubboConfig
    public static class DubboConfiguration {

        @Value("${powerful.dubbo.zk}")
        public String zk;

        public String app = System.getenv("APP");

        @Value("${powerful.dubbo.port}")
        public int port;

        @Bean
        public ApplicationConfig applicationConfig() {
            ApplicationConfig application = new ApplicationConfig();
            application.setName(app);
            application.setVersion(System.getenv("VERSION"));
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
        public DubboRemoteInvoker dubboRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer) {
            return new DubboRemoteInvoker(stringRenderer);
        }

        @Bean
        public RPCControllerAspect rpcControllerAspect() {
            return new RPCControllerAspect();
        }

        @Configuration
        @ComponentScan(basePackages = Gen.GEN_PACKAGE)
        @DubboComponentScan(basePackages = Gen.GEN_PACKAGE)
        public static class DubboGen {}
    }

}
