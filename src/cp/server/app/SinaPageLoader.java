package cp.server.app;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cp.server.common.NewsType;
import cp.server.common.Page;
import cp.server.common.Property;
import cp.server.common.SourceType;
import cp.server.common.sina.SinaProperty;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;
import cp.server.util.FileUtils;
import cp.server.util.RegParser;
import cp.server.util.WebUtils;

public class SinaPageLoader implements PageLoader
{
    private final static int PAGEMAP_INITIAL_NUM = 50000;
    private final static int FLUSH_NUM = 1000;

    String pageType = ".shtml";
    String pageDateReg = "201([3-9]{1})([0-9,-]{6})";
    String pageReg = "/([0-9,a-z,A-Z,/]*)/" + pageDateReg + "/([0-9]+)"
            + pageType;
    String pageContentMark = "content=\"";
    String pageCmtIdSplitter = ":";
    String pageCmtIdContentReg = "[a-z]{2}" + pageCmtIdSplitter + "[0-9,-]+";
    String pageCmtIdReg = "<meta name=[\"]?comment[\"]?[ ]*content=\"[^\"]*\"";
    String pageTitleMark = "<title>";
    String pageTitleKeyReg = pageTitleMark + "[^_|(]*";
    String pageTitleReg = pageTitleMark + "[^<]*";
    String pageDescKeyReg = pageContentMark + "[^\"]+";
    String pageDescReg = "<meta name=[\"]?description[\"]?[ ]*"
            + pageContentMark + "[^\"]+\"";
    String pageKeywordsKeyReg = pageContentMark + "[^\"]+";
    String pageKeywordsReg = "<meta name=[\"]?keywords[\"]?[ ]*"
            + pageContentMark + "[^\"]+\"";

    Map<String, Page> pageMap = new HashMap<String, Page>(PAGEMAP_INITIAL_NUM);
    Set<String> webSet = null;
    List<Page> pageList = new ArrayList<Page>(FLUSH_NUM);
    int countFlush = 0;
    Property property = new SinaProperty();

    @Override
    public void loadPages(boolean isFullAccess) throws LoaderException
    {
        try
        {
            // webSet = (Set<String>) FileUtils.readObjectFromFile("webSet");
            webSet = SinaProperty.getHotWebSet();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new LoaderException("can't read from webSet");
        }

        if (webSet == null)
        {
            throw new LoaderException("webSet is null!");
        }

        if (isFullAccess)
        {
            pageMap = new HashMap<String, Page>(PAGEMAP_INITIAL_NUM);
        }
        else
        {
            try
            {
                pageMap = (Map<String, Page>) FileUtils
                        .readObjectFromFile(property.getPageMapName());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new LoaderException("can't read from pageMap");
            }

            // set the pageDateReg to today
            String date = new Timestamp(System.currentTimeMillis()).toString()
                    .substring(0, 10);
            pageReg = pageReg.replace(pageDateReg, date);
        }

        // TODO: can be multithread process
        // look for all pages
        lookForPages();
        persistenceProcess();

    }

    // base on the current web set
    private void lookForPages()
    {
        NewsType type;
        String url;
        String root;

        for (String typeWithUrl : webSet)
        {
            type = NewsType
                    .valueOf(Integer.valueOf(typeWithUrl.substring(0, 1)));
            root = property.getSiteByType(type);
            url = typeWithUrl.substring(1);

            fetchPages(root, url, type);
        }

    }

    private void fetchPages(String root, String url, NewsType type)
    {

        String webContent = null;

        try
        {
            webContent = WebUtils.fetchPage(url, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (webContent == null)
        {
            return;
        }

        Set<String> pageSet = RegParser.parseReg(webContent, root + pageReg);

        for (String pageUrl : pageSet)
        {
            addPage(pageUrl, type);
        }

        pageSet = RegParser.parseReg(webContent, "\"" + pageReg);

        for (String pageUrl : pageSet)
        {
            addPage(root + pageUrl.substring(1), type);
        }

    }

    private void addPage(String pageUrl, NewsType type)
    {
        if (!this.pageMap.containsKey(pageUrl))
        {
            // every FLUSH_NUM flush
            if (++countFlush > FLUSH_NUM)
            {
                countFlush = 1;
                persistenceProcess();
            }

            // take the hole
            this.pageMap.put(pageUrl, null);
            Page page = createPage(type, pageUrl);
            this.pageMap.put(pageUrl, page);
            this.pageList.add(page);
        }
    }

    // look for news-id(comment-id) and channel in the content
    private Page createPage(NewsType type, String url)
    {
        String webContent = null;
        String result = null;
        String pageId = null;
        String channel = null;
        String title = null;
        String desc = null;
        String keywords = null;
        String date = null;

        Page page = new Page(SourceType.SINA, type, url);

        try
        {
            webContent = WebUtils.fetchPage(url, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (webContent == null)
        {
            page.setAvailable(false);
            return page;
        }

        result = RegParser.findOne(webContent, pageCmtIdReg);

        if (result == null)
        {
            page.setAvailable(false);
            return page;
        }

        result = RegParser.findOne(result, pageCmtIdContentReg);

        if (result == null)
        {
            page.setAvailable(false);
            return page;
        }

        // set pageId
        pageId = result.substring(result.indexOf(pageCmtIdSplitter) + 1);
        page.setId(pageId);

        // set channel
        channel = result.substring(0, result.indexOf(pageCmtIdSplitter));
        page.setChannel(channel);

        result = RegParser.findOne(webContent, pageTitleReg);

        if (result == null)
        {
            page.setAvailable(false);
            return page;
        }

        // set title
        title = RegParser.findOne(result, pageTitleKeyReg).substring(
                pageTitleMark.length());
        page.setTitle(title);

        result = RegParser.findOne(webContent, pageDescReg);

        if (result != null)
        {
            // set description
            desc = RegParser.findOne(result, pageDescKeyReg).substring(
                    pageContentMark.length());
            page.setDescription(desc);
        }

        result = RegParser.findOne(webContent, pageKeywordsReg);

        if (result != null)
        {
            // set keywords
            keywords = RegParser.findOne(result, pageKeywordsKeyReg).substring(
                    pageContentMark.length());
            page.setKeywords(keywords);
        }

        // set date
        date = RegParser.findOne(url, pageDateReg);
        page.setTime(new Timestamp(Date.valueOf(date).getTime()));

        return page;
    }

    private void persistenceProcess()
    {
        Connection con = null;

        try
        {
            con = ConnectionFactory.getConnection();

            ServerDAO dao = new ServerDAO();
            dao.insertPages(con, pageList);

            System.out.println("page list size is " + pageList.size());

            // abandon the persisted data, make a new list for page
            pageList = new ArrayList<Page>(FLUSH_NUM);

            FileUtils.writeOjectToFile(property.getPageMapName(), pageMap);

            System.out.println("page map size is " + pageMap.size());
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
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws LoaderException
    {
        PageLoader p = new SinaPageLoader();
        p.loadPages(false);
    }

}
