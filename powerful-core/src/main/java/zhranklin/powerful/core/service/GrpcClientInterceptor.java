package zhranklin.powerful.core.service;

import io.grpc.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 2019/11/1.
 */

public class GrpcClientInterceptor implements ClientInterceptor {

    private final Map<String, String> customizeHeaders = new HashMap<>();

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                customizeHeaders.forEach((k, v) -> headers.put(Metadata.Key.of(k, Metadata.ASCII_STRING_MARSHALLER), v));
                customizeHeaders.clear();
                super.start(responseListener, headers);
            }
        };
    }

    public void addCustomizeHeaders(Map<String, String> headers) {
        this.customizeHeaders.putAll(headers);
    }
}