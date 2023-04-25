package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "a-sidecar")
public interface ASidecarClient extends CommonClient {
}
