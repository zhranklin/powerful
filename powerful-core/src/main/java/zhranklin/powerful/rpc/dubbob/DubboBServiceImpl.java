package zhranklin.powerful.rpc.dubbob;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import zhranklin.powerful.core.invoker.DubboRemoteInvoker;
import zhranklin.powerful.dubbo.DubboAService;
import zhranklin.powerful.dubbo.DubboBService;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by twogoods on 2019/10/29.
 */
@Service
public class DubboBServiceImpl extends DubboRemoteInvoker implements DubboBService, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DubboBServiceImpl.class);

    @Reference(check = false)
    private DubboAService aService;

    @Override
    public Object echo(Integer num, Instruction instruction, RenderingContext context) {
        logger.info("in DubboBServiceImpl, num: " + num);
        return powerful.execute(instruction, context);
    }

    @Override
    public void afterPropertiesSet() {
        super.dubboAService = aService;
    }
}
