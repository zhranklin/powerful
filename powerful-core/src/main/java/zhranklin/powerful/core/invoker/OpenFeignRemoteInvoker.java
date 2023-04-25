package zhranklin.powerful.core.invoker;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import zhranklin.powerful.core.openfeign.*;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;

import java.util.HashMap;
import java.util.Map;

public class OpenFeignRemoteInvoker extends HttpRemoteAbstractInvoker{

    private static final Logger logger = LoggerFactory.getLogger(OpenFeignRemoteInvoker.class);


    public OpenFeignRemoteInvoker(StringRenderer stringRenderer) {
        super(stringRenderer);
    }

    @Autowired
    @Lazy
    private AAgentClient a;
    @Autowired
    @Lazy
    private BAgentClient b;
    @Autowired
    @Lazy
    private CAgentClient c;
    @Autowired
    @Lazy
    private ADubboAgentClient ad;
    @Autowired
    @Lazy
    private ADubboAgentClient bd;
    @Autowired
    @Lazy
    private ADubboAgentClient cd;
    @Autowired
    @Lazy
    private ASidecarClient as;
    @Autowired
    @Lazy
    private BSidecarClient bs;
    @Autowired
    @Lazy
    private CSidecarClient cs;
    @Autowired
    @Lazy
    private ADubboSidecarClient ads;
    @Autowired
    @Lazy
    private ADubboSidecarClient bds;
    @Autowired
    @Lazy
    private ADubboSidecarClient cds;

    private Map<String, CommonClient> serviceMap = new HashMap<>();

    @Override
    public PowerfulResponse doInvoke(Map<String, String> headers, String url, String method, Instruction body) {
        initServiceMap();
        Map<String, String> params = new HashMap<>();
        String path = null;
        String[] urlString = url.split("\\?");
        String _body = "";
        //param
        if (urlString.length > 1) {
            for (String p :urlString[1].split("&")) {
                String k = p.substring(0, p.indexOf("="));
                String v = p.substring(p.indexOf("=") + 1, p.length());
                if ("_body".equals(k)) {
                    _body = v;
                }
                else {
                    params.put(k, v);
                }
            }
        }
        //method
        String[] ServiceAndPath = urlString[0].split("/");
        String service = ServiceAndPath[2];
        path = ServiceAndPath.length > 4 ? "/" + ServiceAndPath[3] : "";
        try {
            switch (method) {
                case "GET":
                    return PowerfulResponse.fromHttp(serviceMap.get(service).executeGet(_body, headers, params, path));
                case "POST":
                    return PowerfulResponse.fromHttp(serviceMap.get(service).executePost(body, headers, params, path));
                case "DELETE":
                    return PowerfulResponse.fromHttp(serviceMap.get(service).executeDelete(_body, headers, params, path));
                case "PUT":
                    return PowerfulResponse.fromHttp(serviceMap.get(service).executePut(body, headers, params, path));
                default:
                    logger.warn("Unsupported http request method!");
                    return null;
            }
        } catch (Exception e) {
            if(e instanceof FeignException){
                return PowerfulResponse.fromHttp((FeignException) e);
            }
            return null;
        }
    }

    private void initServiceMap() {
        serviceMap.put("a-agent", a);
        serviceMap.put("b-agent", b);
        serviceMap.put("c-agent", c);
        serviceMap.put("a-dubbo-agent", ad);
        serviceMap.put("b-dubbo-agent", bd);
        serviceMap.put("c-dubbo-agent", cd);
        serviceMap.put("a-sidecar", as);
        serviceMap.put("b-sidecar", bs);
        serviceMap.put("c-sidecar", cs);
        serviceMap.put("a-dubbo-sidecar", ads);
        serviceMap.put("b-dubbo-sidecar", bds);
        serviceMap.put("c-dubbo-sidecar", cds);
    }

}
