package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "a-agent")
public interface AAgentClient extends CommonClient<AAgentClient> {
}

