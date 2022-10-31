package com.netease.cloud.nsf.webflux.view.web.service.impl;

import com.netease.cloud.nsf.webflux.view.web.service.IStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service("StockServiceImpl")
public class StockServiceImpl implements IStockService {

    private static Logger log = LoggerFactory.getLogger(StockServiceImpl.class);


    @Value("${stock_provider_url}")
    String stockProviderUrl;

    @Value("${stock_advisor_url}")
    String stockAdvisorUrl;

    @Autowired
    WebClient.Builder builder;

    private WebClient webClient = null;

    private WebClient getWebClient(){
        if(webClient == null){
            webClient = builder.build();
        }
        return webClient;
    }
//    @Override
//    public String echoAdvisor(int times) {
//
//    	StringBuilder sBuilder = new StringBuilder();
//    	String url = stockAdvisorUrl + "/echo";
//    	while(times --> 0) {
//            String result;
//            try{
//                result = getWebClient().get().uri(url + "?p=" + times).retrieve().bodyToMono(String.class).toFuture().get(1000, TimeUnit.MILLISECONDS);
//            }catch (Exception e){
//                result = e.getMessage() + "\r\n";
//            }
//
//            sBuilder.append(result);
//    	}
//    	return sBuilder.toString();
//    }

    @Override
    public Mono<String> echoAdvisor(int times) {

        String url = stockAdvisorUrl + "/echo";
            Mono<String> result;
            try{
                result = getWebClient().get().uri(url + "?p=" + times).retrieve().bodyToMono(String.class);
            }catch (Exception e){
                result = Mono.just(e.getMessage() + "\r\n");
            }
            return result;
    }
    
    @Override
    public Mono<String> echoProvider(int times) {

    	//StringBuilder sBuilder = new StringBuilder();
    	String url = stockProviderUrl + "/echo";
    	//while(times --> 0) {
    	    Mono<String> result;
    	    try{
    	        result = getWebClient().get().uri(url + "?p=" + times).retrieve().bodyToMono(String.class);
            }catch (Exception e){
                result = Mono.just(e.getMessage() + "\r\n");
            }

    		//sBuilder.append(result);
    	//}
    	return result;
    }


//    @Override
//    public String echobyecho(int times) {
//        StringBuilder sBuilder = new StringBuilder();
//        String url = stockAdvisorUrl + "/echobyecho";
//        while (times-- > 0) {
//            String result;
//            try{
//                result = getWebClient().get().uri(url + "?p=" + times).retrieve().bodyToMono(String.class).toFuture().get(1000, TimeUnit.MILLISECONDS);
//            }catch (Exception e){
//                result = e.getMessage() + "\r\n";
//            }
//
//            sBuilder.append(result);
//        }
//        return sBuilder.toString();
//    }

    @Override
    public Mono<String> echobyecho(int times) {
        String url = stockAdvisorUrl + "/echobyecho";
            Mono<String> result;
            try{
                result = getWebClient().get().uri(url + "?p=" + times).retrieve().bodyToMono(String.class);
            }catch (Exception e){
                result = Mono.just(e.getMessage() + "\r\n");
            }
        return  result;
    }

}
