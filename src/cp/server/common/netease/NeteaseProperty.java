package cp.server.common.netease;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cp.server.common.NewsType;
import cp.server.common.Property;

public class NeteaseProperty implements Property
{
    private static Map<NewsType, String> urls = new HashMap<NewsType, String>();
    private static Set<String> hotWebSet = new HashSet<String>();

    // TODO: should be loaded from xml
    static
    {
        //urls.put(NewsType.NEWS, "http://news.163.com");
        //urls.put(NewsType.ENT, "http://ent.163.com");
        //urls.put(NewsType.FINANCE, "http://money.163.com");
        urls.put(NewsType.SPORTS, "http://sports.163.com");
        //urls.put(NewsType.TECH, "http://tech.163.com");

        //hotWebSet.add(NewsType.NEWS.getId() + "http://news.163.com");
        //hotWebSet.add(NewsType.ENT.getId() + "http://ent.163.com");
        //hotWebSet.add(NewsType.FINANCE.getId() + "http://money.163.com");
        hotWebSet.add(NewsType.SPORTS.getId() + "http://sports.163.com");
        //hotWebSet.add(NewsType.TECH.getId() + "http://tech.163.com");
    }

    private static String webSetName = "NeteaseWebSet";
    private static String pageMapName = "NeteasePageMap";
    private static String webReg = "/([0-9,a-z,A-Z]*/{0,1})([0-9,a-z,A-Z]*/{0,1})([0-9,a-z,A-Z]*)/\"";
    
    //comment url properties
    public static final String neteaseCommentUrlHead = "http://comment.news.163.com/cache/newlist/";


    public String getSiteByType(NewsType type)
    {
        return urls.get(type);
    }

    public String getWebSetName()
    {
        return webSetName;
    }

    public String getPageMapName()
    {
        return pageMapName;
    }

    public String getWebReg()
    {
        return webReg;
    }

    public static Set<String> getHotWebSet()
    {
        return hotWebSet;
    }
}
