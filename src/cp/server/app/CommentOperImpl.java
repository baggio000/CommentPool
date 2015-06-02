package cp.server.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cp.server.common.Comment;
import cp.server.common.Page;
import cp.server.common.SourceType;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;

public class CommentOperImpl implements CommentOper
{
    private final Log log = LogFactory.getLog(CommentOperImpl.class);

    private static final int THREADNUM = 1;

    private Stack<Page> pageStack = new Stack<Page>();

    private ReentrantLock stackLock = new ReentrantLock();

    private SourceType source;

    @Override
    public void setSource(SourceType source)
    {
        this.source = source;
    }

    public Page getPage()
    {
        stackLock.lock();

        try
        {
            if (!pageStack.empty())
                return pageStack.pop();
            return null;
        }
        finally
        {
            stackLock.unlock();
        }
    }

    public void init()
    {
        ServerDAO dao = new ServerDAO();
        ExecutorService pool = Executors.newFixedThreadPool(THREADNUM);
        CountDownLatch cdl = new CountDownLatch(THREADNUM);
        List<Page> pages = null;
        CommentThread thread = null;
        Time ts = new Time(System.currentTimeMillis());
        int interval;
        long beg = System.currentTimeMillis();
        Connection con = null;

        try
        {
            if (source == null)
            {
                log.error("source is null");
                return;
            }

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

            pages = dao.queryPagesByIntervalAndSource(con, interval, source);
            log.info("load comments from " + source + " " + pages.size()
                    + "pages.");
            for (Page page : pages)
            {
                pageStack.add(page);
            }
        }
        catch (Exception ex)
        {
            log.error(ex);
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
                    log.error(e);
                }
            }
        }

        for (int i = 0; i < THREADNUM; i++)
        {
            thread = new CommentThread(this, cdl, i);
            pool.execute(thread);
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

        log.info(source + " comment operation end, time last: "
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
        CommentOper p = new CommentOperImpl();
        p.init();

        // Map<String, Page> pageMap = (Map<String, Page>) FileUtils
        // .readObjectFromFile("pageMap");
        // log.info(pageMap.size());
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
        // log.info(page.getUrl());
        // count++;
        // continue;
        // }
        // //p.fetchCommentsFromPage(page);
        // }
        //
        // log.info(count);

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
