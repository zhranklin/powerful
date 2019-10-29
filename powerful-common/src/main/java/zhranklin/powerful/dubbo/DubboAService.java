package zhranklin.powerful.dubbo;

import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;

/**
 * Created by twogoods on 2019/10/29.
 */
public interface DubboAService {
    Object echo(Integer num, Instruction instruction, RenderingContext context);
}
