package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@org.springframework.cloud.netflix.feign.FeignClient(name = "c-agent")
@FeignClient(name = "c-agent")
public interface CAgentClient extends CommonClient<AAgentClient> {
}
