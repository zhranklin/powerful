package zhranklin.powerful.rpc.grpc;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import zhranklin.powerful.grpc.service.EchoGrpc;
import zhranklin.powerful.grpc.service.EchoNum;
import zhranklin.powerful.grpc.service.Reply;
import zhranklin.powerful.core.invoker.GrpcRemoteInvoker;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.RenderingContext;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by twogoods on 2019/10/30.
 */
@GrpcService
@ConditionalOnProperty(name = "powerful.grpc.name", havingValue = "grpc-b")
public class GrpcBService extends GrpcRemoteInvoker implements InitializingBean {
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

    @Override
    public void echo(EchoNum request, StreamObserver<Reply> responseObserver) {
        logger.info("in GrpcBService num: " + request.getNum());
        try {
            Instruction instruction = objectMapper.readValue(request.getInstruction(), Instruction.class);
            RenderingContext context = objectMapper.readValue(request.getContext(), RenderingContext.class);
            String color = getColor();

            responseObserver.onNext(Reply.newBuilder().setMessage(powerful.execute(instruction, context).toString()).build());
            responseObserver.onCompleted();
        } catch (IOException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    @Override
    public void echoAsync(EchoNum request, StreamObserver<Reply> responseObserver) {
    }

    private void fallback(EchoNum req, StreamObserver<Reply> responseObserver){
        responseObserver.onNext(Reply.newBuilder().setMessage("fallback").build());
        responseObserver.onCompleted();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setGrpcAEchoBlockingStub(grpcAEchoBlockingStub);
        setGrpcAEchoStub(grpcAEchoStub);
    }
}
