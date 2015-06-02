package cp.server.app;

import java.sql.Timestamp;

import cp.server.common.JobType;
import cp.server.common.SourceType;
import cp.server.jobs.FileUploader;
import cp.server.jobs.HotCommentByTypeFinder;
import cp.server.jobs.KeywordFinder;
import cp.server.jobs.RealTimeHotCommentFinder;

public class JobExecutor implements Runnable
{
    long interval;
    JobType type;

    public JobExecutor(long interval, JobType type)
    {
        this.interval = interval;
        this.type = type;
    }

    @Override
    public void run()
    {
        switch (type)
        {
        case SINACOMMENTOPER:
            runCommentLoader(SourceType.SINA);
            break;
        case NETEASECOMMENTOPER:
            runCommentLoader(SourceType.NETEASE);
            break;
        case SINAPAGELOADER:
            runSinaPageLoader();
            break;
        case NETEASEPAGELOADER:
            runNeteasePageLoader();
        case HOTCOMMENT:
            runHotCommentFinder();
            break;
        case HOTCOMMENTBYTYPE:
            runHotCommentByTypeFinder();
            break;
        case KEYWORDFINDER:
            runKeyWordFinder();
            break;
        case FILEUPLOADER:
            runFileUploader();
            break;            
        default:
            break;
        }
    }

    private void runSinaPageLoader()
    {
        PageLoader p = new SinaPageLoader();
        long beg = System.currentTimeMillis();
        long end;

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runSinaPageLoader Time is "
                    + new Timestamp(beg));

            try
            {
                p.loadPages(false);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("load sina pages last:" + (end - beg) / 1000.0
                    + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }
        }
    }

    private void runNeteasePageLoader()
    {
        PageLoader p1 = new NeteasePageLoader();
        long beg = System.currentTimeMillis();
        long end;

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runNeteasePageLoader Time is "
                    + new Timestamp(beg));

            try
            {
                p1.loadPages(false);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("load netease pages last:" + (end - beg)
                    / 1000.0 + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }

        }

    }

    private void runCommentLoader(SourceType source)
    {
        long beg = System.currentTimeMillis();
        long end;
        CommentOper co;

        // co = new CommentOperImplSeperatedList();
        co = new CommentOperImpl();

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runCommentLoader" + source + " Time is "
                    + new Timestamp(beg));

            try
            {
                co.setSource(source);
                co.init();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("load comments last:" + (end - beg) / 1000.0
                    + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }

        }
    }

    private void runHotCommentFinder()
    {
        RealTimeHotCommentFinder job = new RealTimeHotCommentFinder();
        long beg = System.currentTimeMillis();
        long end;

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runHotCommentFinder Time is "
                    + new Timestamp(beg));

            try
            {
                job.go();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("load hot comment last:" + (end - beg) / 1000.0
                    + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }

        }

    }

    private void runHotCommentByTypeFinder()
    {
        HotCommentByTypeFinder job = new HotCommentByTypeFinder();
        long beg = System.currentTimeMillis();
        long end;

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runHotCommentByTypeFinder Time is "
                    + new Timestamp(beg));

            try
            {
                job.go();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("load hot comment by type last:" + (end - beg)
                    / 1000.0 + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }

        }

    }

    private void runKeyWordFinder()
    {
        KeywordFinder job = new KeywordFinder();
        long beg = System.currentTimeMillis();
        long end;

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runKeyWordFinder Time is "
                    + new Timestamp(beg));

            try
            {
                job.go();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("load keyword last:" + (end - beg) / 1000.0
                    + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }

        }

    }
    
    private void runFileUploader()
    {
        FileUploader job = new FileUploader();
        long beg = System.currentTimeMillis();
        long end;

        while (true)
        {
            beg = System.currentTimeMillis();
            System.out.println("start runFileUploader Time is "
                    + new Timestamp(beg));

            try
            {
                job.go();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            end = System.currentTimeMillis();

            System.out.println("Time is "
                    + new Timestamp(System.currentTimeMillis()));
            System.out.println("file loader last:" + (end - beg) / 1000.0
                    + "s");

            while (true)
            {
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                end = System.currentTimeMillis();

                if (end - beg > interval)
                {
                    break;
                }
            }

        }

    }    

}
