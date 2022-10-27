package zhranklin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gate")
public class testController {
    @GetMapping("/string/{s}")
    public Mono<String> getString(@PathVariable String s) {
        return Mono.just("gate " + s);
    }
//    @GetMapping("/string2/{s}")
//    public Mono<String> getString2(@PathVariable String s) {
//        return Mono.just("YOU SHALL DIE " + s);
//    }
}
