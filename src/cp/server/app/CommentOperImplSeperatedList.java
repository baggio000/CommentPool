package cp.server.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cp.server.common.Comment;
import cp.server.common.NewsType;
import cp.server.common.Page;
import cp.server.common.SourceType;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;

public class CommentOperImplSeperatedList implements CommentOper
{
    private static final Log log = LogFactory
            .getLog(CommentOperImplSeperatedList.class);

    private SourceType source;

    @Override
    public void setSource(SourceType source)
    {
        this.source = source;
    }
    
    public void init()
    {
        ServerDAO dao = new ServerDAO();
        ExecutorService pool = null;
        CountDownLatch cdl = null;
        List<Page> pages = null;
        CommentThreadSeperatedList thread = null;
        Time ts = new Time(System.currentTimeMillis());
        int interval;
        int threadCount = 0;
        int threadNum = 0;
        Connection con = null;
        long beg = System.currentTimeMillis();

        try
        {
            con = ConnectionFactory.getConnection();

            // before 10am, query with the comment yesterday
            if (Integer.valueOf(ts.toString().substring(0, 2)) > 10)
            {
                interval = 1;
            }
            else
            {
                interval = 2;
            }

            threadNum = NewsType.values().length;

            pool = Executors.newFixedThreadPool(threadNum);
            cdl = new CountDownLatch(threadNum);

            // for (SourceType source : SourceType.values())
            // {
            // pages = dao
            // .queryPagesByIntervalAndSource(con, interval, source);
            //
            // System.out.println("load comments from " + pages.size()
            // + "pages.");
            //
            // thread = new CommentThreadSeperatedList(cdl, threadCount++,
            // pages);
            // pool.execute(thread);
            //
            // }

            for (NewsType type : NewsType.values())
            {
                pages = dao.queryPagesByIntervalAndType(con, interval, source,
                        type);

                log.info("load comments from " + pages.size() + "pages.");

                thread = new CommentThreadSeperatedList(cdl, threadCount++,
                        pages);
                pool.execute(thread);

            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    log.error(e);
                }
            }

        }

        try
        {
            cdl.await();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            log.error(e);
        }

        try
        {
            pool.shutdown();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            log.error(e);
        }

        log.info("comment operation end, time last: "
                + (System.currentTimeMillis() - beg) / 1000.0 + "s");

    }

    @Override
    public List<Comment> fetchCommentsFromPage(Page page)
            throws ParserException
    {

        return null;
    }
    
    public static void main(String args[]) throws ParserException,
            FileNotFoundException, ClassNotFoundException, IOException,
            SQLException
    {
        CommentOper p = new CommentOperImplSeperatedList();
        p.setSource(SourceType.NETEASE);
        p.init();

        // Map<String, Page> pageMap = (Map<String, Page>) FileUtils
        // .readObjectFromFile("pageMap");
        // System.out.println(pageMap.size());
        //
        // if (pageMap == null)
        // {
        // return;
        // }
        //
        // int count = 0;
        //
        // Iterator<Entry<String, Page>> iter = pageMap.entrySet().iterator();
        // while (iter.hasNext())
        // {
        // Map.Entry<String, Page> entry = (Map.Entry<String, Page>) iter
        // .next();
        // Page page = entry.getValue();
        // if (page.getId() == null || page.getChannel() == null ||
        // page.getTitle() == null || !page.isAvailable() )
        // {
        // // TODO set the page unavailable, and save to the pagemap
        // System.out.println(page.getUrl());
        // count++;
        // continue;
        // }
        // //p.fetchCommentsFromPage(page);
        // }
        //
        // System.out.println(count);

        // ServerDAO dao = new ServerDAO();
        //
        // List<Page> pages =
        // dao.queryAllPages(ConnectionFactory.getConnection());
        //
        // for (Page page : pages)
        // {
        // PAGESTACK.add(page);
        // p.fetchCommentsFromPage(page);
        // }

        // Page page = new Page(NewsType.NEWS,
        // "http://news.sina.com.cn/w/2013-09-01/075828101453.shtml");
        // page.setChannel("gj");
        // page.setId("1-1-28101453");

    }
}
