import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestLog
{
    private static final Log log = LogFactory.getLog(TestLog.class);

    public static void main(String args[])
    {
        String key = "�ö��� ����";
        System.out.println(key.contains("����"));

        
        System.out.println(System.getProperty("user.dir"));
        
//        for (int i = 0; i < 100000000; i++)
//        {
//            log.error("shit happens!");
//            log.debug("shit happens!");
//        }
    }
}
