package zhranklin.powerful.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.grpc.service.EchoNum;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * Created by twogoods on 2019/10/29.
 */
public class GrpcPowerfulService extends AbstractPowerfulService {

    private static Logger logger = LoggerFactory.getLogger(GrpcPowerfulService.class);

    @Autowired(required = false)
    private GrpcClientInterceptor grpcClientInterceptor;

    private ApplicationContext applicationContext;

    protected EchoGrpc.EchoBlockingStub grpcAEchoBlockingStub;
    protected EchoGrpc.EchoBlockingStub grpcBEchoBlockingStub;

    protected EchoGrpc.EchoStub grpcAEchoStub;
    protected EchoGrpc.EchoStub grpcBEchoStub;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GrpcPowerfulService(StringRenderer stringRenderer, ApplicationContext applicationContext) {
        super(stringRenderer);
        this.applicationContext = applicationContext;
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
