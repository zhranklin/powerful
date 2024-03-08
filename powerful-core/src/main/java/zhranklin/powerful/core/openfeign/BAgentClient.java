package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "b-agent")
public interface BAgentClient extends CommonClient<AAgentClient> {
}
