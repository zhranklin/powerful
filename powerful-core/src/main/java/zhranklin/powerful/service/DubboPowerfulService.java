package zhranklin.powerful.service;

import com.alibaba.dubbo.config.annotation.Reference;
import zhranklin.powerful.dubbo.DubboAService;
import zhranklin.powerful.dubbo.DubboBService;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Created by twogoods on 2019/10/29.
 */
public class DubboPowerfulService extends AbstractPowerfulService {

    private static Logger logger = LoggerFactory.getLogger(AbstractPowerfulService.class);

    private ApplicationContext applicationContext;

    protected DubboAService dubboAService;

    protected DubboBService dubboBService;

    public DubboPowerfulService(StringRenderer stringRenderer, ApplicationContext applicationContext) {
        super(stringRenderer);
        this.applicationContext = applicationContext;
    }

    public DubboPowerfulService(StringRenderer stringRenderer, ApplicationContext applicationContext, DubboAService dubboAService, DubboBService dubboBService) {
        super(stringRenderer);
        this.applicationContext = applicationContext;
        this.dubboAService=dubboAService;
        this.dubboBService=dubboBService;
    }


    public Object remoteCall(Instruction instruction, RenderingContext context) {
        String num = instruction.getWithQuerys().get("num");
        int param = 0;
        try {
            param = Integer.parseInt(num);
        } catch (Exception e) {
            logger.warn("query param num is illegal");
        }

        String beanName = instruction.getTell();
        if (beanName.equals("dubboAService")) {
            return dubboAService.echo(param, instruction.getTo(), context);
        } else if (beanName.equals("dubboBService")) {
            return dubboBService.echo(param, instruction.getTo(), context);
        }
        return null;
    }
}
