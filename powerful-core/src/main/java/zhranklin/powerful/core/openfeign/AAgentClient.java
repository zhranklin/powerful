package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@org.springframework.cloud.netflix.feign.FeignClient(name = "a-agent")
@FeignClient(name = "a-agent")
public interface AAgentClient extends CommonClient<AAgentClient> {
}

