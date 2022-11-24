package com.netease.cloud.nsf.webflux.provider.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class StockController implements EnvironmentAware {

	private static Logger log = LoggerFactory.getLogger(StockController.class);
	private Environment environment;
	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}


	@Value("${spring.application.name:WEBFLUX-PROVIDER}")
	String name;

	@GetMapping("/hi")
	public Mono<String> greeting(ServerHttpRequest request) {

		String host = address.getHostAddress();
		return Mono.just("greeting from " + name + "[" + host + ":" + port + "(" + mode + ",color:" + color + ")]" + System.lineSeparator());
	}

	@GetMapping("/echo")
	public Mono<String> echo(ServerHttpRequest request) {
		log.info(" echo provider invoked");
		String host = request.getURI().getHost();
		int port = request.getURI().getPort();

		return Mono.just("echo from " + name + "[" + host + ":" + port + "]" + System.lineSeparator());
	}

	@GetMapping("/echobyecho")
	public Mono<String> echobyecho(ServerHttpRequest request) {
		String host = request.getURI().getHost();
		int port = request.getURI().getPort();
		String color = request.getHeaders().getFirst("X-NSF-COLOR");
		StringBuilder sb = new StringBuilder(" meta[没有捏");
		sb.append("]");
		return Mono.just("echo from " + name + "[" + host + ":" + port + "] color:" + color + sb.toString());
	}


	@PostMapping("echoPost")
	public Mono<Object> echoPost(@RequestBody Object obj) {
		return Mono.just(obj);
	}

	@GetMapping("/health")
	@ResponseBody
	public Mono<String> health() {
		return Mono.just("I am good!");
	}

	/**
	 * 熔断测试
	 */
	int count = 0;
	@GetMapping("/sleepgw")
	public Mono<String> sleepgw(ServerHttpRequest request, String msg)  throws InterruptedException {
		if (count++ % 5 < 3) {
			TimeUnit.SECONDS.sleep(10);
		}
		return Mono.just("第" + count + "次sleepgw,参数:" + msg + ",响应服务地址:" + request.getURI().getHost() + ":" + request.getURI().getPort());
	}

	InetAddress address;

	{
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Value("${server.port}")
	int port;
	private String mode = System.getProperty("mode", "unknow");
	private String color = System.getProperty("color", "");

	@GetMapping("/content")
	public String content(String content, int delay) {
		log.info(" provider content executed");
		try {
			TimeUnit.MILLISECONDS.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if ("error".equals(content)) {
			throw new RuntimeException(content);
		}
		if ("rand".equals(content)) {
			if (count++ % 3 == 0) {
				throw new RuntimeException(content);
			}
		}
		String host = address.getHostAddress();
		return content + " from " + name + "[" + host + ":" + port + "(" + mode + ",color:" + color + ")]" + System.lineSeparator();
	}

	@GetMapping(value = "/getConfigs")
	@ResponseBody
	public String getConfig(@RequestParam String key) {
		return environment.getProperty(key);
	}

}
