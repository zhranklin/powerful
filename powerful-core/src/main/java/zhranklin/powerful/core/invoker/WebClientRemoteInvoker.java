package zhranklin.powerful.core.invoker;

import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;

import java.net.URI;
import java.util.Map;

public class WebClientRemoteInvoker extends HttpRemoteAbstractInvoker {

    private final WebClient webClient;

    public WebClientRemoteInvoker(StringRenderer stringRenderer, WebClient webClient) {
        super(stringRenderer);
        this.webClient = webClient;
    }

    @Override
    public PowerfulResponse doInvoke(Map<String, String> headers, String url, String method, Instruction body) {
        return PowerfulResponse.fromHttp(webClient.method(HttpMethod.valueOf(method))
                 .uri(URI.create(url)).body(Mono.just(body), Instruction.class)
                 .headers(newHeaders -> {
                     for (Map.Entry<String, String> entry : headers.entrySet()) {
                         newHeaders.add(entry.getKey(), entry.getValue());
                     }
                     newHeaders.add("powerful-is-webflux", "true");
                 })
                .exchange()
                .block());
    }
}
