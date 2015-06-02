/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package cp.server.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import cp.server.common.Page;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.WebUtils;

/**
 * An example that performs GETs from multiple threads.
 * 
 */
public class ClientMultiThreadedExecution
{
    private final static PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    private final static HttpClientBuilder httpClientBuilder;
    private final static CloseableHttpClient httpClient;

    static
    {
        cm.setMaxTotal(200);
        httpClientBuilder = HttpClients.custom().setConnectionManager(cm);
        httpClient = httpClientBuilder.build();
    }

    private static Stack<String> PAGESTACK = new Stack<String>();

    private static ReentrantLock stackLock = new ReentrantLock();

    protected static String getUrl()
    {
        stackLock.lock();

        try
        {
            if (!PAGESTACK.empty())
                return PAGESTACK.pop();
            return null;
        }
        finally
        {
            stackLock.unlock();
        }
    }

    public static void main(String[] args) throws Exception
    {
        ClientMultiThreadedExecution.fetch();
    }

    public static void fetch() throws Exception
    {
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        // PoolingHttpClientConnectionManager cm = new
        // PoolingHttpClientConnectionManager();
        // cm.setMaxTotal(100);

        // CloseableHttpClient httpclient = HttpClients.custom()
        // .setConnectionManager(cm).build();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        ServerDAO dao = new ServerDAO();
        List<Page> pages = null;
        Time ts = new Time(System.currentTimeMillis());
        int interval;

        try
        {
            // // before 10am, query with the comment yesterday
            // if (Integer.valueOf(ts.toString().substring(0, 2)) > 10)
            // {
            // interval = 1;
            // }
            // else
            // {
            // interval = 2;
            // }
            //
            // pages = dao.queryPagesByDayInterval(
            // ConnectionFactory.getConnection(), interval);
            //
            // System.out.println("load comments from " + pages.size() +
            // "pages.");
            // for (Page page : pages)
            // {
            // PAGESTACK.push(page.getUrl());
            // }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            // create an array of URIs to perform GETs on
            String[] urisToGet =
            {
                    "http://sports.sina.com.cn",
                    "http://news.sina.com.cn",
                    "http://ent.sina.com.cn",
                    "http://tech.sina.com.cn",
                    "http://sports.sina.com.cn/o/2013-10-27/04016852444.shtml",
                    "http://finance.sina.com.cn/china/20131027/043917125695.shtml",
                    "http://sports.sina.com.cn/j/2013-10-27/06336852561.shtml",
                    "http://sports.sina.com.cn/j/2013-10-26/21006851844.shtml" };

            for (int i = 0; i < 10000; i++)
            {
                for (int j = 0; j < urisToGet.length; j++)
                {
                    PAGESTACK.push(urisToGet[j]);
                }
            }

            CountDownLatch cdl = new CountDownLatch(6);

            // create a thread for each URI
            GetThread[] threads = new GetThread[urisToGet.length];

            for (int i = 0; i < 4; i++)
            {
                // HttpGet httpget = new HttpGet(urisToGet[i]);
                threads[i] = new GetThread(urisToGet[i], i + 1, cdl);
            }

            // start the threads
            for (int j = 0; j < 4; j++)
            {
                pool.execute(threads[j]);
                // threads[j].start();
            }

            cdl.await();

        }
        finally
        {
            // httpclient.close();
            pool.shutdown();
        }
    }

    public String fetchUrl(int id, String url)
    {
        HttpContext context = new BasicHttpContext();

        HttpGet httpget = new HttpGet(url);

        StringBuffer result = new StringBuffer();

        BufferedReader rd = null;

        try
        {
            System.out.println(id + " - about to get something from "
                    + httpget.getURI());
            CloseableHttpResponse response = httpClient.execute(httpget,
                    context);
            try
            {
                System.out.println(id + " - get executed");
                // get the response body as an array of bytes
                HttpEntity entity = response.getEntity();
                if (entity != null)
                {
                    // byte[] bytes = EntityUtils.toByteArray(entity);
                    // System.out.println(id + " - " + bytes.length
                    // + " bytes read");
                    try
                    {
                        rd = new BufferedReader(new InputStreamReader(
                                entity.getContent()));

                        String line = "";
                        while ((line = rd.readLine()) != null)
                        {
                            result.append(line);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    finally
                    {
                        if (rd != null)
                        {
                            rd.close();
                        }
                    }
                }
            }
            finally
            {
                response.close();
                httpget.releaseConnection();
            }
        }
        catch (Exception e)
        {
            System.out.println(id + " - error: " + e);
        }

        return url;

    }

    /**
     * A thread that performs a GET.
     */
    static class GetThread implements Runnable
    {
        private final String httpget;
        private final int id;
        private final CountDownLatch cdl;

        // public GetThread(CloseableHttpClient httpClient, HttpGet httpget, int
        // id, CountDownLatch cdl)
        // {
        // this.httpClient = httpClient;
        // this.context = new BasicHttpContext();
        // this.httpget = httpget;
        // this.id = id;
        // this.cdl = cdl;
        // }

        // public GetThread(HttpGet httpget, int id, CountDownLatch cdl)
        // {
        // this.context = new BasicHttpContext();
        // this.httpget = httpget;
        // this.id = id;
        // this.cdl = cdl;
        // }

        public GetThread(String httpget, int id, CountDownLatch cdl)
        {
            this.httpget = httpget;
            this.id = id;
            this.cdl = cdl;
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run()
        {

            // HttpGet request = null;
            // BufferedReader rd = null;
            // HttpContext context = new BasicHttpContext();
            // CloseableHttpClient httpClient = httpClientBuilder.build();
            //
            // try
            // {
            //
            // StringBuilder result = new StringBuilder();
            //
            // CloseableHttpResponse response = httpClient.execute(httpget,
            // context);
            //
            // try
            // {
            // HttpEntity entity = response.getEntity();
            //
            // if (entity != null)
            // {
            //
            // rd = new BufferedReader(new InputStreamReader(
            // entity.getContent()));
            //
            // String line = "";
            // while ((line = rd.readLine()) != null)
            // {
            // result.append(line);
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
            //
            // if (response != null)
            // {
            // response.close();
            // }
            // }
            // }
            // catch (Exception ex)
            // {
            // ex.printStackTrace();
            // }

            try
            {
                while (true)
                {
                    String url = getUrl();

                    if (url == null)
                    {
                        break;
                    }
                    ClientMultiThreadedExecution c = new ClientMultiThreadedExecution();
                    WebUtils w = new WebUtils();
                    String content = w.fetchPage(url, null);
                    System.out.println(content.length());

                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {

                cdl.countDown();
            }

        }

        private String str;

        public String getString()
        {
            return str;
        }

    }

}