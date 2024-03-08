package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "c-agent")
public interface CAgentClient extends CommonClient<AAgentClient> {
}
