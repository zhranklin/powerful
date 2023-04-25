package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "b-sidecar")
public interface BSidecarClient extends CommonClient {
}
