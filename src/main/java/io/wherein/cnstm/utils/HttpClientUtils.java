package io.wherein.cnstm.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient Util.
 */
public class HttpClientUtils {

  /**
   * do get request.
   *
   * @param url request url.
   * @return HttpClientResult.
   * @throws URISyntaxException URISyntaxException.
   * @throws IOException IOException.
   */
  public static String doGet(String url)
      throws URISyntaxException, IOException {
    CloseableHttpClient httpClient = null;
    CloseableHttpResponse httpResponse = null;
    HttpGet httpGet = null;
    try {
      URIBuilder uriBuilder = new URIBuilder(url);
      httpGet = new HttpGet(uriBuilder.build());
      RequestConfig.Builder requestConfig = RequestConfig.custom();
      httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig.build()).build();

      httpResponse = httpClient.execute(httpGet);
      return getHttpClientResult(httpResponse);
    } finally {
      org.apache.http.client.utils.HttpClientUtils.closeQuietly(httpClient);
      org.apache.http.client.utils.HttpClientUtils.closeQuietly(httpResponse);
    }
  }

  /**
   * get http response.
   *
   * @return String
   */
  public static String getHttpClientResult(HttpResponse httpResponse) throws IOException {
    String content = "";
    if (httpResponse != null && httpResponse.getStatusLine() != null) {
      if (httpResponse.getEntity() != null) {
        content = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
      }
    }
    return content;
  }
}
