package cp.server.app;

import cp.server.common.sina.SinaProperty;

public class SinaWebLoader extends WebLoader
{
    public void loadWebs(boolean isFullAccess) throws LoaderException
    {
        super.setWebProperty(new SinaProperty());
        super.loadWebs(isFullAccess);
    }


    public static void main(String args[]) throws LoaderException
    {
        WebLoader p = new SinaWebLoader();
        p.loadWebs(true);
    }

}
