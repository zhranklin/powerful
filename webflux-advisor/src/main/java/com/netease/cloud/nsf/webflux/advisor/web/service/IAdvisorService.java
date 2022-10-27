package com.netease.cloud.nsf.webflux.advisor.web.service;



import org.springframework.http.server.reactive.ServerHttpRequest;

import java.util.List;

public interface IAdvisorService {
	
	public List<String> batchHi();
	
	public String deepInvoke(int times);

	public String echobyecho();

	public String divide(ServerHttpRequest request);

}
