package cp.server.app;

import java.util.HashSet;
import java.util.Set;

import cp.server.common.NewsType;
import cp.server.common.Property;
import cp.server.util.FileUtils;
import cp.server.util.RegParser;
import cp.server.util.WebUtils;

public class WebLoader
{
    String webReg;
    String webSetName;
    Property webProperty;
    Set<String> webSet = new HashSet<String>();
    
    protected WebLoader()
    {

    }

    protected void setWebProperty(Property webProperty)
    {
        this.webProperty = webProperty;
    }

    public void loadWebs(boolean isFullAccess) throws LoaderException
    {
        webReg = webProperty.getWebReg();
        webSetName = webProperty.getWebSetName();

        if (isFullAccess)
        {
            webSet = new HashSet<String>();
        }
        else
        {
            try
            {
                webSet = (Set<String>) FileUtils.readObjectFromFile(webSetName);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw new LoaderException("can't read from webSet");
            }

        }

        if (!isFullAccess)
        {
            String webUrl = null;
            NewsType type;

            HashSet<String> tmpWebSet = new HashSet<String>(webSet);

            for (String key : tmpWebSet)
            {
                webUrl = key.substring(1);
                type = NewsType.valueOf(Integer.valueOf(key.substring(0, 1)));
                lookForWebs(webProperty.getSiteByType(type), webUrl, type);
                persistenceProcess();
            }
        }
        else
        {
            // TODO: can be multithread process
            // look for all pages
            for (NewsType type : NewsType.values())
            {
                if(type == NewsType.FINANCE)
                {
                    continue;
                }
                
                webSet.add(type.getId() + webProperty.getSiteByType(type));

                lookForWebs(webProperty.getSiteByType(type),
                        webProperty.getSiteByType(type), type);

                // persist the webset
                persistenceProcess();
            }

        }
    }

    // look for the news web address, not pages
    private void lookForWebs(String root, String url, NewsType type)
    {
        String webContent = null;

        System.out.println("going to fetch:" + url);

        try
        {
            webContent = WebUtils.fetchPage(url, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (webContent == null)
        {
            // if the error happens when fetching web
            // remove it in webset
            String key = type.getId() + url;
            webSet.remove(key);
            return;
        }

        String key;
        String searchUrl;
        Set<String> searchWebSet = RegParser
                .parseReg(webContent, root + webReg);

        for (String webUrl : searchWebSet)
        {
            searchUrl = webUrl.substring(0, webUrl.length() - 1);
            key = type.getId() + searchUrl;

            if (webSet.contains(key))
            {
                continue;
            }
            else
            {
                webSet.add(key);
            }

            lookForWebs(root, searchUrl, type);
        }

        System.out.println("finish fetch:" + url);

    }

    private void persistenceProcess()
    {
        try
        {
            FileUtils.writeOjectToFile(webSetName, webSet);

            System.out.println("web set size is " + webSet.size());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
