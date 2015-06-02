package cp.server.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cp.server.common.Comment;
import cp.server.common.HotComment;
import cp.server.common.NewsType;
import cp.server.common.SystemProperty;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.FileUtils;
import cp.server.util.FtpUtils;

public class HotCommentByTypeFinder
{
    private static Map<NewsType, List<HotComment>> HotCmts = new HashMap<NewsType, List<HotComment>>(
            NewsType.values().length);;

    public void go()
    {
        ServerDAO dao = new ServerDAO();
        List<HotComment> list;
        List<Comment> latestList;
        List<Comment> hotList;
        Connection con = null;
        String fileName = null;

        try
        {
            con = ConnectionFactory.getConnection();

            for (NewsType newsType : NewsType.values())
            {
                list = dao.queryHotCommentByType(con, newsType);

                HotCmts.put(newsType, list);

                for (HotComment cmt : list)
                {
                    String pageId = cmt.getPageId();
                    String pageDate = cmt.getPageDate().toString();

                    hotList = dao.queryCommentByPageIdOrderByAgree(con, pageId);
                    latestList = dao.queryCommentByPageIdOrderByTime(con,
                            pageId);

                    fileName = SystemProperty.HotListFileName
                            + "_" + pageDate + "_" + pageId;
                    FileUtils.writeOjectToFile(fileName, hotList);
                    FileUploader.putFileName(fileName);

                    fileName = SystemProperty.LatestListFileName + "_" + pageDate
                            + "_" + pageId;
                    FileUtils.writeOjectToFile(fileName, latestList);
                    FileUploader.putFileName(fileName);
                    
                    // ftp.uploadFile(SystemProperty.HotListFileName + "_"
                    // + pageDate + "_" + pageId);
                    // ftp.uploadFile(SystemProperty.LatestListFileName + "_"
                    // + pageDate + "_" + pageId);
                }
            }

            fileName = SystemProperty.HotCommentFileName;
            FileUtils.writeOjectToFile(fileName, HotCmts);
            FileUploader.putFileName(fileName);
            
            // ftp.uploadFile(SystemProperty.HotCommentFileName);
//            try
//            {
//                SFtpUtils.upload(SystemProperty.OPENSHIFTHOSTNAME,
//                        SystemProperty.OPENSHIFTUSERNAME, "",
//                        SystemProperty.HotCommentFileName,
//                        SystemProperty.OPENSHIFTREMOTEFILEPATH
//                                + SystemProperty.HotCommentFileName,
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
//                SFtpUtils
//                        .upload(SystemProperty.HUAWEIHOSTNAME,
//                                SystemProperty.HUAWEIUSERNAME,
//                                SystemProperty.HUAWEIPASSWORD,
//                                SystemProperty.HotCommentFileName,
//                                SystemProperty.HUAWEIREMOTEFILEPATH
//                                        + SystemProperty.HotCommentFileName,
//                                null, null);
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }

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

    public static void main(String args[]) throws FileNotFoundException,
            ClassNotFoundException, IOException
    {
        HotCommentByTypeFinder r = new HotCommentByTypeFinder();
        r.go();

        List<HotComment> list = (List<HotComment>) FileUtils
                .readObjectFromFile(SystemProperty.HotCommentFileName + 0);

        System.out.println(list.size());
    }
}
