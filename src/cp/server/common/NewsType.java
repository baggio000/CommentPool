package cp.server.common;

public enum NewsType
{
    NEWS(0), TECH(1), FINANCE(2), ENT(3), SPORTS(4);

    public static NewsType valueOf(int id)
    {
        switch (id)
        {
        case 0:
            return NEWS;
        case 1:
            return TECH;
        case 2:
            return FINANCE;
        case 3:
            return ENT;
        case 4:
            return SPORTS;
        default:
            return NEWS;
        }
    }

    private final int id;

    NewsType(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static void main(String args[])
    {
        NewsType type = NewsType.valueOf(0);
        NewsType type1 = NewsType.valueOf(0);
        System.out.println(type == type1);
    }
}
