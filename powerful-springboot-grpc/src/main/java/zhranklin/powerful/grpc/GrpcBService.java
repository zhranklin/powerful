package zhranklin.powerful.grpc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.grpc.service.EchoNum;
import zhranklin.powerful.grpc.service.Reply;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import zhranklin.powerful.service.GrpcPowerfulService;
import zhranklin.powerful.service.StringRenderer;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by twogoods on 2019/10/30.
 */
@GrpcService
@ConditionalOnProperty(name = "framew.application.name", havingValue = "grpc-b")
public class GrpcBService extends GrpcPowerfulService implements InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(GrpcBService.class);

    @GrpcClient("grpc-a")
    private EchoGrpc.EchoBlockingStub grpcAEchoBlockingStub;
    @GrpcClient("grpc-a")
    private EchoGrpc.EchoStub grpcAEchoStub;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        logger.info("init grpc-b service ...");
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public GrpcBService(StringRenderer stringRenderer, ApplicationContext applicationContext) {
        super(stringRenderer, applicationContext);
    }

    @Override
    public void echo(EchoNum request, StreamObserver<Reply> responseObserver) {
        logger.info("in GrpcBService num: " + request.getNum());
        try {
            Instruction instruction = objectMapper.readValue(request.getInstruction(), Instruction.class);
            RenderingContext context = objectMapper.readValue(request.getContext(), RenderingContext.class);
            String color = getColor();

            responseObserver.onNext(Reply.newBuilder().setMessage(execute(instruction, context).toString()).build());
            responseObserver.onCompleted();
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void echoAsync(EchoNum request, StreamObserver<Reply> responseObserver) {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setGrpcAEchoBlockingStub(grpcAEchoBlockingStub);
        setGrpcAEchoStub(grpcAEchoStub);
    }
}