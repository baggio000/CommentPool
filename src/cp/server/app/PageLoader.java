package cp.server.app;

public interface PageLoader
{
    public void loadPages(boolean isFullAccess) throws LoaderException;
}
