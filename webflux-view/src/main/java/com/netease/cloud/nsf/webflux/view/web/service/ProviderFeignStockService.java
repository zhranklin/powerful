package com.netease.cloud.nsf.webflux.view.web.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//@FeignClient(name = "webflux-provider", url = "http://127.0.0.1:5201")
@FeignClient(name = "webflux-provider")
public interface ProviderFeignStockService {

    @RequestMapping(method = RequestMethod.GET, value = "/echo")
    public String echoProvider(@RequestParam("p") int p);

    @RequestMapping(method = RequestMethod.GET, value = "/echobyecho")
    public String echobyecho(@RequestParam("p") int p);
}