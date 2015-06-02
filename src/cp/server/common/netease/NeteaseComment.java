package cp.server.common.netease;

import java.io.Serializable;
import java.util.Map;

public class NeteaseComment implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public NeteaseComment(Map<String, String> map)
    {
        setF(map.get("f"));
        setD(map.get("d"));
        setB(map.get("b"));
        if (map.get("a") != null)
        {
            setA(Integer.valueOf(map.get("a")));
        }
        setN(map.get("n"));
        if (map.get("l") != null)
        {
            setL(Integer.valueOf(map.get("l")));
        }
        setTsn(map.get("tsn"));
        setIp(map.get("ip"));
        if (map.get("v") != null)
        {
            setV(Integer.valueOf(map.get("v")));
        }
        setU(map.get("u"));
        setT(map.get("t"));
        setP(map.get("p"));
        setBi(map.get("bi"));
        setTid(map.get("tid"));
        setPi(map.get("pi"));
    }

    // area
    private String f;

    // pageid
    private String d;

    // content of comment
    private String b;

    // dont know
    private int a;

    // user name
    private String n;

    // dont know
    private int l;

    // dont know
    private String tsn;

    // ip
    private String ip;

    // vote num
    private int v;

    // dont know
    private String u;

    // comment time
    private String t;

    // comment id
    private String p;

    // page category
    private String bi;

    // dont know
    private String tid;

    // comment id = page id + comment id
    private String pi;
    
    private String paraent;

    public String getB()
    {
        return b;
    }

    public void setB(String b)
    {
        this.b = b;
    }

    public String getF()
    {
        return f;
    }

    public void setF(String f)
    {
        this.f = f;
    }

    public String getD()
    {
        return d;
    }

    public void setD(String d)
    {
        this.d = d;
    }

    public int getA()
    {
        return a;
    }

    public void setA(int a)
    {
        this.a = a;
    }

    public String getN()
    {
        return n;
    }

    public void setN(String n)
    {
        this.n = n;
    }

    public int getL()
    {
        return l;
    }

    public void setL(int l)
    {
        this.l = l;
    }

    public String getTsn()
    {
        return tsn;
    }

    public void setTsn(String tsn)
    {
        this.tsn = tsn;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public int getV()
    {
        return v;
    }

    public void setV(int v)
    {
        this.v = v;
    }

    public String getU()
    {
        return u;
    }

    public void setU(String u)
    {
        this.u = u;
    }

    public String getT()
    {
        return t;
    }

    public void setT(String t)
    {
        this.t = t;
    }

    public String getP()
    {
        return p;
    }

    public void setP(String p)
    {
        this.p = p;
    }

    public String getBi()
    {
        return bi;
    }

    public void setBi(String bi)
    {
        this.bi = bi;
    }

    public String getTid()
    {
        return tid;
    }

    public void setTid(String tid)
    {
        this.tid = tid;
    }

    public String getPi()
    {
        return pi;
    }

    public void setPi(String pi)
    {
        this.pi = pi;
    }

    public String getParaent()
    {
        return paraent;
    }

    public void setParaent(String paraent)
    {
        this.paraent = paraent;
    }

}
