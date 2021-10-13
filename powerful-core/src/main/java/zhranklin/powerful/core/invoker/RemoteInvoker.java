package zhranklin.powerful.core.invoker;

import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;
import zhranklin.powerful.model.RenderingContext;

/**
 * Created by 张武 at 2019/12/2
 */
public interface RemoteInvoker {
	PowerfulResponse invoke(Instruction instruction, RenderingContext context);
}
