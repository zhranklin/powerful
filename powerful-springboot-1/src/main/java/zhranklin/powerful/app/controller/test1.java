package zhranklin.powerful.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class test1 {
//    @RequestMapping(method = RequestMethod.GET, path = "/test/1")
//    public Object return200(){
//        return String.valueOf(111);
//    }

    @GetMapping("/string/{s}")
    public Mono<String> getString(@PathVariable String s) {
        return Mono.just("YOU SHALL DIE " + s);
    }
    @GetMapping("/string2/{s}")
    public Mono<String> getString2(@PathVariable String s) {
        return Mono.just("YOU SHALL DIE " + s);
    }
}
