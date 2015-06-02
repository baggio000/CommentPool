package cp.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class ConnectionFactory
{
    private static Connection con = null;
    private static PoolProperties p = null;
    private static DataSource datasource = null;

    static
    {
        p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/cmtfinder?useUnicode=true&characterEncoding=gbk");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername("root");
        p.setPassword("");
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("select 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setMinIdle(10);
        datasource = new DataSource();
        datasource.setPoolProperties(p);
    }

    public synchronized static Connection getConnection()
    {
        Connection con = null;

        try
        {
            con = datasource.getConnection();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return con;
    }

    public static Connection getSingleConnection() throws ClassNotFoundException,
            SQLException
    {
        if (con != null)
        {
            return con;
        }

        Class.forName("com.mysql.jdbc.Driver");

        String url = "jdbc:mysql://localhost:3306/cmtfinder?useUnicode=true&characterEncoding=gbk"; // 指定了字符集
        String username = "root";
        String password = "";

        con = DriverManager.getConnection(url, username, password);

        return con;
    }
    
    public static void main(String args[])
    {
        ConnectionFactory.getConnection();
    }
}
