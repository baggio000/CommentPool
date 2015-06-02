package cp.server.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import cp.server.common.Comment;
import cp.server.common.HotComment;
import cp.server.common.Keyword;
import cp.server.common.Page;
import cp.server.common.SystemProperty;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.FileUtils;

public class KeywordFinder
{
    private static Map<String, Integer> keyMap;
    private static final int MAXKEYWORDNUM = 100;
    private static final int MINKEYWORDCMTNUM = 1000;

    // some words should be forbidden from showing
    private String[] blackWords =
    { "今日网言", "每周一星", "毒舌美少女" };

    private String[] blackConnWords =
    { "中国" };

    public static Map<String, Integer> getKeyMap()
    {
        return keyMap;
    }

    public static void setKeyMap(Map<String, Integer> keyMap)
    {
        KeywordFinder.keyMap = keyMap;
    }

    public void go()
    {
        Map<Keyword, Integer> kMap = getKeywordsMap();
        Map<String, Keyword> map = reduceMap(kMap);

        TreeSet<Keyword> treeSet = new TreeSet<Keyword>(
                new Comparator<Keyword>() {
                    @Override
                    public int compare(Keyword k1, Keyword k2)
                    {
                        return k2.getCount() - k1.getCount();
                    }
                });

        Iterator<Entry<String, Keyword>> iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<String, Keyword> entry = iter.next();
            String key = entry.getKey();
            Keyword keyword = entry.getValue();
            int val = keyword.getCount();

            // block the black word and should be reach the min cmt num
            if (val > MINKEYWORDCMTNUM && !isBlackConnWord(key))
            {
                compareNStore4TreeSet(treeSet, keyword);

                // System.out.println("key is " + key + " val is " + val);
            }
        }

        Object[] keyArray = treeSet.toArray();
        int keywordListSize = keyArray.length > MAXKEYWORDNUM ? MAXKEYWORDNUM
                : keyArray.length;

        List<Keyword> keyList = new ArrayList<Keyword>(keywordListSize);

        if (keywordListSize == 0)
        {
            System.out.println("keyword list size is 0.");
            return;
        }

        /*
         * no combine for pageid for (int i = 0; i < keywordListSize; i++) {
         * keyList.add((Keyword) keyArray[i]); System.out.println("key is " +
         * keyList.get(i).getKeyword() + " val is " +
         * keyList.get(i).getCount()); }
         */

        keyList.add((Keyword) keyArray[0]);
        for (int i = 1; i < keywordListSize; i++)
        {
            boolean isSame = false;

            for (int j = 0; j < keyList.size(); j++)
            {
                Keyword curtKW = (Keyword) keyArray[i];
                Keyword cmpKW = keyList.get(j);

                if (cmpKW.getPageList().size() < curtKW.getPageList().size())
                {
                    continue;
                }

                for (Page curtPage : curtKW.getPageList())
                {
                    for (Page cmpPage : cmpKW.getPageList())
                    {
                        if (curtPage.getId().equals(cmpPage.getId()))
                        {
                            isSame = true;
                            break;
                        }
                    }

                    if (isSame)
                    {
                        break;
                    }
                }

                if (isSame)
                {
                    boolean isPageIdSame = false;

                    for (Page curtPage : curtKW.getPageList())
                    {
                        for (Page cmpPage : cmpKW.getPageList())
                        {
                            if (curtPage.getId().equals(cmpPage.getId()))
                            {
                                isPageIdSame = true;
                                break;
                            }
                        }

                        if (!isPageIdSame)
                        {
                            cmpKW.getPageList().add(curtPage);
                        }
                        else
                        {
                            isPageIdSame = false;
                        }
                    }

                    String keyword = cmpKW.getKeyword();
                    keyword = keyword + " " + curtKW.getKeyword();
                    cmpKW.setKeyword(keyword);
                    break;
                }

            }

            if (!isSame)
            {
                keyList.add((Keyword) keyArray[i]);
                // System.out.println("key is " + keyList.get(i).getKeyword()
                // + " val is " + keyList.get(i).getCount());
            }
        }

        List<List<HotComment>> keywordHotCmtList = new ArrayList<List<HotComment>>(
                keywordListSize);
        ServerDAO dao = new ServerDAO();
        List<Comment> latestList;
        List<Comment> hotList;

        Connection con = null;

        try
        {
            con = ConnectionFactory.getConnection();

            /*
             * no need to query hot cmts for keywords Set<String>
             * keywordPageIdSet = new HashSet<String>(); String keyword; int
             * idx; List<HotComment> hotCmts;
             * 
             * for (int i = 0; i < keywordListSize; i++) { keyword = ((Keyword)
             * keyList.get(i)).getKeyword();
             * 
             * if ((idx = keyword.indexOf(" ")) > 0) { keyword =
             * keyword.substring(0, idx); }
             * 
             * hotCmts = dao.queryHotCommentByKeyword(con, keyword);
             * 
             * keywordHotCmtList.add(i, hotCmts);
             * 
             * for (int j = 0; j < hotCmts.size(); j++) {
             * keywordPageIdSet.add(hotCmts.get(j).getTime().toString()
             * .substring(0, 10) + hotCmts.get(j).getPageId()); } }
             */

            for (Keyword keyword : keyList)
            {
                for (int i = 0; i < keyword.getPageList().size(); i++)
                {
                    String pageId = keyword.getPageList().get(i).getId();
                    String pageDate = keyword.getPageList().get(i).getTime()
                            .toString().substring(0, 10);

                    hotList = dao.queryCommentByPageIdOrderByAgree(con, pageId);
                    latestList = dao.queryCommentByPageIdOrderByTime(con,
                            pageId);

                    FileUtils.writeOjectToFile(SystemProperty.HotListFileName
                            + "_" + pageDate + "_" + pageId, hotList);
                    FileUtils.writeOjectToFile(
                            SystemProperty.LatestListFileName + "_" + pageDate
                                    + "_" + pageId, latestList);
                    // ftp.uploadFile(SystemProperty.HotListFileName + "_"
                    // + pageDate + "_" + pageId);
                    // ftp.uploadFile(SystemProperty.LatestListFileName + "_"
                    // + pageDate + "_" + pageId);
                }

            }

            String fileName = null;

            try
            {
                fileName = SystemProperty.KeywordListFileName;
                FileUtils.writeOjectToFile(fileName, keyList);
                FileUploader.putFileName(fileName);

                fileName = SystemProperty.KeywordCommentListFileName;
                FileUtils.writeOjectToFile(fileName, keywordHotCmtList);
                FileUploader.putFileName(fileName);

                // ftp.uploadFile(SystemProperty.KeywordListFileName);
                // ftp.uploadFile(SystemProperty.KeywordCommentListFileName);
//                try
//                {
//                    SFtpUtils.upload(SystemProperty.OPENSHIFTHOSTNAME,
//                            SystemProperty.OPENSHIFTUSERNAME, "",
//                            SystemProperty.KeywordListFileName,
//                            SystemProperty.OPENSHIFTREMOTEFILEPATH
//                                    + SystemProperty.KeywordListFileName,
//                            SystemProperty.OPENSHIFTPPKPATH,
//                            SystemProperty.OPENSHIFTPPKPWD);
//                    SFtpUtils
//                            .upload(SystemProperty.OPENSHIFTHOSTNAME,
//                                    SystemProperty.OPENSHIFTUSERNAME,
//                                    "",
//                                    SystemProperty.KeywordCommentListFileName,
//                                    SystemProperty.OPENSHIFTREMOTEFILEPATH
//                                            + SystemProperty.KeywordCommentListFileName,
//                                    SystemProperty.OPENSHIFTPPKPATH,
//                                    SystemProperty.OPENSHIFTPPKPWD);
//                }
//                catch (Exception ex)
//                {
//                    ex.printStackTrace();
//                }
//
//                try
//                {
//                    SFtpUtils.upload(SystemProperty.HUAWEIHOSTNAME,
//                            SystemProperty.HUAWEIUSERNAME,
//                            SystemProperty.HUAWEIPASSWORD,
//                            SystemProperty.KeywordListFileName,
//                            SystemProperty.HUAWEIREMOTEFILEPATH
//                                    + SystemProperty.KeywordListFileName, null,
//                            null);
//
//                    SFtpUtils
//                            .upload(SystemProperty.HUAWEIHOSTNAME,
//                                    SystemProperty.HUAWEIUSERNAME,
//                                    SystemProperty.HUAWEIPASSWORD,
//                                    SystemProperty.KeywordCommentListFileName,
//                                    SystemProperty.HUAWEIREMOTEFILEPATH
//                                            + SystemProperty.KeywordCommentListFileName,
//                                    null, null);
//                }
//                catch (Exception ex)
//                {
//                    ex.printStackTrace();
//                }

            }
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<Keyword, Integer> getKeywordsMap()
    {
        Map<Keyword, Integer> keyMap = null;
        ServerDAO dao = new ServerDAO();
        Connection con = null;
        Time ts = new Time(System.currentTimeMillis());
        int interval;

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

            keyMap = dao.queryKeywordsByInterval(con, interval);

        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                    e.printStackTrace();
                }
            }
        }

        return keyMap;
    }

    public Map<String, Keyword> reduceMap(Map<Keyword, Integer> keyMap)
    {
        Map<String, Keyword> reduceMap = new HashMap<String, Keyword>();

        if (keyMap == null)
        {
            return null;
        }

        Iterator<Entry<Keyword, Integer>> iter = keyMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Entry<Keyword, Integer> entry = iter.next();
            Keyword key = entry.getKey();
            int val = entry.getValue();

            if (key == null)
            {
                System.out.println("shit happens!");
                continue;
            }

            String keyword = key.getKeyword();
            Page page = key.getPage();

            int idx = keyword.indexOf(",");

            // it's possibly a topic, delete it
            // I suppose it length 20
            if (idx > 20)
            {
                keyword = keyword.substring(idx + 1);
            }

            // block the black words
            if (isBlackWord(keyword))
            {
                continue;
            }

            String[] keys = keyword.split(",");

            for (String k : keys)
            {
                k = k.toUpperCase();

                if (k.indexOf(" ") > 0)
                {
                    String[] spaceKeys = k.split(" ");
                    for (String sk : spaceKeys)
                    {
                        compareNStore(reduceMap, sk, val, page);
                    }

                }
                else
                {
                    compareNStore(reduceMap, k, val, page);
                }
            }
        }

        return reduceMap;
    }

    private void compareNStore(Map<String, Keyword> reduceMap, String key,
            int val, Page page)
    {
        // keyword and news type same
        if (reduceMap.containsKey(key)
                && reduceMap.get(key).getPageList().get(0).getNewsType() == page
                        .getNewsType())
        {
            int tmpVal = val;
            tmpVal += reduceMap.get(key).getCount();
            reduceMap.get(key).setCount(tmpVal);
            reduceMap.get(key).getPageList().add(page);
        }
        else
        {
            List<Page> pageList = new ArrayList<Page>();
            pageList.add(page);
            Keyword keywordVal = new Keyword(key, pageList);
            keywordVal.setCount(val);
            reduceMap.put(key, keywordVal);
        }
    }

    private void compareNStore4TreeSet(TreeSet<Keyword> treeSet, Keyword keyword)
    {
        if (treeSet.contains(keyword))
        {
            Keyword k1 = treeSet.ceiling(keyword);

            if (comparePageIdList(k1.getPageList(), keyword.getPageList()))
            {
                String temp = k1.getKeyword() + " " + keyword.getKeyword();
                k1.setKeyword(temp);
            }
            else
            {
                // very very bad code, but it's an easy way
                // plus one to make it different
                keyword.setCount(keyword.getCount() + 1);
                compareNStore4TreeSet(treeSet, keyword);
            }
        }
        else
        {
            treeSet.add(keyword);
        }
    }

    private boolean comparePageIdList(List<Page> list, List<Page> cmpList)
    {
        boolean result = true;

        if (list.size() == cmpList.size())
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (!list.get(i).getId().equals(cmpList.get(i).getId()))
                {
                    result = false;
                    break;
                }
            }
        }
        else
        {
            result = false;
        }

        return result;
    }

    private boolean isBlackWord(String word)
    {
        for (int i = 0; i < blackWords.length; i++)
        {
            if (word.contains(blackWords[i]))
            {
                return true;
            }
        }

        return false;
    }

    private boolean isBlackConnWord(String word)
    {
        for (int i = 0; i < blackConnWords.length; i++)
        {
            if (word.contains(blackConnWords[i]))
            {
                return true;
            }
        }

        return false;
    }

    public static void main(String args[])
    {
        KeywordFinder k = new KeywordFinder();

        k.go();
    }
}
