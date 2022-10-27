package com.netease.cloud.nsf.webflux.view.web.service.impl;

import com.netease.cloud.nsf.webflux.view.web.service.AdvisorFeignStockService;
import com.netease.cloud.nsf.webflux.view.web.service.IStockService;
import com.netease.cloud.nsf.webflux.view.web.service.ProviderFeignStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("StockFeignServiceImpl")
public class StockFeignServiceImpl implements IStockService {

    @Autowired
    private AdvisorFeignStockService advisorFeignStockService;
    @Autowired
    private ProviderFeignStockService providerFeignStockService;




    @Override
    public String echoAdvisor(int times) {
        StringBuilder sBuilder = new StringBuilder();
        while (times-- > 0) {
            sBuilder.append(advisorFeignStockService.echoAdvisor(times));
        }
        return sBuilder.toString();
    }

    @Override
    public String echoProvider(int times) {
        StringBuilder sBuilder = new StringBuilder();
        while (times-- > 0) {
            sBuilder.append(providerFeignStockService.echoProvider(times));
        }
        return sBuilder.toString();
    }


    @Override
    public String echobyecho(int times) {
        StringBuilder sBuilder = new StringBuilder();
        while (times-- > 0) {
            sBuilder.append(advisorFeignStockService.echobyecho(times));
        }
        return sBuilder.toString();
    }

}
