package cp.server.common;

import java.io.Serializable;
import java.util.List;

public class Keyword implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String keyword;
    private int count;
    private List<Page> pageList;
    private Page page;

    public Keyword(String keyword, Page page)
    {
        this.keyword = keyword;
        this.page = page;
    }

    public Keyword(String keyword, List<Page> pageList)
    {
        this.keyword = keyword;
        this.pageList = pageList;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public List<Page> getPageList()
    {
        return pageList;
    }

    public void setPageList(List<Page> pageList)
    {
        this.pageList = pageList;
    }
    
    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

    @Override
    public boolean equals(Object cKeyword)
    {
        return cKeyword != null
                && this.keyword.equals(((Keyword) cKeyword).getKeyword());
    }

    @Override
    public int hashCode()
    {
        return this.keyword.hashCode();
    }

    public static void main(String args[])
    {
//        Keyword k1 = new Keyword("a", "1", "");
//        Keyword k2 = new Keyword("a", "2", "");
//        Map<Keyword, Integer> keyMap = new HashMap<Keyword, Integer>(300);
//        keyMap.put(k1, 1);
//        System.out.println(k1.equals(k2));
//
//        System.out.println(keyMap.containsKey("a"));
    }

}
