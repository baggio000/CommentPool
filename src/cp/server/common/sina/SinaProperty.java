package cp.server.common.sina;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cp.server.common.NewsType;
import cp.server.common.Property;

public class SinaProperty implements Property
{
    private static Map<NewsType, String> urls = new HashMap<NewsType, String>();
    private static Set<String> hotWebSet = new HashSet<String>();

    private static String webReg = "/([0-9,a-z,A-Z]*/{0,1})([0-9,a-z,A-Z]*/{0,1})([0-9,a-z,A-Z]*)/\"";
    private static String webSetName = "SinaWebSet";
    private static String pageMapName = "SinaPageMap";
    
    //comment url properties
    public final static String commentUrlHead = "http://comment5.news.sina.com.cn/page/info?format=js&group=&compress=1&ie=gbk&jsvar=requestId_44705133&page_size=100";
    public final static String channelStr = "&channel=";
    public final static String newsidStr = "&newsid=";
    public final static String pageStr = "&page=";

    // TODO: should be loaded from xml
    static
    {
        urls.put(NewsType.NEWS, "http://news.sina.com.cn");
        urls.put(NewsType.ENT, "http://ent.sina.com.cn");
        urls.put(NewsType.FINANCE, "http://finance.sina.com.cn");
        urls.put(NewsType.SPORTS, "http://sports.sina.com.cn");
        urls.put(NewsType.TECH, "http://tech.sina.com.cn");

        hotWebSet.add(NewsType.NEWS.getId() + "http://news.sina.com.cn");
        hotWebSet.add(NewsType.ENT.getId() + "http://ent.sina.com.cn");
        hotWebSet.add(NewsType.FINANCE.getId() + "http://finance.sina.com.cn");
        hotWebSet.add(NewsType.SPORTS.getId() + "http://sports.sina.com.cn");
        hotWebSet.add(NewsType.TECH.getId() + "http://tech.sina.com.cn");
    }

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
