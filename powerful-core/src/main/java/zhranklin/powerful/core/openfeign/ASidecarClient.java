package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@org.springframework.cloud.netflix.feign.FeignClient(name = "a-sidecar")
@FeignClient(name = "a-sidecar")
public interface ASidecarClient extends CommonClient {
}
