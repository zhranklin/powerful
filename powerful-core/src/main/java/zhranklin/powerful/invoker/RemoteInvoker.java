package zhranklin.powerful.invoker;

import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;

/**
 * Created by 张武 at 2019/12/2
 */
public interface RemoteInvoker {
	Object invoke(Instruction instruction, RenderingContext context);
}
