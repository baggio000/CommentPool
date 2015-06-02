package cp.server.common.sina;

import java.io.Serializable;

public class SinaComment implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String result;
    private String uid;
    private String status;
    private int vote;
    private String parent;
    private String usertype;
    private String ip;
    private String content;
    private String time;
    private int rank;
    private int level;
    private String area;
    private String nick;
    private int against;
    private String thread;
    private int length;
    private String config;
    private String mid;
    private String newsid;
    private String channel;
    private int agree;

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getVote()
    {
        return vote;
    }

    public void setVote(int vote)
    {
        this.vote = vote;
    }

    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }

    public String getUsertype()
    {
        return usertype;
    }

    public void setUsertype(String usertype)
    {
        this.usertype = usertype;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
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

    public int getAgainst()
    {
        return against;
    }

    public void setAgainst(int against)
    {
        this.against = against;
    }

    public String getThread()
    {
        return thread;
    }

    public void setThread(String thread)
    {
        this.thread = thread;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public String getConfig()
    {
        return config;
    }

    public void setConfig(String config)
    {
        this.config = config;
    }

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }

    public String getNewsid()
    {
        return newsid;
    }

    public void setNewsid(String newsid)
    {
        this.newsid = newsid;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public int getAgree()
    {
        return agree;
    }

    public void setAgree(int agree)
    {
        this.agree = agree;
    }

}
