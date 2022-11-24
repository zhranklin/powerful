package com.netease.cloud.nsf.webflux.advisor.web.service;



import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IAdvisorService {
	
//	public List<String> batchHi();
//
//	public String deepInvoke(int times);
//
//	public String echobyecho();
//
//	public String divide(ServerHttpRequest request);
    public Mono<String> batchHi();

	public Mono<String> deepInvoke(int times);

	public Mono<String> echobyecho();

	public Mono<String> divide(ServerHttpRequest request);

	public Mono<String> sendProvider(String content, int delay, String providerName);

}
