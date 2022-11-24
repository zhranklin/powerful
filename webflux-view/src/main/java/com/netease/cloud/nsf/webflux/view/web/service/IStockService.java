package com.netease.cloud.nsf.webflux.view.web.service;


import reactor.core.publisher.Mono;

import java.util.List;


public interface IStockService {

	
	//public String echoAdvisor(int times);
	public Mono<String> echoAdvisor(int times);
	
	public Mono<String> echoProvider(int times);

//	public String echobyecho(int times);
	public Mono<String> echobyecho(int times);

	public Mono<String> sendProvider(String content, int delay, String advisorName, String providerName);

}
