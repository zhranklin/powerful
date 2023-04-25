package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "c-sidecar")
public interface CSidecarClient extends CommonClient {
}
