package cp.server.jobs;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cp.server.common.Comment;
import cp.server.common.HotComment;
import cp.server.common.SystemProperty;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.FileUtils;

public class RealTimeHotCommentFinder
{
    public void go()
    {
        ServerDAO dao = new ServerDAO();
        List<HotComment> list;
        List<Comment> latestList;
        List<Comment> hotList;
        List<Comment> replyList;
        Set<String> pageIdSet = new HashSet<String>();
        Connection con = null;
        String fileName = null;

        try
        {
            con = ConnectionFactory.getConnection();
            list = dao.queryRealTimeHotComment(con);

            fileName = SystemProperty.RealTimeHotCommentFileName;

            FileUtils.writeOjectToFile(fileName, list);
            
            FileUploader.putFileName(fileName);

            // ftp.uploadFile(SystemProperty.RealTimeHotCommentFileName);

//            try
//            {
//                SFtpUtils.upload(SystemProperty.OPENSHIFTHOSTNAME,
//                        SystemProperty.OPENSHIFTUSERNAME, "",
//                        SystemProperty.RealTimeHotCommentFileName,
//                        SystemProperty.OPENSHIFTREMOTEFILEPATH
//                                + SystemProperty.RealTimeHotCommentFileName,
//                        SystemProperty.OPENSHIFTPPKPATH,
//                        SystemProperty.OPENSHIFTPPKPWD);
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//
//            try
//            {
//                SFtpUtils.upload(SystemProperty.HUAWEIHOSTNAME,
//                        SystemProperty.HUAWEIUSERNAME,
//                        SystemProperty.HUAWEIPASSWORD,
//                        SystemProperty.RealTimeHotCommentFileName,
//                        SystemProperty.HUAWEIREMOTEFILEPATH
//                                + SystemProperty.RealTimeHotCommentFileName,
//                        null, null);
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }

            String pageId = null;
            String pageDate = null;
            String cmtId = null;

            for (HotComment cmt : list)
            {
                pageId = cmt.getPageId();
                pageDate = cmt.getPageDate().toString();
                cmtId = cmt.getCmtId();

                if (pageIdSet.contains(pageId))
                {
                    pageIdSet.add(pageId);
                }
                else
                {
                    hotList = dao.queryCommentByPageIdOrderByAgree(con, pageId);
                    latestList = dao.queryCommentByPageIdOrderByTime(con,
                            pageId);

                    fileName = SystemProperty.HotListFileName + "_" + pageDate
                            + "_" + pageId;
                    FileUtils.writeOjectToFile(fileName, hotList);
                    FileUploader.putFileName(fileName);

                    fileName = SystemProperty.LatestListFileName + "_"
                            + pageDate + "_" + pageId;
                    FileUtils.writeOjectToFile(fileName, latestList);
                    FileUploader.putFileName(fileName);

                    // ftp.uploadFile(SystemProperty.HotListFileName + "_" +
                    // pageDate
                    // + "_" + pageId);
                    // ftp.uploadFile(SystemProperty.LatestListFileName + "_"
                    // + pageDate + "_" + pageId);

                }

                replyList = dao.queryCommentReplyByCmtId(con, cmtId);

                if (replyList.size() > 0)
                {
                    fileName = SystemProperty.REPLY_LIST_FILE_NAME + "_"
                            + pageDate + "_" + cmtId;
                    FileUtils.writeOjectToFile(fileName, replyList);

                    FileUploader.putFileName(fileName);
                }

            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (con != null)
                {
                    con.close();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String args[])
    {
        RealTimeHotCommentFinder r = new RealTimeHotCommentFinder();
        r.go();
    }
}
