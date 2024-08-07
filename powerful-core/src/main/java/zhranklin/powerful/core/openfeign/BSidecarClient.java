package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@org.springframework.cloud.netflix.feign.FeignClient(name = "b-sidecar")
@FeignClient(name = "b-sidecar")
public interface BSidecarClient extends CommonClient {
}
