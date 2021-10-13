package zhranklin.powerful.dubbo.tmpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.assist.PowerfulRPCTemplate;
import zhranklin.powerful.core.invoker.RPCInvokeContext;
import zhranklin.powerful.core.service.PowerfulService;
import zhranklin.powerful.dubbo.pojo.DubboPojo1;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Service
@Component
@PowerfulRPCTemplate("test")
public class DubboServiceTestImpl implements DubboServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DubboServiceTestImpl.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    PowerfulService powerful;

    @PostConstruct
    public void init() {
        logger.info(String.format("init %s...", getClass().getName()));
    }

    @Override
    public Object echo(Integer n, Instruction instruction) {
        return powerful.execute(instruction, RPCInvokeContext.renderingContext.get());
    }

    @Override
    public Object test(String s, Instruction instruction) {
        return powerful.execute(instruction, RPCInvokeContext.renderingContext.get());
    }

    @Override
    public Object echo(Integer n1, Integer n2, Instruction instruction) {
        return powerful.execute(instruction, RPCInvokeContext.renderingContext.get());
    }

    @Override
    public Object complex(DubboPojo1 body, Instruction instruction) {
        return powerful.execute(instruction, RPCInvokeContext.renderingContext.get());
    }

    @SuppressWarnings("SameReturnValue")
    public Object fallback(Integer num, Instruction instruction, RenderingContext context) {
        return "fallback";
    }

}
