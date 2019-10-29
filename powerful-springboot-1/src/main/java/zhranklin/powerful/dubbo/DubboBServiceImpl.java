package zhranklin.powerful.dubbo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.service.DubboPowerfulService;
import zhranklin.powerful.service.StringRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

/**
 * Created by twogoods on 2019/10/29.
 */
@Service
public class DubboBServiceImpl extends DubboPowerfulService implements DubboBService, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(DubboBServiceImpl.class);

    @Reference(check = false)
    private DubboAService aService;

    public DubboBServiceImpl(StringRenderer stringRenderer, ApplicationContext applicationContext) {
        super(stringRenderer, applicationContext);
    }

    @Override
    public Object echo(Integer num, Instruction instruction, RenderingContext context) {
        logger.info("in DubboBServiceImpl, num: " + num);
        return execute(instruction, context);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.dubboAService = aService;
    }
}
