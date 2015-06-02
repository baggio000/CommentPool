package cp.server.common.netease;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NeteaseJson implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<Map<String, Object>> newPosts;

    private int tcount;
    private String reqtime;

    public int getTcount()
    {
        return tcount;
    }

    public void setTcount(int tcount)
    {
        this.tcount = tcount;
    }

    public String getReqtime()
    {
        return reqtime;
    }

    public void setReqtime(String reqtime)
    {
        this.reqtime = reqtime;
    }

    public List<Map<String, Object>> getNewPosts()
    {
        return newPosts;
    }

    public void setNewPosts(List<Map<String, Object>> newPosts)
    {
        this.newPosts = newPosts;
    }

}
