package cp.server.app;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cp.server.common.Comment;
import cp.server.common.Page;
import cp.server.common.netease.NeteaseComment;
import cp.server.common.netease.NeteaseJson;
import cp.server.common.netease.NeteaseJsonParser;
import cp.server.common.netease.NeteaseProperty;
import cp.server.common.sina.SinaJson;
import cp.server.common.sina.SinaJsonParser;
import cp.server.common.sina.SinaProperty;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.WebUtils;

public class CommentThread implements Runnable
{
    private static final Log log = LogFactory.getLog(CommentThread.class);

    private final static int MAX_COMMENT_PAGE = 100;
    private final static int TIME_OUT = 60 * 60 * 1000; // one hour time out
    private final static int UPDATE_AGREE_BASELINE = 50;
    private CommentOperImpl coi = null;
    private CountDownLatch cdl = null;

    private int threadSerialNo;
    private boolean running = true;
    private long beg;

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public CommentThread(CommentOperImpl coi, CountDownLatch cdl, int threadSerialNo)
    {
        this.coi = coi;
        this.cdl = cdl;
        this.threadSerialNo = threadSerialNo;
    }

    @Override
    public void run()
    {
        Page page = null;
        Connection con = null;

        try
        {
            con = ConnectionFactory.getConnection();

            while (running)
            {
                beg = System.currentTimeMillis();
                page = coi.getPage();

                // if page is null, break and end the thread
                if (page == null)
                {
                    log.info(threadSerialNo
                            + ":no page at all, prepare to quit loop");
                    break;
                }

                switch (page.getSource())
                {
                case SINA:
                    try
                    {
                        parseSinaComment(con, page);
                    }
                    catch (Exception ex)
                    {
                        log.info(threadSerialNo + ":error happens in"
                                + page.getUrl());
                        log.error(ex);
                    }
                    break;
                case NETEASE:
                    try
                    {
                        parseNeteaseComment(con, page);
                    }
                    catch (Exception ex)
                    {
                        log.info(threadSerialNo + ":error happens in"
                                + page.getUrl());
                        log.error(ex);
                    }
                    break;
                case SOHU:
                case TENCENT:
                    break;
                default:
                    throw new Exception("page source not found");
                }

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

            log.info(threadSerialNo + ":loop end at "
                    + new Timestamp(System.currentTimeMillis()));
        }

        log.info("end");
    }

    private void parseSinaComment(Connection con, Page page) throws Exception
    {
        String commentPage = null;
        String commentUrl = null;

        StringBuffer buffer = new StringBuffer();

        String commentUrlHead = buffer.append(SinaProperty.commentUrlHead)
                .append(SinaProperty.channelStr).append(page.getChannel())
                .append(SinaProperty.newsidStr).append(page.getId())
                .append(SinaProperty.pageStr).toString();

        log.debug("in parseSinaComment: start parsing " + page.getUrl());

        for (int i = 1; i < MAX_COMMENT_PAGE; i++)
        {

            // if it's timeout, break and set running false
            if (timeoutCheck(beg))
            {
                setRunning(false);
                break;
            }

            commentUrl = commentUrlHead + String.valueOf(i);

            log.debug("in parseSinaComment: start fetchPage " + commentUrl);

            try
            {
                commentPage = WebUtils.fetchPage(commentUrl, null);
            }
            catch (Exception ex)
            {
                throw ex;
            }
            
            log.debug("in parseSinaComment: finsh fetchPage " + commentUrl);

            // if commentPage is null, break
            if (commentPage == null)
            {
                break;
            }

            SinaJson sinaJson = null;
            try
            {
                sinaJson = SinaJsonParser.parse(commentPage);
            }
            catch (Exception ex)
            {
                throw ex;
            }

            // if no comments, break
            if (sinaJson.getResult().getCmntlist() == null
                    || sinaJson.getResult().getCmntlist().size() == 0)
            {
                break;
            }

            List<Comment> cmtList = SinaJsonParser.toComments(sinaJson
                    .getResult().getCmntlist());

            if (cmtList == null)
            {
                log.info("error happens in " + commentUrl);
                log.info("Comment list is null!");
                break;
            }

            persistenceProcess(con, cmtList);

        }
        
        log.debug("in parseSinaComment: finish parsing " + page.getUrl());

    }

    private void parseNeteaseComment(Connection con, Page page)
            throws Exception
    {
        String commentPage = null;
        String commentUrl = null;
        String encoding = "UTF-8";

        StringBuffer buffer = new StringBuffer();
        NeteaseComment ncmt;

        String commentUrlHead = buffer
                .append(NeteaseProperty.neteaseCommentUrlHead)
                .append(page.getCmtUrlKeys()).append("/").append(page.getId())
                .append("_").toString();
        
        log.debug("in parseNeteaseComment: start parsing " + page.getUrl());

        for (int i = 1; i < MAX_COMMENT_PAGE; i++)
        {
            // if it's timeout, break and set running false
            if (timeoutCheck(beg))
            {
                setRunning(false);
                break;
            }

            commentUrl = commentUrlHead + String.valueOf(i) + ".html";

            log.debug("in parseNeteaseComment: start fetchPage " + commentUrl);

            try
            {
                commentPage = WebUtils.fetchPage(commentUrl, encoding);
            }
            catch (Exception ex)
            {
                throw ex;
            }
            
            log.debug("in parseNeteaseComment: finish fetchPage " + commentUrl);


            // if commentPage is null, break
            if (commentPage == null)
            {
                break;
            }

            NeteaseJson json = null;
            try
            {
                json = NeteaseJsonParser.parse(commentPage);
            }
            catch (Exception ex)
            {
                throw ex;
            }

            List<NeteaseComment> list = new ArrayList<NeteaseComment>();

            if (json.getNewPosts() == null)
            {
                break;
            }

            try
            {
                for (int j = 0; j < json.getNewPosts().size(); j++)
                {
                    Map map = json.getNewPosts().get(j);
                    int k = map.size();
                    k = map.get("d") == null ? k : k - 1;
                    ncmt = new NeteaseComment((Map) map.get(String.valueOf(k)));

                    if (k > 1)
                    {
                        NeteaseComment cmt = new NeteaseComment(
                                (Map) map.get(String.valueOf(k - 1)));
                        if(cmt.getPi() != null)
                        {
                            ncmt.setParaent(cmt.getPi());    
                        }
                        
                        cmt = null;
                    }

                    list.add(ncmt);
                    
                    ncmt = null;
                }

                List<Comment> cmtList = NeteaseJsonParser.toComments(list);

                if (cmtList == null)
                {
                    log.info("error happens in " + commentUrl);
                    log.info("Comment list is null!");
                    break;
                }

                persistenceProcess(con, cmtList);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        log.debug("in parseNeteaseComment: finish parsing " + page.getUrl());
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
                    // only the agree number is over the UPDATE_AGREE_BASELINE
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
            dao.insertComments(con, insertList);

            // update the existed data
            dao.updateCommentsSourceProperty(con, updateList);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    private boolean timeoutCheck(long beg)
    {
        return System.currentTimeMillis() - beg > TIME_OUT;
    }

    public static void main(String args[]) throws InterruptedException
    {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        CountDownLatch cdl = new CountDownLatch(5);
        CommentThread thread;
        for (int i = 0; i < 5; i++)
        {
//            thread = new CommentThread(cdl, i);
//            pool.execute(thread);
        }
        cdl.await();
        pool.shutdown();

    }

}
