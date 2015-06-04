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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import cp.server.common.NewsType;
import cp.server.common.Page;
import cp.server.common.SourceType;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;

/**
 * An example that performs GETs from multiple threads.
 * 
 */
public class ClientMultiThreadedExecution
{

    public static void main(String[] args) throws Exception
    {
        // Create an HttpClient with the ThreadSafeClientConnManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm).build();
        try
        {
            // create an array of URIs to perform GETs on
            String[] urisToGet =
            {
                    // "http://sports.sina.com.cn",
                    // "http://news.sina.com.cn",
                    // "http://ent.sina.com.cn",
                    // "http://tech.sina.com.cn",
                    "http://sports.sina.com.cn/o/2013-10-27/04016852444.shtml",
                    "http://finance.sina.com.cn/china/20131027/043917125695.shtml",
                    "http://finance.sina.com.cn/china/20131027/160117127254.shtml",
                    "http://news.sina.com.cn/c/2013-10-27/031028540929.shtml",
                    "http://sports.163.com/13/1026/18/9C4OK9KQ00051C89.html",
                    "http://sports.163.com/13/1026/21/9C54OMV300051C89.html",
                    "http://sports.sina.com.cn/g/laliga/2013-10-27/00476852194.shtml",
                    "http://news.163.com/13/1027/14/9C6TL46N0001124J.html",
                    "http://news.163.com/13/1027/15/9C736GOU00014JB6.html" };
            // String[] urisToGet =
            // { "http://sports.163.com", "http://news.163.com",
            // "http://ent.163.com", "http://tech.163.com",
            // "http://money.163.com/13/1026/15/9C4FMKCH00254TI5.html",
            // "http://sports.163.com/13/1026/20/9C50T3A900051C89.html",
            // "http://sports.163.com/13/1026/18/9C4OK9KQ00051C89.html",
            // "http://sports.163.com/13/1026/21/9C54OMV300051C89.html",
            // "http://app.audiogon.com/listings/solid-state-first-watt-pass-labs-sit-1-pair-new-new-innovation-from-pass-2013-10-26-amplifiers-89509",
            // "http://app.audiogon.com/listings/solid-state-first-watt-pass-labs-sit-2-120v-demo-new-innovation-from-pass-2013-10-26-amplifiers-89509",
            // "http://app.audiogon.com/listings/solid-state-pass-labs-first-watt-j2-120v-satisfaction-guaranteed-2013-10-26-amplifiers-89509"};

            // create a thread for each URI
            GetThread[] threads = new GetThread[20];
            
            int i =0 ;
            
            for (SourceType source : SourceType.values())
            {
                for (NewsType type : NewsType.values())
                {
                    threads[i] = new GetThread(httpclient, ++i, source, type);


                }
            }

            // start the threads
            for (int j = 0; j < threads.length; j++)
            {
                threads[j].start();
            }

            // join the threads
            for (int j = 0; j < threads.length; j++)
            {
                threads[j].join();
            }

        }
        finally
        {
            httpclient.close();
        }
    }

    /**
     * A thread that performs a GET.
     */
    static class GetThread extends Thread
    {

        private final CloseableHttpClient httpClient;
        private final HttpContext context;
        private final SourceType source;
        private final NewsType type;
        private final int id;

        public GetThread(CloseableHttpClient httpClient, int id, SourceType source,
                NewsType type)
        {
            this.httpClient = httpClient;
            this.context = new BasicHttpContext();
            this.source = source;
            this.type = type;
            this.id = id;
        }

        /**
         * Executes the GetMethod and prints some status information.
         */
        @Override
        public void run()
        {
            ServerDAO dao = new ServerDAO();
            Connection con = null;

            List<Page> pages = null;
            try
            {
                con = ConnectionFactory.getConnection();
                pages = dao.queryPagesByIntervalAndType(con, 1, source,
                        type);
            }
            catch (SQLException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            finally
            {
                if(con != null)
                {
                    try
                    {
                        con.close();
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            
            if(pages == null)
            {
                System.out.println("shit happens!!");
                return;
            }

            for (int i = 0; i < pages.size(); i++)
            {
                try
                {
                    HttpGet httpget = new HttpGet(pages.get(i).getUrl());
                    System.out.println(id + " - about to get something from "
                            + httpget.getURI());
                    CloseableHttpResponse response = httpClient.execute(
                            httpget, context);
                    try
                    {
                        System.out.println(id + " - get executed");
                        // get the response body as an array of bytes
                        HttpEntity entity = response.getEntity();
                        if (entity != null)
                        {
                            byte[] bytes = EntityUtils.toByteArray(entity);
                            System.out.println(id + " - " + bytes.length
                                    + " bytes read");
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
            }
        }

    }

}
