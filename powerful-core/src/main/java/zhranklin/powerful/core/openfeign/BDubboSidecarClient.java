package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "b-dubbo-sidecar")
public interface BDubboSidecarClient extends CommonClient {
}
