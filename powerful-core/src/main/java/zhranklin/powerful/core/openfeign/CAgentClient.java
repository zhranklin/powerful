package zhranklin.powerful.core.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import zhranklin.powerful.model.Instruction;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@FeignClient(name = "c-agent")
public interface CAgentClient extends CommonClient<AAgentClient> {
}
