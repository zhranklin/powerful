package zhranklin.powerful.core.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;

import java.io.IOException;
import java.util.Map;

public class HttpClient5RemoteInvoker extends HttpRemoteAbstractInvoker {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientRemoteInvoker.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public HttpClient5RemoteInvoker(StringRenderer stringRenderer) {
        super(stringRenderer);
    }

    @Override
    public PowerfulResponse doInvoke(Map<String, String> headers, String url, String method, Instruction body) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            switch (method) {
                case "GET":
                    return execGetRequest(httpclient, headers, url);
                case "POST":
                    return execPostRequest(httpclient, headers, url, objectMapper.writeValueAsString(body));
                case "DELETE":
                    return execDeleteRequest(httpclient, headers, url);
                case "PUT":
                    return execPutRequest(httpclient, headers, url, objectMapper.writeValueAsString(body));
                case "PATCH":
                    return execPatchRequest(httpclient, headers, url, objectMapper.writeValueAsString(body));
                default:
                    logger.warn("Unsupported http request method!");
                    return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private PowerfulResponse execGetRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url)
            throws IOException, ParseException {
        HttpGet httpGet = new HttpGet(url);
        headers.forEach(httpGet::setHeader);
        return PowerfulResponse.fromHttp(httpclient.execute(httpGet));
    }

    private PowerfulResponse execDeleteRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url)
            throws IOException, ParseException {
        HttpDelete httpDelete = new HttpDelete(url);
        headers.forEach(httpDelete::setHeader);
        return PowerfulResponse.fromHttp(httpclient.execute(httpDelete));
    }

    private PowerfulResponse execPostRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url, String body)
            throws IOException, ParseException {
        HttpPost httpPost = new HttpPost(url);
        headers.forEach(httpPost::setHeader);
        httpPost.setEntity(new StringEntity(body));
        return PowerfulResponse.fromHttp(httpclient.execute(httpPost));
    }

    private PowerfulResponse execPutRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url, String body)
            throws IOException, ParseException {
        HttpPut httpPut = new HttpPut(url);
        headers.forEach(httpPut::setHeader);
        httpPut.setEntity(new StringEntity(body));
        return PowerfulResponse.fromHttp(httpclient.execute(httpPut));
    }

    private PowerfulResponse execPatchRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url, String body)
            throws IOException, ParseException {
        HttpPatch httpPatch = new HttpPatch(url);
        headers.forEach(httpPatch::setHeader);
        httpPatch.setEntity(new StringEntity(body));
        return PowerfulResponse.fromHttp(httpclient.execute(httpPatch));
    }
}
