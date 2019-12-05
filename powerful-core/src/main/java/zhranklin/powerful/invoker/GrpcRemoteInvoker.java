package zhranklin.powerful.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.grpc.service.EchoNum;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.service.GrpcClientInterceptor;
import zhranklin.powerful.service.PowerfulService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2019/10/29.
 */
public class GrpcRemoteInvoker extends EchoGrpc.EchoImplBase implements RemoteInvoker {

    private static Logger logger = LoggerFactory.getLogger(GrpcRemoteInvoker.class);

    @Autowired(required = false)
    private GrpcClientInterceptor grpcClientInterceptor;

    protected PowerfulService powerful;

    public void setPowerful(PowerfulService powerful) {
        this.powerful = powerful;
    }

    protected EchoGrpc.EchoBlockingStub grpcAEchoBlockingStub;
    protected EchoGrpc.EchoBlockingStub grpcBEchoBlockingStub;

    protected EchoGrpc.EchoStub grpcAEchoStub;
    protected EchoGrpc.EchoStub grpcBEchoStub;

    private ObjectMapper objectMapper = new ObjectMapper();

    public Object invoke(Instruction instruction, RenderingContext context) {
        String num = instruction.getWithQuerys().get("num");
        int param = 0;
        try {
            param = Integer.parseInt(num);
        } catch (Exception e) {
            logger.warn("query param num is illegal");
        }
        String beanName = instruction.getTell();
        EchoNum.Builder builder = EchoNum.newBuilder();
        try {
            String instructionStr = objectMapper.writeValueAsString(instruction.getTo());
            String contextStr = objectMapper.writeValueAsString(context);
            builder.setInstruction(instructionStr).setContext(contextStr);
        } catch (Exception e) {
            logger.error("convert to json string error");
        }
        builder.setNum(param);
        EchoNum echoNum = builder.build();
        grpcClientInterceptor.addCustomizeHeaders(instruction.getWithHeaders());
        if (beanName.equalsIgnoreCase("grpc-a")) {
            return grpcAEchoBlockingStub.echo(echoNum).getMessage();
        } else if (beanName.equalsIgnoreCase("grpc-b")) {
            return grpcBEchoBlockingStub.echo(echoNum).getMessage();
        }
        return "unknow service";
    }


    protected String getColor() {
        try {
            Class clazz = this.getClass().getClassLoader().loadClass("zhranklin.agent.core.flowcolor.GlobalContext");
            Method method = clazz.getDeclaredMethod("getFlowColor");
            Object color = method.invoke(null);
            if (color == null) {
                return "null";
            }
            return color.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setGrpcAEchoBlockingStub(EchoGrpc.EchoBlockingStub grpcAEchoBlockingStub) {
        this.grpcAEchoBlockingStub = grpcAEchoBlockingStub;
    }

    public void setGrpcBEchoBlockingStub(EchoGrpc.EchoBlockingStub grpcBEchoBlockingStub) {
        this.grpcBEchoBlockingStub = grpcBEchoBlockingStub;
    }

    public void setGrpcAEchoStub(EchoGrpc.EchoStub grpcAEchoStub) {
        this.grpcAEchoStub = grpcAEchoStub;
    }

    public void setGrpcBEchoStub(EchoGrpc.EchoStub grpcBEchoStub) {
        this.grpcBEchoStub = grpcBEchoStub;
    }
}
