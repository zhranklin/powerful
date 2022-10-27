package com.netease.cloud.nsf.webflux.provider.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class StockController {

	private static Logger log = LoggerFactory.getLogger(StockController.class);


	@Value("${spring.application.name:WEBFLUX-PROVIDER}")
	String name;

	@GetMapping("/hi")
	public Mono<String> greeting(ServerHttpRequest request) {

		String host = request.getURI().getHost();
		int port = request.getURI().getPort();

		return Mono.just("greeting from " + name + "[" + host + ":" + port + "]" + System.lineSeparator());
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

}
