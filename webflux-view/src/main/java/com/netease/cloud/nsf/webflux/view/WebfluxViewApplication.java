package com.netease.cloud.nsf.webflux.view;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableFeignClients
public class WebfluxViewApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(WebfluxViewApplication.class, args);;
    }
}
