package cp.server.common.sina;

import java.io.Serializable;

public class SinaJson implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private SinaJsonResult result;

    public SinaJsonResult getResult()
    {
        return result;
    }

    public void setResult(SinaJsonResult result)
    {
        this.result = result;
    }

}
