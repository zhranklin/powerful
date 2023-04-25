package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "a-dubbo-agent")
public interface ADubboAgentClient extends CommonClient<ADubboAgentClient> {
}
