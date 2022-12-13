package zhranklin.powerful.core.invoker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zhranklin.powerful.core.service.StringRenderer;
import zhranklin.powerful.model.Instruction;
import zhranklin.powerful.model.PowerfulResponse;

import java.io.IOException;
import java.util.Map;

public class HttpClientRemoteInvoker extends HttpRemoteAbstractInvoker {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientRemoteInvoker.class);

    private final ObjectMapper objectMapper;

    public HttpClientRemoteInvoker(StringRenderer stringRenderer, ObjectMapper objectMapper) {
        super(stringRenderer);
        this.objectMapper = objectMapper;
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
            throws IOException {
        HttpGet httpGet = new HttpGet(url);
        headers.forEach(httpGet::setHeader);
        return PowerfulResponse.fromHttp(httpclient.execute(httpGet));
    }

    private PowerfulResponse execDeleteRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url)
            throws IOException {
        HttpDelete httpDelete = new HttpDelete(url);
        headers.forEach(httpDelete::setHeader);
        return PowerfulResponse.fromHttp(httpclient.execute(httpDelete));
    }

    private PowerfulResponse execPostRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url, String body)
            throws IOException {
        HttpPost httpPost = new HttpPost(url);
        headers.forEach(httpPost::setHeader);
        httpPost.setEntity(new StringEntity(body));
        return PowerfulResponse.fromHttp(httpclient.execute(httpPost));
    }

    private PowerfulResponse execPutRequest(CloseableHttpClient httpclient, Map<String, String> headers, String url, String body)
            throws IOException {
        HttpPut httpPut = new HttpPut(url);
        headers.forEach(httpPut::setHeader);
        httpPut.setEntity(new StringEntity(body));
        return PowerfulResponse.fromHttp(httpclient.execute(httpPut));
    }
}
