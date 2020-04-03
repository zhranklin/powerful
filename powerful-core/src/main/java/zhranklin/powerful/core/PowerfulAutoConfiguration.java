package zhranklin.powerful.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import zhranklin.powerful.core.cases.RequestCase;
import zhranklin.powerful.core.cases.StaticResources;
import zhranklin.powerful.core.invoker.DubboRemoteInvoker;
import zhranklin.powerful.core.invoker.GrpcRemoteInvoker;
import zhranklin.powerful.core.invoker.HttpRemoteInvoker;
import zhranklin.powerful.core.service.GrpcClientInterceptor;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.core.service.TestingMethodService;
import net.devh.boot.grpc.client.interceptor.GlobalClientInterceptorConfigurer;
import net.devh.boot.grpc.server.serverfactory.GrpcServerLifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = {"zhranklin.powerful.core.controllers"})
public class PowerfulAutoConfiguration {

    @Value("${configPath:/etc/powerful-cases/config.yaml}")
	String configPath;

    @Bean
    public FilterRegistrationBean filterRegist() {
        FilterRegistrationBean<Filter> frBean = new FilterRegistrationBean();
        frBean.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                servletRequest.setAttribute("yamlBody",
                    new ObjectMapper(new YAMLFactory()).readValue(servletRequest.getInputStream(), RequestCase.class));
                filterChain.doFilter(servletRequest, servletResponse);
            }
            @Override public void init(FilterConfig filterConfig) { }
            @Override public void destroy() { }
        });
        frBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        frBean.addUrlPatterns("/y");
        return frBean;
    }

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
        return new StaticResources(configPath);
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
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return mapper;
    }

    @ConditionalOnProperty(name = "powerful.grpc.enabled", havingValue = "true")
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
    public static class DubboConfiguration {

        @Value("${framew.zk}")
        public String zk;

        @Value("dubbo-${powerful.dubbo.name}")
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

        @ConditionalOnProperty(name="powerful.dubbo.name", havingValue="dubbo-a")
        @DubboComponentScan(basePackages = "zhranklin.powerful.rpc.dubboa")
        public static class DubboA {}

        @ConditionalOnProperty(name="powerful.dubbo.name", havingValue="dubbo-b")
        @DubboComponentScan(basePackages = "zhranklin.powerful.rpc.dubbob")
        public static class DubboB {}
    }

}
