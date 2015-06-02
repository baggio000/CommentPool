package cp.server.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Comment implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String cmtId;
    private String content;
    private String ip;
    private String pageId;
    private String area;
    private String nick;
    private int agree;
    private int against;
    private Timestamp time;
    private int localAgree;
    private int localAgainst;
    private SourceType source;
    private String parent;

    public String getCmtId()
    {
        return cmtId;
    }

    public void setCmtId(String cmtId)
    {
        this.cmtId = cmtId;
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

    public int getAgainst()
    {
        return against;
    }

    public void setAgainst(int against)
    {
        this.against = against;
    }

    public Timestamp getTime()
    {
        return time;
    }

    public void setTime(Timestamp time)
    {
        this.time = time;
    }

    public int getLocalAgree()
    {
        return localAgree;
    }

    public void setLocalAgree(int localAgree)
    {
        this.localAgree = localAgree;
    }

    public int getLocalAgainst()
    {
        return localAgainst;
    }

    public void setLocalAgainst(int localAgainst)
    {
        this.localAgainst = localAgainst;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public SourceType getSource()
    {
        return source;
    }

    public void setSource(SourceType source)
    {
        this.source = source;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }
    
}
