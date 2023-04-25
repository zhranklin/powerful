package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "b-dubbo-agent")
public interface BDubboAgentClient extends CommonClient<BDubboAgentClient> {
}
