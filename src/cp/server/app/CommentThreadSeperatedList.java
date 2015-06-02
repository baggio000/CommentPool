package cp.server.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cp.server.common.Comment;
import cp.server.common.Page;
import cp.server.common.SourceType;
import cp.server.common.netease.NeteaseComment;
import cp.server.common.netease.NeteaseJson;
import cp.server.common.netease.NeteaseJsonParser;
import cp.server.common.sina.SinaJson;
import cp.server.common.sina.SinaJsonParser;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.WebUtils;

public class CommentThreadSeperatedList implements Runnable
{
    private static final Log log = LogFactory
            .getLog(CommentThreadSeperatedList.class);

    private final static int MAX_COMMENT_PAGE = 100;
    private final static int TIME_OUT = 60 * 60 * 1000; // one hour time out
    private final static int UPDATE_AGREE_BASELINE = 50;
    private static final int WORKERTHREADNUM = 2;

    private CountDownLatch cdl = null;

    private int threadSerialNo;

    private List<Page> pageList;

    private Stack<Page> PAGESTACK = new Stack<Page>();

    private ReentrantLock stackLock = new ReentrantLock();

    private long maxTryLockWait = 30l;

    protected Page getPage()
    {
        try
        {
            stackLock.tryLock(maxTryLockWait, TimeUnit.SECONDS);
        }
        catch (Exception ex)
        {
            log.error("Shit happens!" + threadSerialNo
                    + " try lock timeout!");
            log.error(ex);
            return null;
        }

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

    public CommentThreadSeperatedList(CountDownLatch cdl, int threadSerialNo,
            List<Page> pageList)
    {
        this.cdl = cdl;
        this.threadSerialNo = threadSerialNo;
        this.pageList = pageList;
    }

    @Override
    public void run()
    {
        CountDownLatch cdlWorker = new CountDownLatch(WORKERTHREADNUM);
        ExecutorService pool = Executors.newFixedThreadPool(WORKERTHREADNUM);
        CommentWorkerThread cwtThread;

        try
        {
            if (pageList == null || pageList.size() == 0)
            {
                return;
            }

            for (Page page : pageList)
            {
                PAGESTACK.push(page);
            }

            for (int i = 0; i < WORKERTHREADNUM; i++)
            {
                cwtThread = new CommentWorkerThread(cdlWorker, pageList.get(0)
                        .getSource());

                pool.execute(cwtThread);
            }

            cdlWorker.await();

        }
        catch (Exception ex)
        {
            log.error(ex);
        }
        finally
        {
            try
            {
                pool.shutdown();
            }
            catch (Exception ex)
            {
                log.error(ex);
            }

            try
            {
                cdl.countDown();
            }
            catch (Exception ex)
            {
                log.error(ex);
            }

            log.info(threadSerialNo + ":loop end at "
                    + new Timestamp(System.currentTimeMillis()));
        }

       log.info("end");
    }

    private class CommentWorkerThread implements Runnable
    {
        private boolean running = true;
        private long beg;
        private SourceType source;
        private CountDownLatch cdl = null;

        public void setRunning(boolean running)
        {
            this.running = running;
        }

        public CommentWorkerThread(CountDownLatch cdl, SourceType source)
        {
            this.cdl = cdl;
            this.source = source;
        }

        public void run()
        {

            Connection con = null;

            try
            {
                con = ConnectionFactory.getConnection();
                switch (source)
                {
                case SINA:
                    try
                    {
                        parseSinaPageList(con);
                    }
                    catch (Exception ex)
                    {
                        log.error(ex);
                    }
                    break;
                case NETEASE:
                    try
                    {
                        parseNeteasePageList(con);
                    }
                    catch (Exception ex)
                    {
                        log.error(ex);
                    }
                    break;
                case SOHU:
                case TENCENT:
                    break;
                default:
                    break;
                }
            }
            catch (Exception ex)
            {
                log.error(ex);
            }
            finally
            {
                try
                {
                    cdl.countDown();
                }
                catch (Exception ex)
                {
                    log.error(ex);
                }

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

        }

        private void parseSinaPageList(Connection con) throws Exception
        {
            Page page = null;

            log.debug("start parseSinaPageList");

            while (running)
            {
                beg = System.currentTimeMillis();

                page = getPage();

                // if page is null, break and end the thread
                if (page == null)
                {
                    log.info(threadSerialNo
                            + ":no page at all, prepare to quit loop");
                    break;
                }

                try
                {
                    parseSinaComment(con, page);
                }
                catch (Exception ex)
                {
                    log.info(threadSerialNo + ":error happens in "
                            + page.getUrl());
                    log.error(ex);
                }

            }
            
            log.debug("end parseSinaPageList");
        }

        private void parseNeteasePageList(Connection con) throws Exception
        {
            Page page = null;

            log.debug("start parseNeteasePageList");

            while (running)
            {
                beg = System.currentTimeMillis();

                page = getPage();

                // if page is null, break and end the thread
                if (page == null)
                {
                    log.info(threadSerialNo
                            + ":no page at all, prepare to quit loop");
                    break;
                }

                try
                {
                    parseNeteaseComment(con, page);
                }
                catch (Exception ex)
                {
                    log.error(threadSerialNo + ":error happens in "
                            + page.getUrl());
                    log.error(ex);
                }

            }
            
            log.debug("end parseNeteasePageList");

        }

        private void parseSinaComment(Connection con, Page page)
                throws Exception
        {
            String commentUrlHead = "http://comment5.news.sina.com.cn/page/info?format=js&group=&compress=1&ie=gbk&jsvar=requestId_44705133&page_size=100";
            String channelStr = "&channel=";
            String newsidStr = "&newsid=";
            String pageStr = "&page=";

            String commentPage = null;
            String commentUrl = null;
            String encoding = "GBK";

            StringBuffer buffer = new StringBuffer();

            commentUrlHead = buffer.append(commentUrlHead).append(channelStr)
                    .append(page.getChannel()).append(newsidStr)
                    .append(page.getId()).append(pageStr).toString();

            SinaJson sinaJson = null;

            try
            {
                for (int i = 1; i < MAX_COMMENT_PAGE; i++)
                {
                    log.debug("parse parseSinaComment 1");

                    // if it's timeout, break and set running false
                    if (timeoutCheck(beg))
                    {
                        setRunning(false);
                        break;
                    }
                    
                    commentUrl = commentUrlHead + String.valueOf(i);

                    log.debug("start parse parseSinaComment 1 " + commentUrl);

                    commentPage = WebUtils.fetchPage(commentUrl, encoding);
                    
                    log.debug("finish parse parseSinaComment 1" + commentPage == null);
                    
                    // if commentPage is null, break
                    if (commentPage == null)
                    {
                        break;
                    }
                    
                    log.debug("parse parseSinaComment 2");

                    sinaJson = SinaJsonParser.parse(commentPage);

                    // if no comments, break
                    if (sinaJson.getResult().getCmntlist() == null
                            || sinaJson.getResult().getCmntlist().size() == 0)
                    {
                        break;
                    }
                    
                    log.debug("parse parseSinaComment 3");

                    List<Comment> cmtList = SinaJsonParser.toComments(sinaJson
                            .getResult().getCmntlist());

                    if (cmtList == null)
                    {
                        log.error("error happens in " + commentUrl);
                        log.error("Comment list is null!");
                        break;
                    }
                    
                    log.debug("parse parseSinaComment 4");

                    if (cmtList.size() == 0)
                    {
                        break;
                    }

                    persistenceProcess(con, cmtList);
                    
                    log.debug("parse parseSinaComment 5");
                }
            }
            catch (Exception ex)
            {
                log.error(ex);
                throw ex;
            }
        }

        private void parseNeteaseComment(Connection con, Page page)
                throws Exception
        {
            String commentPage = null;
            String commentUrl = null;
            String encoding = "UTF-8";

            StringBuffer buffer = new StringBuffer();

            String neteaseCommentUrlHead = "http://comment.news.163.com/cache/newlist/";

            String commentUrlHead = buffer.append(neteaseCommentUrlHead)
                    .append(page.getCmtUrlKeys()).append("/")
                    .append(page.getId()).append("_").toString();

            NeteaseJson json = null;
            NeteaseComment ncmt;

            try
            {
                for (int i = 1; i < MAX_COMMENT_PAGE; i++)
                {
                    log.debug("parse parseNeteaseComment 1");

                    // if it's timeout, break and set running false
                    if (timeoutCheck(beg))
                    {
                        setRunning(false);
                        break;
                    }

                    commentUrl = commentUrlHead + String.valueOf(i) + ".html";

                    log.debug("start parse parseSinaComment 1 " + commentUrl);

                    commentPage = WebUtils.fetchPage(commentUrl, encoding);
                    
                    log.debug("finish parse parseSinaComment 1" + commentPage == null);
                    
                    // if commentPage is null, break
                    if (commentPage == null)
                    {
                        break;
                    }
                    
                    log.debug("parse parseNeteaseComment 2");

                    json = NeteaseJsonParser.parse(commentPage);

                    List<NeteaseComment> list = new ArrayList<NeteaseComment>();

                    if (json.getNewPosts() == null)
                    {
                        break;
                    }
                    
                    log.debug("parse parseNeteaseComment 3");

                    for (int j = 0; j < json.getNewPosts().size(); j++)
                    {
                        Map map = json.getNewPosts().get(j);
                        int k = map.size();
                        k = map.get("d") == null ? k : k - 1;
                        ncmt = new NeteaseComment((Map) map.get(String
                                .valueOf(k)));

                        if (k > 1)
                        {
                            NeteaseComment cmt = new NeteaseComment(
                                    (Map) map.get(String.valueOf(k - 1)));
                            ncmt.setParaent(cmt.getPi());
                            cmt = null;
                        }

                        list.add(ncmt);

                        ncmt = null;
                    }
                    
                    log.debug("parse parseNeteaseComment 4");

                    List<Comment> cmtList = NeteaseJsonParser.toComments(list);

                    if (cmtList == null)
                    {
                        log.error("error happens in " + commentUrl);
                        log.error("Comment list is null!");
                        break;
                    }
                    
                    log.debug("parse parseNeteaseComment 5");

                    persistenceProcess(con, cmtList);
                }
            }
            catch (Exception ex)
            {
                log.error(ex);
                log.error("url content is " + commentPage);
                throw ex;
            }
        }

        private void persistenceProcess(Connection con, List<Comment> cmtList)
                throws Exception
        {
            ServerDAO dao = new ServerDAO();

            List<Comment> insertList = new ArrayList<Comment>(cmtList.size());
            List<Comment> updateList = new ArrayList<Comment>(cmtList.size());
            try
            {
                for (Comment comment : cmtList)
                {
                    if (dao.isCommentExisted(con, comment.getCmtId()))
                    {
                        // only the agree number is over the
                        // UPDATE_AGREE_BASELINE
                        // can be updated
                        if (comment.getAgree() > UPDATE_AGREE_BASELINE)
                        {
                            updateList.add(comment);
                        }
                    }
                    else
                    {
                        insertList.add(comment);
                    }

                }

                // insert the data never exists
                if (insertList.size() != 0)
                {
                    dao.insertComments(con, insertList);
                }

                // update the existed data

                if (updateList.size() != 0)
                {
                    dao.updateCommentsSourceProperty(con, updateList);
                }
            }
            catch (Exception e)
            {
                log.error(e);
                throw e;
            }
        }

        private boolean timeoutCheck(long beg)
        {
            return System.currentTimeMillis() - beg > TIME_OUT;
        }
    }

    public static void main(String args[]) throws InterruptedException
    {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        CountDownLatch cdl = new CountDownLatch(5);
        CommentThreadSeperatedList thread;
        for (int i = 0; i < 5; i++)
        {
            // thread = new CommentThreadSeperatedList(cdl, i);
            // pool.execute(thread);
        }
        cdl.await();
        pool.shutdown();

    }

}
