package cp.server.common;

public enum SourceType
{
    SINA(0), TENCENT(1), NETEASE(2), SOHU(3);

    public static SourceType valueOf(int id)
    {
        switch (id)
        {
        case 0:
            return SINA;
        case 1:
            return TENCENT;
        case 2:
            return NETEASE;
        case 3:
            return SOHU;
        default:
            return SINA;
        }
    }

    private final int id;

    SourceType(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
}
