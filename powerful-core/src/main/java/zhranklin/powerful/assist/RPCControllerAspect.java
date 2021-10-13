package zhranklin.powerful.assist;

import com.alibaba.dubbo.rpc.RpcContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.core.invoker.RPCInvokeContext;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;
import zhranklin.powerful.model.RenderingContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by 张武 at 2021/10/13
 */
@Aspect
public class RPCControllerAspect {

	@Pointcut("within(@zhranklin.powerful.assist.PowerfulRPCTemplate *)")
	public void beanAnnotated() {}

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {}

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * dubbo的真正入口
	 * @param jp
	 * @return
	 * @throws Throwable
	 */
	@Around("beanAnnotated() && publicMethod())")
	public Object handleDubboRequest(ProceedingJoinPoint jp) throws Throwable {
		RenderingContext context = new RenderingContext();
		RPCInvokeContext.renderingContext.set(context);

		Instruction instruction = null;
		try {
			RpcContext clientContext = RpcContext.getContext();
			clientContext.getAttachments().forEach(context.getRequestHeaders()::put);
			Object[] args = jp.getArgs();
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				if (arg instanceof Instruction) {
					instruction = (Instruction) arg;
					context.getParams().put(""+i, "<Instruction>");
				} else {
					context.getParams().put(""+i, arg instanceof String ? (String) arg : mapper.writeValueAsString(arg));
				}
			}
			String argsForLog = IntStream.range(0, args.length).mapToObj(String::valueOf).map(context.getParams()::get).collect(Collectors.joining(","));
			System.out.printf("### Invoking %s.%s(%s)\n", jp.getSignature().getDeclaringType().getSimpleName(), jp.getSignature().getName(), argsForLog);
			Object result = jp.proceed();
			PowerfulResponse res = context.getResult();
			//todo 根据statusCode抛异常等
			return result;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		} finally {
			RPCInvokeContext.renderingContext.set(null);
			RpcContext serverContext = RpcContext.getServerContext();
			if (instruction != null) {
				instruction.getResponseHeaders().forEach(serverContext::setAttachment);
			}
		}
	}

}
