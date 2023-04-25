package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "c-dubbo-sidecar")
public interface CDubboSidecarClient extends CommonClient {
}
