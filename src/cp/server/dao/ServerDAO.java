package cp.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cp.server.common.Comment;
import cp.server.common.HotComment;
import cp.server.common.Keyword;
import cp.server.common.NewsType;
import cp.server.common.Page;
import cp.server.common.SourceType;

public class ServerDAO
{
    private static int MAXBATCHSIZE = 500;

    private List<Page> exec4FetchingPage(PreparedStatement pstmt)
            throws SQLException
    {
        ResultSet rs = null;
        List<Page> pageList;

        try
        {
            pageList = new ArrayList<Page>();
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                Page page = new Page();

                page.setAvailable(rs.getBoolean("available"));
                page.setChannel(rs.getString("channel"));
                page.setId(rs.getString("pageid"));
                page.setTitle(rs.getString("pagetitle"));
                page.setUrl(rs.getString("url"));
                page.setTime(rs.getTimestamp("time"));
                page.setSource(SourceType.valueOf(rs.getInt("source")));
                page.setNewsType(NewsType.valueOf(rs.getInt("newsType")));
                page.setDescription(rs.getString("description"));
                page.setKeywords(rs.getString("keywords"));
                page.setCmtUrlKeys(rs.getString("cmturlkeys"));

                pageList.add(page);
            }
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }

        return pageList;
    }

    private List<Comment> exec4FetchingCmt(PreparedStatement pstmt)
            throws SQLException
    {
        ResultSet rs = null;
        List<Comment> cmtList;

        try
        {
            cmtList = new ArrayList<Comment>();
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                Comment cmt = new Comment();

                cmt.setCmtId(rs.getString("cmtid"));
                cmt.setContent(rs.getString("content"));
                cmt.setIp(rs.getString("ip"));
                cmt.setPageId(rs.getString("pageid"));
                cmt.setArea(rs.getString("area"));
                cmt.setNick(rs.getString("nick"));
                cmt.setAgree(rs.getInt("agree"));
                cmt.setAgainst(rs.getInt("against"));
                cmt.setTime(rs.getTimestamp("time"));
                cmt.setLocalAgree(rs.getInt("localagree"));
                cmt.setLocalAgainst(rs.getInt("localagainst"));
                cmt.setSource(SourceType.valueOf(rs.getInt("source")));
                cmt.setParent(rs.getString("parent"));

                cmtList.add(cmt);
            }
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }

        return cmtList;
    }

    private List<HotComment> exec4FetchingHotCmt(Connection con,
            PreparedStatement pstmt) throws SQLException
    {
        ResultSet rs = null;
        List<HotComment> hotcmtList;
        List<Comment> cmtList;
        String cmtId = null;

        try
        {
            hotcmtList = new ArrayList<HotComment>();
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                cmtId = rs.getString("cmtid");

                if (cmtId == null || cmtId.trim().equals("")
                        || cmtId.trim().equals("0"))
                {
                    continue;
                }

                cmtList = queryCommentReplyByCmtId(con, cmtId);
                
                //if no reply, it's possibly a valueless comment, next
                if(cmtList == null || cmtList.size() == 0)
                {
                    continue;
                }

                HotComment hotcmt = new HotComment();

                hotcmt.setCmtId(cmtId);
                hotcmt.setPageId(rs.getString("pageid"));
                hotcmt.setPageTitle(rs.getString("pagetitle"));
                hotcmt.setContent(rs.getString("c2.content"));
                hotcmt.setTime(rs.getTimestamp("time"));
                hotcmt.setSource(SourceType.valueOf(rs.getInt("source")));
                hotcmt.setNewsType(NewsType.valueOf(rs.getInt("newsType")));
                hotcmt.setArea(rs.getString("area"));
                hotcmt.setPageDate(rs.getDate("time"));
                hotcmt.setAgree(rs.getInt("agree"));
                hotcmt.setParentContent(rs.getString("c1.content"));

                //TODO: data size is too large, forbid it at the moment
//                hotcmt.setReplyCmt(cmtList.size() == 0 ? null : cmtList);
                hotcmtList.add(hotcmt);
            }
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }

        return hotcmtList;
    }

    public void insertComment(Connection con, Comment comment)
            throws SQLException
    {
        String sql = "insert into comment(cmtid,content,ip,pageid,area,nick,agree,against,time,localagree,localagainst,source,parent) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int index = 1;

        PreparedStatement pstmt = con.prepareStatement(sql);
        try
        {
            pstmt.setString(index++, comment.getCmtId());
            pstmt.setString(index++, comment.getContent());
            pstmt.setString(index++, comment.getIp());
            pstmt.setString(index++, comment.getPageId());
            pstmt.setString(index++, comment.getArea());
            pstmt.setString(index++, comment.getNick());
            pstmt.setInt(index++, comment.getAgree());
            pstmt.setInt(index++, comment.getAgainst());
            pstmt.setTimestamp(index++, comment.getTime());
            pstmt.setInt(index++, comment.getLocalAgree());
            pstmt.setInt(index++, comment.getLocalAgainst());
            pstmt.setInt(index++, comment.getSource().getId());
            pstmt.setString(index++, comment.getParent());

            pstmt.execute();
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }
    }

    public void insertComments(Connection con, List<Comment> comments)
            throws SQLException
    {
        // ignore the error, so that can escape the duplicated unique key error
        String sql = "insert ignore into comment(cmtid,content,ip,pageid,area,nick,agree,against,time,localagree,localagainst,source,parent) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int index = 1;
        int i = 0;
        Comment comment;

        PreparedStatement pstmt = con.prepareStatement(sql);
        try
        {
            while (i < comments.size())
            {
                for (; i % ServerDAO.MAXBATCHSIZE < ServerDAO.MAXBATCHSIZE
                        && i < comments.size(); i++, index = 1)
                {
                    comment = comments.get(i);

                    // if comment is null, set iter size to let it break while;
                    if (comment == null)
                    {
                        i = comments.size();
                        break;
                    }

                    pstmt.setString(index++, comment.getCmtId());
                    pstmt.setString(index++, comment.getContent());
                    pstmt.setString(index++, comment.getIp());
                    pstmt.setString(index++, comment.getPageId());
                    pstmt.setString(index++, comment.getArea());
                    pstmt.setString(index++, comment.getNick());
                    pstmt.setInt(index++, comment.getAgree());
                    pstmt.setInt(index++, comment.getAgainst());
                    pstmt.setTimestamp(index++, comment.getTime());
                    pstmt.setInt(index++, comment.getLocalAgree());
                    pstmt.setInt(index++, comment.getLocalAgainst());
                    pstmt.setInt(index++, comment.getSource().getId());
                    pstmt.setString(index++, comment.getParent());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }
    }

    public void updateCommentsSourceProperty(Connection con,
            List<Comment> comments) throws SQLException
    {
        String sql = "update comment set agree = ?, against = ? where cmtid = ?";
        int index = 1;
        int i = 0;
        Comment comment;

        PreparedStatement pstmt = con.prepareStatement(sql);
        try
        {
            while (i < comments.size())
            {
                for (; i % ServerDAO.MAXBATCHSIZE < ServerDAO.MAXBATCHSIZE
                        && i < comments.size(); i++, index = 1)
                {
                    comment = comments.get(i);

                    // if comment is null, set iter size to let it break while;
                    if (comment == null)
                    {
                        i = comments.size();
                        break;
                    }

                    pstmt.setInt(index++, comment.getAgree());
                    pstmt.setInt(index++, comment.getAgainst());
                    pstmt.setString(index++, comment.getCmtId());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }
    }

    public void setCommentIsValid(Connection con, String cmtId, boolean isValid)
            throws SQLException
    {
        String sql = "update comment set isvalid = ? where cmtid = ?";
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);

            pstmt.setBoolean(1, isValid);
            pstmt.setString(2, cmtId);

            pstmt.execute();
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }
    }

    public void updateCommentsLocalProperty(Connection con,
            List<Comment> comments) throws SQLException
    {
        String sql = "update comment set agree = ?, against = ? where cmtid = ?";
        int index = 1;
        int i = 0;
        Comment comment;

        PreparedStatement pstmt = con.prepareStatement(sql);
        try
        {
            while (i < comments.size())
            {
                for (; i % ServerDAO.MAXBATCHSIZE < ServerDAO.MAXBATCHSIZE
                        && i < comments.size(); i++, index = 1)
                {
                    comment = comments.get(i);
                    pstmt.setInt(index++, comment.getLocalAgree());
                    pstmt.setInt(index++, comment.getLocalAgainst());
                    pstmt.setString(index++, comment.getCmtId());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }
    }

    public boolean isCommentExisted(Connection con, String cmtId)
            throws SQLException
    {
        String sql = "select * from comment where cmtid = ? limit 0,1";
        boolean isExisted = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, cmtId);
            rs = pstmt.executeQuery();
            isExisted = rs.next();
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return isExisted;
    }

    public void insertPages(Connection con, List<Page> pages)
            throws SQLException
    {
        // ignore the error, so that can escape the duplicated unique key error
        String sql = "insert ignore into page values (?,?,?,?,?,?,?,?,?,?,?)";
        int index = 1;
        int i = 0;
        Page page;

        PreparedStatement pstmt = con.prepareStatement(sql);
        try
        {
            while (i < pages.size())
            {
                for (; i % ServerDAO.MAXBATCHSIZE < ServerDAO.MAXBATCHSIZE
                        && i < pages.size(); i++, index = 1)
                {
                    page = pages.get(i);
                    pstmt.setString(index++, page.getId());
                    pstmt.setString(index++, page.getTitle());
                    pstmt.setString(index++, page.getUrl());
                    pstmt.setString(index++, page.getChannel());
                    pstmt.setTimestamp(index++, page.getTime());
                    pstmt.setInt(index++, page.getSource().getId());
                    pstmt.setInt(index++, page.getNewsType().getId());
                    pstmt.setBoolean(index++, page.isAvailable());
                    pstmt.setString(index++, page.getDescription());
                    pstmt.setString(index++, page.getKeywords());
                    pstmt.setString(index++, page.getCmtUrlKeys());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
            }
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }
    }

    public List<Page> queryAllPages(Connection con) throws SQLException
    {
        String sql = "select * from page where available = true";
        List<Page> pageList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pageList = exec4FetchingPage(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return pageList;
    }

    public List<Page> queryPagesByDayInterval(Connection con, int interval)
            throws SQLException
    {
        String sql = "select * from page where time between date_sub(now(),interval #interval# day) and now() and available = true";
        List<Page> pageList = null;
        PreparedStatement pstmt = null;

        try
        {
            sql = sql.replaceFirst("#interval#", String.valueOf(interval));

            pstmt = con.prepareStatement(sql);
            pageList = exec4FetchingPage(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return pageList;
    }

    public List<Page> queryPagesByIntervalAndSource(Connection con,
            int interval, SourceType source) throws SQLException
    {
        String sql = "select * from page where time between date_sub(now(),interval #interval# day) and now() and source = ?  and available = true";
        List<Page> pageList = null;
        PreparedStatement pstmt = null;

        try
        {
            sql = sql.replaceFirst("#interval#", String.valueOf(interval));

            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, source.getId());
            pageList = exec4FetchingPage(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return pageList;
    }

    public List<Page> queryPagesByIntervalAndType(Connection con, int interval,
            SourceType source, NewsType type) throws SQLException
    {
        String sql = "select * from page where time between date_sub(now(),interval #interval# day) and now() and source = ? and newstype = ?  and available = true";
        List<Page> pageList = null;
        PreparedStatement pstmt = null;

        try
        {
            sql = sql.replaceFirst("#interval#", String.valueOf(interval));

            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, source.getId());
            pstmt.setInt(2, type.getId());

            pageList = exec4FetchingPage(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return pageList;
    }

    public List<Page> queryPagesInThreeDays(Connection con) throws SQLException
    {
        String sql = "select * from page where time between date_sub(now(),interval 3 day) and now()  and available = true";
        List<Page> pageList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pageList = exec4FetchingPage(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return pageList;
    }

    public Map<Keyword, Integer> queryKeywordsByInterval(Connection con,
            int dayInterval) throws SQLException
    {
        String sql = "select p.keywords,count(*),p.pageid,p.time,p.pagetitle,p.newstype,p.url from comment c, page p where p.pageid = c.pageid  and p.available = true and p.time > date_sub(now(),interval #interval# day) group by p.pageid order by count(*) desc limit 150";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<Keyword, Integer> keyMap = new HashMap<Keyword, Integer>(300);
        String keywords = null;
        Keyword keyword = null;
        Page page = null;
        int count;

        try
        {
            sql = sql.replaceFirst("#interval#", String.valueOf(dayInterval));

            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                keywords = rs.getString(1);

                if (keywords == null)
                {
                    continue;

                }

                page = new Page();

                count = rs.getInt(2);

                page.setKeywords(keywords);
                page.setId(rs.getString(3));
                page.setTime(rs.getTimestamp(4));
                page.setTitle(rs.getString(5));
                page.setNewsType(NewsType.valueOf(rs.getInt(6)));
                page.setUrl(rs.getString(7));

                keyword = new Keyword(keywords, page);

                keyMap.put(keyword, count);

            }

        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return keyMap;
    }

    public List<Comment> queryCommentByPageIdOrderByAgree(Connection con,
            String pageId) throws SQLException
    {
        String sql = "select * from comment where pageid = ? order by agree desc limit 200";
        List<Comment> cmtList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, pageId);

            cmtList = exec4FetchingCmt(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return cmtList;
    }

    public List<Comment> queryCommentByPageIdOrderByTime(Connection con,
            String pageId) throws SQLException
    {
        String sql = "select * from comment where pageid = ? order by time desc limit 200";
        List<Comment> cmtList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, pageId);

            cmtList = exec4FetchingCmt(pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return cmtList;
    }

    public List<Comment> queryCommentReplyByCmtId(Connection con, String cmtId)
            throws SQLException
    {
        String sql = "select distinct content,cmtid,ip,pageid,area,nick,agree,against,time,localagree,localagainst,source,parent from comment where parent = ? order by agree desc limit 100";
        List<Comment> cmtList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, cmtId);

            cmtList = exec4FetchingCmt(pstmt);

        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return cmtList;
    }

    public List<HotComment> queryRealTimeHotComment(Connection con)
            throws SQLException
    {
        // String sql =
        // "select c.cmtid,c.content,c.agree,c.area,c.time,p.pagetitle,p.newstype,p.source,p.pageid,p.source,p.time from comment c, page p where p.pageid = c.pageid and c.time > date_sub(now(),interval 2 hour) order by c.agree desc, c.time desc limit 100";
        String sql = "select c2.*,c1.content from (select c.cmtid,c.content,c.agree,c.area,c.time,c.parent,p.pagetitle,p.newstype,p.pageid,p.source from comment c, page p where p.pageid = c.pageid and c.isvalid = true and p.available = true and c.time > date_sub(now(),interval 2 hour) order by c.agree desc, c.time desc limit 100) c2 left join comment c1 on c2.parent = c1.cmtid";
        List<HotComment> hotcommentList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            hotcommentList = exec4FetchingHotCmt(con, pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return hotcommentList;
    }

    public List<HotComment> queryHotCommentByType(Connection con,
            NewsType newsType) throws SQLException
    {
        // String sql =
        // "select c.cmtid,c.content,c.agree,c.area,c.time,p.pagetitle,p.newstype,p.source,p.pageid,p.time from comment c, page p where p.pageid = c.pageid and c.time > date_sub(now(),interval 2 hour) and p.newstype = ? order by c.agree desc limit 80";
        String sql = "select c2.*,c1.content from (select c.cmtid,c.content,c.agree,c.area,c.time,c.parent,p.pagetitle,p.newstype,p.pageid,p.source from comment c, page p where p.pageid = c.pageid and c.isvalid = true and p.available = true and c.time > date_sub(now(),interval 2 hour) and p.newstype = ? order by c.agree desc, c.time desc limit 80) c2 left join comment c1 on c2.parent = c1.cmtid";
        List<HotComment> hotcommentList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, newsType.getId());

            hotcommentList = exec4FetchingHotCmt(con, pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return hotcommentList;
    }

    public List<HotComment> queryHotCommentByKeyword(Connection con,
            String keyword) throws SQLException
    {
        // TODO: 需要替换的语句，回来写,#pageid#为keyword的pageidList
        // TODO: 可以考虑改写为pl
        // String sql =
        // "select distinct c.content,c.cmtid,c.agree,c.area,c.time,p.pagetitle,p.newstype,p.source,p.pageid,p.time from comment c,page p where c.pageid = p.pageid and c.time > date_sub(now(),interval 12 hour) and p.pageid in (#pageid#) order by c.agree desc, c.time desc limit 300"
        String sql = "select c2.*,c1.content from (select distinct c.content,c.cmtid,c.agree,c.area,c.time,c.parent,p.pagetitle,p.newstype,p.source,p.pageid from comment c,page p where c.pageid = p.pageid and c.isvalid = true and p.available = true and c.time > date_sub(now(),interval 2 hour) and p.keywords like ? order by c.agree desc, c.time desc limit 300) c2 left join comment c1 on c2.parent = c1.cmtid;";
        List<HotComment> hotcommentList = null;
        PreparedStatement pstmt = null;

        try
        {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");

            hotcommentList = exec4FetchingHotCmt(con, pstmt);
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return hotcommentList;
    }

    public void cleanCommentsInExpiredPage(Connection con, int dayInterval)
            throws SQLException
    {
        String sql = "delete from comment where pageid in (select p.pageid from page p where p.time < date_sub(now(),interval #interval# day))";
        PreparedStatement pstmt = null;

        try
        {
            if (dayInterval <= 0)
            {
                return;
            }

            sql = sql.replaceFirst("#interval#", String.valueOf(dayInterval));

            pstmt = con.prepareStatement(sql);
            pstmt.execute();
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return;
    }

    public void cleanExpiredPage(Connection con, int dayInterval)
            throws SQLException
    {
        String sql = "delete from page where time < date_sub(now(),interval #interval# day)";
        PreparedStatement pstmt = null;

        try
        {
            if (dayInterval <= 0)
            {
                return;
            }

            sql = sql.replaceFirst("#interval#", String.valueOf(dayInterval));

            pstmt = con.prepareStatement(sql);
            pstmt.execute();
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
        }

        return;
    }

    public static void main(String args[]) throws SQLException,
            ClassNotFoundException
    {
        ServerDAO dao = new ServerDAO();
        List<Comment> list = new ArrayList<Comment>();

        for (int i = 0; i < 5023; i++)
        {
            Comment comment = new Comment();
            comment.setCmtId("123123123");
            comment.setAgainst(10);
            comment.setAgree(15);
            comment.setArea("SH");
            comment.setContent("你好，世界上的第一条信息");
            comment.setIp("127.0.0.1");
            comment.setLocalAgainst(10);
            comment.setLocalAgree(16);
            comment.setNick("nick");
            comment.setPageId("123333333-1231023123");
            comment.setTime(new Timestamp(System.currentTimeMillis()));
            list.add(comment);
        }

        List<Page> pageList = new ArrayList<Page>();

        Page page = new Page(SourceType.SINA, NewsType.ENT, "");
        page.setDescription("holyshit desc");
        page.setKeywords("goddamn keywords");
        page.setTitle("holy title");
        page.setCmtUrlKeys("shitshitshit");

        pageList.add(page);

        // 第一步：加载MySQL的JDBC的驱动
        Class.forName("com.mysql.jdbc.Driver");

        // 取得连接的url,能访问MySQL数据库的用户名,密码；studentinfo：数据库名
        String url = "jdbc:mysql://localhost:3306/cmtfinder?useUnicode=true&characterEncoding=gbk";
        String username = "root";
        String password = "";

        // 第二步：创建与MySQL数据库的连接类的实例
        Connection con = DriverManager.getConnection(url, username, password);

        dao.insertPages(con, pageList);
    }

}
