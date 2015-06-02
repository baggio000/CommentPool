package cp.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import cp.server.common.SystemProperty;

public class WebUtils
{
    private static final Log log = LogFactory.getLog(WebUtils.class);

    private final static int MAX_HTTP_CONN = 200;
    private final static int MAX_PER_ROUTE = 20;
    private final static int READ_BUFF = 4 * 1024;
    private final static int SOCKET_TIMEOUT = 10 * 1000;
    private final static int CONN_TIMEOUT = 20 * 1000;
    private final static int CONN_REQ_TIMEOUT = 10 * 1000;
    private static PoolingHttpClientConnectionManager cm;
    private static CloseableHttpClient httpClient;

    static
    {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(SOCKET_TIMEOUT).build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectTimeout(CONN_TIMEOUT)
                .setConnectionRequestTimeout(CONN_REQ_TIMEOUT)
                .setRedirectsEnabled(false).build();

        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(MAX_HTTP_CONN);
        cm.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        httpClient = HttpClients.custom().setConnectionManager(cm)
                .setDefaultSocketConfig(socketConfig)
                .setDefaultRequestConfig(requestConfig).build();

    }

    public static String fetchPage(String url, String encoding)
            throws ClientProtocolException, IOException
    {
        HttpGet request = null;
        CloseableHttpResponse response = null;
        BufferedReader rd = null;
        StringWriter result = new StringWriter();
        HttpContext context = new BasicHttpContext();
        char[] buffer = new char[READ_BUFF];

        try
        {
            if (encoding == null || encoding.trim().length() <= 0)
            {
                encoding = SystemProperty.DEFAULTENCODING;
            }

            log.debug("fetchPage 1");
            request = new HttpGet(url);
            response = httpClient.execute(request, context);
            log.debug("fetchPage 2");

            if (response.getStatusLine().getStatusCode() != 200)
            {
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());
                System.out.println("Access page failed:" + url);
                return null;
            }

            log.debug("fetchPage 3");

            try
            {
                HttpEntity entity = response.getEntity();

                if (entity != null)
                {

                    rd = new BufferedReader(new InputStreamReader(
                            entity.getContent(), encoding));

                    log.debug("fetchPage 4");

                    int n = 0;
                    while ((n = rd.read(buffer)) != -1)
                    {
                        result.write(buffer, 0, n);
                    }
                }
            }
            catch (Exception ex)
            {
                log.error(ex);
                return null;
            }
            finally
            {
                if (rd != null)
                {
                    rd.close();
                }
            }
        }
        finally
        {
            if (response != null)
            {
                response.close();
            }

            if (request != null)
            {
                request.releaseConnection();
            }
        }

        log.debug("fetchPage 5");

        return result.toString();
    }

    // public static String fetchPage(String url, String encoding)
    // throws ClientProtocolException, IOException
    // {
    // HttpGet request = null;
    // CloseableHttpResponse response = null;
    // BufferedReader rd = null;
    // StringWriter result = new StringWriter();
    // HttpContext context = new BasicHttpContext();
    // char[] buffer = new char[READ_BUFF];
    //
    // try
    // {
    // request = new HttpGet(url);
    // response = httpClient.execute(request, context);
    //
    // if (response.getStatusLine().getStatusCode() != 200)
    // {
    // System.out.println("Response Code : "
    // + response.getStatusLine().getStatusCode());
    // System.out.println("Access page failed:" + url);
    // return null;
    // }
    //
    // try
    // {
    // HttpEntity entity = response.getEntity();
    //
    // if (entity != null)
    // {
    //
    // rd = new BufferedReader(new InputStreamReader(
    // entity.getContent(), encoding));
    //
    // int n = 0;
    // while ((n = rd.read(buffer)) != -1)
    // {
    // result.write(buffer, 0, n);
    // }
    // }
    // }
    // catch (Exception ex)
    // {
    // ex.printStackTrace();
    // }
    // finally
    // {
    // if (rd != null)
    // {
    // rd.close();
    // }
    // }
    // }
    // finally
    // {
    // if (response != null)
    // {
    // response.close();
    // }
    //
    // if (request != null)
    // {
    // request.releaseConnection();
    // }
    // }
    //
    // return result.toString();
    // }

    public static void main(String args[]) throws ClientProtocolException,
            IOException
    {
        String url = "http://comment.news.163.com/cache/newlist/sports_nba_bbs/9EC2QQ9L00051CA1_1.html";
        String web;

        web = WebUtils.fetchPage(url, "UTF8");

        System.out.println(web);
        FileUtils.writeStringToFile("163json.txt", web, false);

        // Gson gson = new Gson();
        // NeteaseJson nJson = gson.fromJson(
        // web.substring(web.indexOf("{"), web.length() - 1),
        // NeteaseJson.class);
        //
        // System.out.println(nJson.getReqtime());
        // System.out.println(nJson.getTcount());
        // System.out.println(nJson.getNewPosts().get(1).size());
        //
        // NeteaseComment cmt = new NeteaseComment(((Map) nJson.getNewPosts()
        // .get(1).get("5")));
        //
        // System.out.println(cmt.getB());

        // int count = 10000;
        // String pageReg =
        // "/([0-9,a-z,A-Z,/]*)/201([2-3]{1})([0-9,-]{6})/([0-9]+).shtml";
        // ArrayList<String> list = new ArrayList<String>();
        // while (count-- > 0)
        // {
        // web = WebUtils.fetchPage(url);
        //
        // Set<String> set = RegParser.parseReg(web, pageReg);
        // for(String a:set)
        // {
        // list.add(a);
        // }
        // }
    }

}
