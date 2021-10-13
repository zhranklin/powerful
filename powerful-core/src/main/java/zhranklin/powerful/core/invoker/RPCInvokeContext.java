package zhranklin.powerful.core.invoker;

import zhranklin.powerful.model.RenderingContext;

/**
 * Created by 张武 at 2021/10/13
 */
public class RPCInvokeContext {
	public static final ThreadLocal<RenderingContext> renderingContext = new ThreadLocal<>();
}
