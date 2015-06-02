package cp.server.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;

import cp.server.common.SystemProperty;
import cp.server.dao.ServerDAO;
import cp.server.util.ConnectionFactory;

public class GarbageCleaner
{
    public void go()
    {
        Connection con = null;

        try
        {
            ServerDAO dao = new ServerDAO();

            con = ConnectionFactory.getConnection();

            dao.cleanCommentsInExpiredPage(con, SystemProperty.EXPIREDDAY);

            dao.cleanExpiredPage(con, SystemProperty.EXPIREDDAY);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                if (con != null)
                {
                    con.close();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
    }

    public static void main(String args[]) throws FileNotFoundException,
            ClassNotFoundException, IOException
    {
        GarbageCleaner r = new GarbageCleaner();
        r.go();
    }
}
