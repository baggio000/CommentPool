package cp.server.common.sina;

import java.io.Serializable;
import java.util.List;

public class SinaJsonResult implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private List<SinaComment> cmntlist;
    private List<SinaComment> hot_list;

    public List<SinaComment> getHot_list()
    {
        return hot_list;
    }

    public void setHot_list(List<SinaComment> hot_list)
    {
        this.hot_list = hot_list;
    }

    public List<SinaComment> getCmntlist()
    {
        return cmntlist;
    }

    public void setCmntlist(List<SinaComment> cmntlist)
    {
        this.cmntlist = cmntlist;
    }
}
