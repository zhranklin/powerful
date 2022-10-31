package com.netease.cloud.nsf.webflux.advisor.web.service.impl;

import com.netease.cloud.nsf.webflux.advisor.web.service.IAdvisorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class AdvisorServiceImpl implements IAdvisorService {

	private static Logger log = LoggerFactory.getLogger(AdvisorServiceImpl.class);

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	WebClient.Builder builder;


	@Value("${hot_stock_ids}")
	String hotStockIds;

	@Value("${stock_provider_url}")
	String stockProviderUrl;
	
	@Value("${stock_viewer_url}")
	String stockViewerrUrl;

	@Value("${error:true}")
	boolean errorSwitch;

	private int retryCount = -1;


	/**
	 * @return  ids separated by comma 
	 *  e.g. xx,yy,zz
	 */
	private String getRecommendStockIds() {
		return hotStockIds;
	}

	@Override
	public List<String> batchHi() {
		
		List<String> results = new ArrayList<>();
		
		for(int i = 0; i < 20; i++) {
			String result;
			try{
				result = builder.build().get().uri(stockProviderUrl + "/hi?p=" + i).retrieve().bodyToMono(String.class).toFuture().get(1000, TimeUnit.MILLISECONDS);
			}catch (Exception e){
				result = e.getMessage() + "\r\n";
			}
			results.add(result);
		}
		return results;
	}

	@Override
	public String deepInvoke(int times) {
		
		if(times --> 0) {
			String result;
			try{
				result = builder.build().get().uri(stockViewerrUrl + "/deepInvoke?times=" + times).retrieve().bodyToMono(String.class).toFuture().get(1000, TimeUnit.MILLISECONDS);
			}catch (Exception e){
				result = e.getMessage() + "\r\n";
			}

			return result;
		} 
		return "finish";
	}

	@Override
	public String echobyecho() {
		StringBuilder sBuilder = new StringBuilder();
		String url = stockProviderUrl + "/echobyecho";
		String result;
		try{
			result = builder.build().get().uri(url).retrieve().bodyToMono(String.class).toFuture().get(1000, TimeUnit.MILLISECONDS);
		}catch (Exception e){
			result = e.getMessage() + "\r\n";
		}
		sBuilder.append(result);
		return sBuilder.toString();
	}

	@Override
	public String divide(ServerHttpRequest request) {
		// TODO Auto-generated method stub
        List<String> results = new ArrayList<>();
       
        HttpHeaders headers = new HttpHeaders();
        Iterator headerNames = request.getHeaders().keySet().iterator();
        String headerName = null;
        while(headerNames.hasNext()){
        	headerName = (String) headerNames.next();
        	headers.add(headerName, request.getHeaders().getFirst(headerName));
       
        	log.info("headerName = " + headerName + " value = " + request.getHeaders().getFirst(headerName));

        }
        HttpEntity<MultiValueMap<String, String>>  entity =  new HttpEntity<MultiValueMap<String, String>>(null,headers);
		Consumer<HttpHeaders> headersConsumer = httpHeaders -> {
			headers.forEach((key, value) -> httpHeaders.add(key, value.get(0)));
		};
		String result = "异步调用失败了，得看看代码";
		try{
			result = builder.build().get().uri(stockProviderUrl + "/hi?"+request.getURI().getQuery()).headers(headersConsumer).retrieve().bodyToMono(String.class).toFuture().get(2000, TimeUnit.MILLISECONDS);
		}catch (Exception e){

		}

		
		return result;
	}

	
}
