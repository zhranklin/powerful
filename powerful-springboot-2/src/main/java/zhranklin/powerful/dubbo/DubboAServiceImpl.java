package zhranklin.powerful.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import zhranklin.powerful.invoker.DubboRemoteInvoker;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

/**
 * Created by twogoods on 2019/10/29.
 */
@Service
public class DubboAServiceImpl extends DubboRemoteInvoker implements DubboAService, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(DubboAServiceImpl.class);

    @Reference(check = false)
    private DubboBService bService;

    @PostConstruct
    public static void init() {
        System.out.println("init...");
    }

    @Override
    public Object echo(Integer num, Instruction instruction, RenderingContext context) {
        logger.info("in DubboAServiceImpl, num: " + num);
        return powerful.execute(instruction, context);
    }

    public Object fallback(Integer num, Instruction instruction, RenderingContext context) {
        return "fallback";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.dubboBService = bService;
    }
}
