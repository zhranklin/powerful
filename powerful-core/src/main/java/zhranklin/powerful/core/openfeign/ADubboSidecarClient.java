package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "a-dubbo-sidecar")
public interface ADubboSidecarClient extends CommonClient {
}
