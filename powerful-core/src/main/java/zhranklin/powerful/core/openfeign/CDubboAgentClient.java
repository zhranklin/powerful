package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "c-dubbo-agent")
public interface CDubboAgentClient extends CommonClient<CDubboAgentClient> {
}
