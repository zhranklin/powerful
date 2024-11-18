package zhranklin.powerful.core;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.reactive.function.client.WebClient;
import zhranklin.powerful.assist.Gen;
import zhranklin.powerful.assist.RPCControllerAspect;
import zhranklin.powerful.core.cases.StaticResources;
import zhranklin.powerful.core.invoker.*;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.core.service.TestingMethodService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Created by 张武 at 2019/9/24
 */
@Configuration
@ComponentScan(basePackages = {"zhranklin.powerful.core.controllers"})
public class PowerfulAutoConfiguration {

    private final static Logger logger = LoggerFactory.getLogger(PowerfulAutoConfiguration.class);

    @Value("${configPath:/etc/powerful-cases/config.yaml}")
	String configPath;

    // 目前HTTP客户端支持两种：RestTemplate 和 HttpClient
    @Value("${defaultHttpClient:RestTemplate}")
    String defaultHttpClient;

    @Bean
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    public FilterRegistrationBean filterRegistSpringBoot3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.info("ConditionalOnClass jakarta.servlet.Filter.");
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        jakarta.servlet.Filter filterSpringboot3 = new jakarta.servlet.Filter() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest servletRequest, jakarta.servlet.ServletResponse servletResponse, jakarta.servlet.FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
                servletRequest.setAttribute("realBody", StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8));
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            public void init(jakarta.servlet.FilterConfig filterConfig) {
            }

            @Override
            public void destroy() {
            }
        };
        Method setFilterMethod = FilterRegistrationBean.class.getDeclaredMethod("setFilter", jakarta.servlet.Filter.class);
        setFilterMethod.setAccessible(true);
        setFilterMethod.invoke(frBean, filterSpringboot3);
        frBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        frBean.addUrlPatterns("/y");
        frBean.addUrlPatterns("/b");
        return frBean;
    }

    @Bean
    @ConditionalOnMissingClass("jakarta.servlet.Filter")
    public FilterRegistrationBean filterRegist() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        logger.info("ConditionalOnMissingClass jakarta.servlet.Filter.");
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        javax.servlet.Filter filter = new javax.servlet.Filter() {
            @Override
            public void doFilter(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse, javax.servlet.FilterChain filterChain) throws IOException, javax.servlet.ServletException {
                servletRequest.setAttribute("realBody", StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8));
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            public void init(javax.servlet.FilterConfig filterConfig) {
            }

            @Override
            public void destroy() {
            }
        };
        Method setFilterMethod = FilterRegistrationBean.class.getDeclaredMethod("setFilter", javax.servlet.Filter.class);
        setFilterMethod.setAccessible(true);
        setFilterMethod.invoke(frBean, filter);
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
                                    WebClientRemoteInvoker webClientRemoteInvoker, TestingMethodService testingMethodService) {
        PowerfulService powerful = new PowerfulService(stringRenderer(), testingMethodService);
        if ("restTemplate".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", httpRestTemplateRemoteInvoker);
        } else if ("httpClient".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", httpClientRemoteInvoker);
        } else if ("httpClient5".equalsIgnoreCase(defaultHttpClient)) {
            powerful.setInvoker("http", httpClient5RemoteInvoker);
		} else if ("webflux".equalsIgnoreCase(defaultHttpClient)) {
			powerful.setInvoker("http", webClientRemoteInvoker);
        } else {
            throw new IllegalStateException(String.format("Default HTTP Client not supported: '%s'", defaultHttpClient));
        }
        powerful.setInvoker("dubbo", dubbo);
		powerful.setInvoker("webflux", webClientRemoteInvoker);
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
    WebClientRemoteInvoker webClientRemoteInvoker(@Qualifier("stringRenderer") StringRenderer stringRenderer, WebClient webClient) {
        return new WebClientRemoteInvoker(stringRenderer, webClient);
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
	WebClient webClient() {
		return WebClient.create();
	}

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
