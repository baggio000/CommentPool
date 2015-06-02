package cp.server.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Page implements Serializable
{

    private static final long serialVersionUID = 1L;

    private NewsType newsType;
    private String title;
    private String url;
    private String id;
    private String channel;
    private boolean available = true;
    private Timestamp time;
    private SourceType source;
    private String description;
    private String keywords;
    private String cmtUrlKeys;

    public Page()
    {
        
    }
    
    public Page(SourceType source, NewsType newsType, String url)
    {
        this.source = source;
        this.newsType = newsType;
        this.url = url;
    }

    public NewsType getNewsType()
    {
        return newsType;
    }

    public void setNewsType(NewsType newsType)
    {
        this.newsType = newsType;
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public Timestamp getTime()
    {
        return time;
    }

    public void setTime(Timestamp time)
    {
        this.time = time;
    }

    public SourceType getSource()
    {
        return source;
    }

    public void setSource(SourceType source)
    {
        this.source = source;
    }
    
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public String getCmtUrlKeys()
    {
        return cmtUrlKeys;
    }

    public void setCmtUrlKeys(String cmtUrlKeys)
    {
        this.cmtUrlKeys = cmtUrlKeys;
    }
    
}
