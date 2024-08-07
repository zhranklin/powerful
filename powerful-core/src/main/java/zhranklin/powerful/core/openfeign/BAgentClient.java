package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@org.springframework.cloud.netflix.feign.FeignClient(name = "b-agent")
@FeignClient(name = "b-agent")
public interface BAgentClient extends CommonClient<AAgentClient> {
}
