package zhranklin.powerful.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
import zhranklin.powerful.core.invoker.HttpClient5RemoteInvoker;
import zhranklin.powerful.core.invoker.HttpClientRemoteInvoker;
import zhranklin.powerful.core.invoker.HttpRestTemplateRemoteInvoker;
import zhranklin.powerful.core.invoker.OpenFeignRemoteInvoker;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.core.service.TestingMethodService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = {"zhranklin.powerful.core.controllers"})
public class PowerfulAutoConfiguration {

    @Value("${configPath:/etc/powerful-cases/config.yaml}")
	String configPath;

    // 目前HTTP客户端支持两种：RestTemplate 和 HttpClient
    @Value("${defaultHttpClient:RestTemplate}")
    String defaultHttpClient;

    // 以下方法是为了解决SpringBoot3.0的兼容问题。编译springboot版本的demo时需要使用以下被注释的代码
//    @Bean
//    public FilterRegistrationBean filterRegistSpringBoot3() {
//        FilterRegistrationBean frBean = new FilterRegistrationBean();
//        frBean.setFilter(new jakarta.servlet.Filter() {
//            @Override
//            public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse, jakarta.servlet.FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
//                servletRequest.setAttribute("realBody", StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8));
//                filterChain.doFilter(servletRequest, servletResponse);
//            }
//            @Override public void init(jakarta.servlet.FilterConfig filterConfig) { }
//            @Override public void destroy() { }
//        });
//        frBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        frBean.addUrlPatterns("/y");
//        frBean.addUrlPatterns("/b");
//        return frBean;
//    }

    // 以下方法是为了解决SpringBoot3.0的兼容问题。编译springboot2.x版本的demo时需要使用以下
    @Bean
    public FilterRegistrationBean filterRegist() {
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setFilter(new javax.servlet.Filter() {
            @Override
            public void doFilter(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse, javax.servlet.FilterChain filterChain) throws IOException, javax.servlet.ServletException {
                servletRequest.setAttribute("realBody", StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8));
                filterChain.doFilter(servletRequest, servletResponse);
            }
            @Override public void init(javax.servlet.FilterConfig filterConfig) { }

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
    PowerfulService powerfulService(@Autowired(required = false) DubboRemoteInvoker dubbo, HttpClientRemoteInvoker httpClientRemoteInvoker,
                                    HttpRestTemplateRemoteInvoker httpRestTemplateRemoteInvoker, HttpClient5RemoteInvoker httpClient5RemoteInvoker,
                                    OpenFeignRemoteInvoker openFeignRemoteInvoker, TestingMethodService testingMethodService) {
        PowerfulService powerful = new PowerfulService(stringRenderer(), testingMethodService);
        if ("restTemplate".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", httpRestTemplateRemoteInvoker);
        } else if ("httpClient".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", httpClientRemoteInvoker);
        } else if ("httpClient5".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", httpClient5RemoteInvoker);
        }else if ("openfeign".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", openFeignRemoteInvoker);
        } else {
            throw new IllegalStateException(String.format("Default HTTP Client not supported: '%s'", defaultHttpClient));
        }
        powerful.setInvoker("dubbo", dubbo);
        powerful.setInvoker("openfeign", openFeignRemoteInvoker);
        powerful.setInvoker("http(restTemplate)".toLowerCase(Locale.ENGLISH), httpRestTemplateRemoteInvoker);
        powerful.setInvoker("http(httpClient)".toLowerCase(Locale.ENGLISH), httpClientRemoteInvoker);
        powerful.setInvoker("http(httpClient5)".toLowerCase(Locale.ENGLISH), httpClient5RemoteInvoker);
        if (dubbo != null) {
            dubbo.setPowerful(powerful);
        }
        return powerful;
    }

    @Bean
    HttpClientRemoteInvoker httpClientRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer) {
        return new HttpClientRemoteInvoker(stringRenderer);
    }

    @Bean
    HttpRestTemplateRemoteInvoker httpRestTemplateRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer, RestTemplate restTemplate) {
        return new HttpRestTemplateRemoteInvoker(stringRenderer, restTemplate);
    }

    @Bean
    HttpClient5RemoteInvoker httpClient5RemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer) {
        return new HttpClient5RemoteInvoker(stringRenderer);
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
    @ConditionalOnClass(jakarta.servlet.http.HttpServletRequest.class)
    RestTemplate restTemplateSpringboot3() {
        return new RestTemplate() {{
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(@jakarta.annotation.Nullable ClientHttpResponse clientHttpResponse) {
                    return true;
                }

                @Override
                public void handleError(@jakarta.annotation.Nullable ClientHttpResponse clientHttpResponse) {

                }
            });
        }};
    }

    @Bean
    @ConditionalOnMissingClass("jakarta.servlet.http.HttpServletRequest")
    RestTemplate restTemplate() {
        return new RestTemplate() {{
            setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(@javax.annotation.Nullable ClientHttpResponse clientHttpResponse) {
                    return true;
                }

                @Override
                public void handleError(@javax.annotation.Nullable ClientHttpResponse clientHttpResponse) {

                }
            });
        }};
    }

    @Bean
    OpenFeignRemoteInvoker openFeignRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer) {return new OpenFeignRemoteInvoker(stringRenderer);}

    @Bean
    Jackson2ObjectMapperBuilderCustomizer objectMapper() {
        return builder -> builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT, DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @ConditionalOnProperty(name="powerful.dubbo.enabled", havingValue="true")
    @Configuration
    @EnableDubboConfig
    @ConditionalOnClass(org.apache.dubbo.config.spring.context.annotation.DubboComponentScan.class)
    public static class DubboConfiguration27x {

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

    @ConditionalOnProperty(name = "powerful.dubbo.enabled", havingValue = "true")
    @Configuration
    @com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig
    @ConditionalOnClass(com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan.class)
    public static class DubboConfiguration26X {

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
        @com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan(basePackages = Gen.GEN_PACKAGE)
        public static class DubboGen {
        }
    }

}
