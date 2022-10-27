package com.netease.cloud.nsf.webflux.view.web.entity;

import java.util.List;

/**
 * @author Weng Yanghui (wengyanghui@corp.netease.com)
 * @version $Id: Const.java, v 1.0 2018/5/16
 */
public class HttpResponse {

    private String message;

    public HttpResponse(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    //    @Override
//    public String toString() {
//        return "Stock [id=" + id + ", name=" + name + ", dailyKLineAddr=" + dailyKLineAddr + ", openingPrice=" + openingPrice
//                + ", closingPrice=" + closingPrice + ", currentPrice=" + currentPrice + ", inPrice=" + inPrice
//                + ", outPrice=" + outPrice + ", topTodayPrice=" + topTodayPrice + ", bottomTodayPrice="
//                + bottomTodayPrice + "]";
//    }

    @Override
    public String toString(){
        return "ssss";
    }
}
