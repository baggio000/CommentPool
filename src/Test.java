import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import cp.server.app.CommentOper;
import cp.server.app.CommentOperImpl;
import cp.server.app.CommentThread;
import cp.server.app.JobExecutor;
import cp.server.app.LoaderException;
import cp.server.app.NeteasePageLoader;
import cp.server.app.NeteaseWebLoader;
import cp.server.app.PageLoader;
import cp.server.app.SinaPageLoader;
import cp.server.app.SinaWebLoader;
import cp.server.app.WebLoader;
import cp.server.common.JobType;

public class Test
{
    public static void main(String args[]) throws ClientProtocolException,
            IOException, LoaderException
    {

        CommentOper co = new CommentOperImpl();
        WebLoader w = new SinaWebLoader();
        WebLoader w1 = new NeteaseWebLoader();
        PageLoader p = new SinaPageLoader();
        PageLoader p1 = new NeteasePageLoader();
        long beg = System.currentTimeMillis();
        long end;

        // try
        // {
        // w.loadWebs(true);
        // w1.loadWebs(true);
        // }
        // catch (Exception ex)
        // {
        // ex.printStackTrace();
        // }
        //
        // end = System.currentTimeMillis();

        // try
        // {
        // p.loadPages(false);
        // p1.loadPages(false);
        // }
        // catch (Exception ex)
        // {
        // ex.printStackTrace();
        // }
        //
        // end = System.currentTimeMillis();
        //
        // System.out.println("load pages last:" + (end - beg) / 1000.0 + "s");
        //
        // beg = System.currentTimeMillis();
        //
        // try
        // {
        // co.init();
        // }
        // catch (Exception ex)
        // {
        // ex.printStackTrace();
        // }
        //
        // end = System.currentTimeMillis();
        //
        // System.out.println("load comments last:" + (end - beg) / 1000.0 +
        // "s");

        ExecutorService pool = Executors.newFixedThreadPool(10);

        pool.submit(new JobExecutor(5 * 60 * 1000, JobType.SINACOMMENTOPER));
        pool.submit(new JobExecutor(5 * 60 * 1000, JobType.NETEASECOMMENTOPER));
        pool.submit(new JobExecutor(10 * 60 * 1000, JobType.SINAPAGELOADER));
        pool.submit(new JobExecutor(10 * 60 * 1000, JobType.NETEASEPAGELOADER));
        pool.submit(new JobExecutor(5 * 60 * 1000, JobType.KEYWORDFINDER));
        pool.submit(new JobExecutor(5 * 60 * 1000, JobType.HOTCOMMENT));
        pool.submit(new JobExecutor(5 * 60 * 1000, JobType.HOTCOMMENTBYTYPE));
        pool.submit(new JobExecutor(2 * 60 * 1000, JobType.FILEUPLOADER));
        
    }
}
