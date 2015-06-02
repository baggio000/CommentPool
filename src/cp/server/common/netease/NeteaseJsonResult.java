package cp.server.common.netease;

import java.io.Serializable;
import java.util.Map;

public class NeteaseJsonResult implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int d;
    
    private Map<String, NeteaseComment> cmtMap;

    public Map<String, NeteaseComment> getCmtMap()
    {
        return cmtMap;
    }

    public void setCmtMap(Map<String, NeteaseComment> cmtMap)
    {
        this.cmtMap = cmtMap;
    }

    public int getD()
    {
        return d;
    }

    public void setD(int d)
    {
        this.d = d;
    }
    
    

}
