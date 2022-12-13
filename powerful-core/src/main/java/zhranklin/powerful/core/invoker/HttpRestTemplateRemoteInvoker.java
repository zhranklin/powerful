package zhranklin.powerful.core.invoker;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;

import java.net.URI;
import java.util.Map;

public class HttpRestTemplateRemoteInvoker extends HttpRemoteAbstractInvoker {
    private final RestTemplate restTemplate;

    public HttpRestTemplateRemoteInvoker(StringRenderer stringRenderer, RestTemplate restTemplate) {
        super(stringRenderer);
        this.restTemplate = restTemplate;
    }

    @Override
    public PowerfulResponse doInvoke(Map<String, String> headers, String url, String method, Instruction body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::set);
        return PowerfulResponse.fromHttp(restTemplate.exchange(new RequestEntity<>(body, httpHeaders, HttpMethod.valueOf(method), URI.create(url)), String.class));
    }
}
