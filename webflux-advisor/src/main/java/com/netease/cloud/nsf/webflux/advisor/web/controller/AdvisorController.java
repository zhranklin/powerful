package com.netease.cloud.nsf.webflux.advisor.web.controller;



import com.netease.cloud.nsf.webflux.advisor.web.service.IAdvisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;

@RestController
public class AdvisorController {

	private static Logger log = LoggerFactory.getLogger(AdvisorController.class);
	
	@Autowired
	IAdvisorService advisorService;


	
	@GetMapping("/hi")
	public Mono<String > greeting() {
		return advisorService.batchHi();
	}
	
	@Value("${spring.application.name}")
	String name;

	@GetMapping("/echo")
	public Mono<String> echo(ServerHttpRequest request) {
		log.info("echo advisor invoked");
		String host = request.getLocalAddress().getHostString();
		int port = request.getLocalAddress().getPort();
		
		return Mono.just("echo from " + name + "[" + host + ":" + port + "]" + System.lineSeparator());
	}


	@GetMapping("/echobyecho")
	public Flux<String> echobyecho(ServerHttpRequest request) {
		String host = request.getLocalAddress().getHostString();
		int port = request.getLocalAddress().getPort();
		String color = request.getHeaders().getFirst("X-NSF-COLOR");
		StringBuilder sb = new StringBuilder(" meta[");
		Iterator headerNames = request.getHeaders().keySet().iterator();
		while (headerNames.hasNext()) {
			String header = (String) headerNames.next();
			if (header.startsWith("nsf-biz-")) {
				sb.append(header).append(":").append(request.getHeaders().getFirst(header)).append(" ");
			}
		}
		sb.append("]");
		Mono<String> res = advisorService.echobyecho();
		return Flux.merge(Mono.just("echo from " + name + "[" + host + ":" + port + "] color:" + color + sb.toString() + " | ") , res );
	}


	@GetMapping("/health")
	@ResponseBody
	public Mono<String> health() {
		return Mono.just("I am good!");
	}
	
	@RequestMapping("/deepInvoke")
	@ResponseBody
	public Mono<String> deepInvoke(@RequestParam int times) {
		return advisorService.deepInvoke(times);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public Mono<String> register(@RequestBody String jsonString) {
		return Mono.just("register json :\r\n" + jsonString);
	}

	@Value("${test:hi}")
	String test;

	@GetMapping("/test")
	public Mono<String> TestApollo(){
		return Mono.just(test);
	}

	@Value("${test2}")
	String test2;

	@GetMapping("/test2")
	public Mono<String> TestApollo2(){
		return Mono.just(test2);
	}
	
	@GetMapping("/divide")
	public Mono<String> divide(ServerHttpRequest request) {
		
		return advisorService.divide(request);

		
	}
}
