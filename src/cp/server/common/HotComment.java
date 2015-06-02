package cp.server.common;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class HotComment implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String cmtId;
    private String pageTitle;
    private NewsType newsType;
    private String content;
    private String pageId;
    private String pageUrl;
    private String area;
    private int agree;
    private Timestamp time;
    private Date pageDate;
    private SourceType source;
    private String parentContent;
    private List<Comment> replyCmt;

    public String getCmtId()
    {
        return cmtId;
    }

    public void setCmtId(String cmtId)
    {
        this.cmtId = cmtId;
    }

    public String getPageTitle()
    {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle)
    {
        this.pageTitle = pageTitle;
    }

    public NewsType getNewsType()
    {
        return newsType;
    }

    public void setNewsType(NewsType newsType)
    {
        this.newsType = newsType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPageId()
    {
        return pageId;
    }

    public void setPageId(String pageId)
    {
        this.pageId = pageId;
    }

    public int getAgree()
    {
        return agree;
    }

    public void setAgree(int agree)
    {
        this.agree = agree;
    }

    public Timestamp getTime()
    {
        return time;
    }

    public void setTime(Timestamp time)
    {
        this.time = time;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public SourceType getSource()
    {
        return source;
    }

    public void setSource(SourceType source)
    {
        this.source = source;
    }

    public Date getPageDate()
    {
        return pageDate;
    }

    public void setPageDate(Date pageDate)
    {
        this.pageDate = pageDate;
    }

    public String getParentContent()
    {
        return parentContent;
    }

    public void setParentContent(String parentContent)
    {
        this.parentContent = parentContent;
    }

    public String getPageUrl()
    {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl)
    {
        this.pageUrl = pageUrl;
    }

    public List<Comment> getReplyCmt()
    {
        return replyCmt;
    }

    public void setReplyCmt(List<Comment> replyCmt)
    {
        this.replyCmt = replyCmt;
    }
}
