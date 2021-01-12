package zhranklin.powerful.core.invoker;

import zhranklin.powerful.dubbo.DubboAService;
import zhranklin.powerful.dubbo.DubboBService;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.core.service.PowerfulService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by twogoods on 2019/10/29.
 */
public class DubboRemoteInvoker implements RemoteInvoker {

    private static final Logger logger = LoggerFactory.getLogger(PowerfulService.class);

    protected DubboAService dubboAService;

    protected DubboBService dubboBService;

    protected PowerfulService powerful;

    public void setPowerful(PowerfulService powerful) {
        this.powerful = powerful;
    }

    public DubboRemoteInvoker() {
    }

    public DubboRemoteInvoker(DubboAService dubboAService, DubboBService dubboBService) {
        this.dubboAService=dubboAService;
        this.dubboBService=dubboBService;
    }


    public Object invoke(Instruction instruction, RenderingContext context) {
        String num = instruction.getQueries().get("num");
        int param = 0;
        try {
            param = Integer.parseInt(num);
        } catch (Exception e) {
            logger.warn("query param num is illegal");
        }

        String beanName = instruction.getCall();
        if (beanName.equals("dubboAService")) {
            return dubboAService.echo(param, instruction.getTo(), context);
        } else if (beanName.equals("dubboBService")) {
            return dubboBService.echo(param, instruction.getTo(), context);
        }
        return "unknow service";
    }
}
