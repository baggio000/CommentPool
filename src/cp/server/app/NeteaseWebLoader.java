package cp.server.app;

import cp.server.common.netease.NeteaseProperty;

public class NeteaseWebLoader extends WebLoader
{
    public void loadWebs(boolean isFullAccess) throws LoaderException
    {
        super.setWebProperty(new NeteaseProperty());
        super.loadWebs(isFullAccess);
    }

    public static void main(String args[]) throws LoaderException
    {
        WebLoader p = new NeteaseWebLoader();
        p.loadWebs(true);
    }

}
