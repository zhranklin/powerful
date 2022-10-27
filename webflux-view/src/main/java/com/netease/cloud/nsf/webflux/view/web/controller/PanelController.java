package com.netease.cloud.nsf.webflux.view.web.controller;

import com.netease.cloud.nsf.webflux.view.web.entity.HttpResponse;
import com.netease.cloud.nsf.webflux.view.web.service.impl.StockServiceImpl;
import com.netease.cloud.nsf.webflux.view.web.manager.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

import java.util.UUID;


/**
 * @author Chen Jiahan | chenjiahan@corp.netease.com
 */
@Controller()
public class PanelController {

    private static Logger log = LoggerFactory.getLogger(PanelController.class);

    @Qualifier("StockServiceImpl")
    @Autowired
    StockServiceImpl stockService;


    @GetMapping("/exception")
    @ResponseBody
    public Mono<String> exception(String msg) {
        if(!StringUtils.isEmpty(msg)){
            throw new RuntimeException(msg);
        }
        return Mono.just("no exception");
    }



    @GetMapping("/logs")
    @ResponseBody
    public Mono<HttpResponse> getHttpLog() {
    	return Mono.just(new HttpResponse(LogManager.logs()));
    }
    
    @GetMapping("/logs/clear")
    @ResponseBody
    public Mono<HttpResponse> clearLogs() {
    	LogManager.clear();
    	return Mono.just(new HttpResponse("clear logs success"));
    }
    
    @GetMapping("/echo/advisor")
    @ResponseBody
    public Mono<HttpResponse> echoAdvisor(ServerHttpRequest request,
                                    @RequestParam(name = "time", defaultValue = "10", required = false) int time) {
    	String result = stockService.echoAdvisor(time);
    	LogManager.put(UUID.randomUUID().toString(), result);
    	return Mono.just(new HttpResponse(result));
    }
    
    @GetMapping("/echo/provider")
    @ResponseBody
    public Mono<HttpResponse> echoProvider(ServerHttpRequest request,
    		@RequestParam(name = "time", defaultValue = "10", required = false) int time) {
    	String result = stockService.echoProvider(time);
    	LogManager.put(UUID.randomUUID().toString(), result);
    	return Mono.just(new HttpResponse(result));
    }

    @GetMapping("/echobyecho")
    @ResponseBody
    public Mono<HttpResponse> echobyecho(ServerHttpRequest request,
                                   @RequestParam(name = "time", defaultValue = "10", required = false) int time) {
        String result = stockService.echobyecho(time);
        LogManager.put(UUID.randomUUID().toString(), result);
        return Mono.just(new HttpResponse(result));
    }

    
    @GetMapping("/health")
    @ResponseBody
    public Mono<String> health() {
    	return Mono.just("I am good!");
    }

    
    private Mono<HttpResponse> handleExceptionResponse(Exception e) {
        NsfExceptionUtil.NsfExceptionWrapper nsfException = NsfExceptionUtil.parseException(e);
        log.error(nsfException.getThrowable().getMessage());
        if(nsfException.getType() == NsfExceptionUtil.NsfExceptionType.NORMAL_EXCEPTION){
            return Mono.just(new HttpResponse(nsfException.getThrowable().getMessage()));
        }
        return Mono.just(new HttpResponse(nsfException.getType().getDesc()));
    }

}