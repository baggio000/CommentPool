package cp.server.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import cp.server.common.SystemProperty;
import cp.server.util.FileUtils;
import cp.server.util.FtpUtils;
import cp.server.util.SFtpUtils;
import cp.server.util.ZipCompress;

public class FileUploader
{
    private static Set<String> fileNameSet1 = new HashSet<String>();
    private static Set<String> fileNameSet2 = null;

    private static Set<String> fileNameSet = fileNameSet1;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss");

    private static final FtpUtils ftp = new FtpUtils();

    public static void putFileName(String fileName)
    {
        synchronized (fileNameSet)
        {
            fileNameSet.add(fileName);
        }
    }

    public void go()
    {
        Set<String> tmpfileNameSet;
        ZipCompress zipCompress = ZipCompress.getInstance();
        String destFilePath = "updateload_DATE.zip";
        String dateKey = "DATE";

        if (fileNameSet == fileNameSet1)
        {
            tmpfileNameSet = fileNameSet1;
            fileNameSet2 = new HashSet<String>();
            fileNameSet = fileNameSet2;
        }
        else
        {
            tmpfileNameSet = fileNameSet2;
            fileNameSet1 = new HashSet<String>();
            fileNameSet = fileNameSet1;
        }
        
        System.out.println("upload start with " + tmpfileNameSet.size()
                + " files");

        if (tmpfileNameSet.size() == 0)
        {
            return;
        }

        try
        {
            destFilePath = destFilePath.replaceFirst(dateKey, dateFormat
                    .format(new Timestamp(System.currentTimeMillis())));

            System.out.println(destFilePath);

            zipCompress.compress(tmpfileNameSet, destFilePath);

            //upload2Local(destFilePath);

            // upload2Huawei(destFilePath);

            // upload2OpenShift(destFilePath);

            upload2RayyangHK(destFilePath);

//            upload2CONOHA(destFilePath);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                System.out.println(destFilePath + " deleted");
                FileUtils.deleteFile(destFilePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void upload2Local(String destFilePath)
    {
        try
        {
            ftp.connectServerDefault();

            ftp.uploadFile(destFilePath);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (ftp != null)
            {
                try
                {
                    ftp.disConnect();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void upload2Huawei(String destFilePath)
    {
        try
        {
            SFtpUtils.upload(SystemProperty.HUAWEIHOSTNAME,
                    SFtpUtils.DEFAULT_SFTP_PORT, SystemProperty.HUAWEIUSERNAME,
                    SystemProperty.HUAWEIPASSWORD, destFilePath,
                    SystemProperty.HUAWEIREMOTEFILEPATH + destFilePath, null,
                    null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void upload2OpenShift(String destFilePath)
    {
        try
        {
            SFtpUtils.upload(SystemProperty.OPENSHIFTHOSTNAME,
                    SFtpUtils.DEFAULT_SFTP_PORT,
                    SystemProperty.OPENSHIFTUSERNAME, "", destFilePath,
                    SystemProperty.OPENSHIFTREMOTEFILEPATH + destFilePath,
                    SystemProperty.OPENSHIFTPPKPATH,
                    SystemProperty.OPENSHIFTPPKPWD);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void upload2RayyangHK(String destFilePath)
    {
        try
        {
            SFtpUtils.upload(SystemProperty.RAYYANGHKHOSTNAME,
                    SystemProperty.RAYYANGHKPORT,
                    SystemProperty.RAYYANGHKUSERNAME,
                    SystemProperty.RAYYANGHKPASSWORD, destFilePath,
                    SystemProperty.RAYYANGHKREMOTEFILEPATH + destFilePath,
                    null, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void upload2CONOHA(String destFilePath)
    {
        try
        {
            SFtpUtils.upload(SystemProperty.CONOHAHOSTNAME,
                    SystemProperty.CONOHAPORT, SystemProperty.CONOHAUSERNAME,
                    "", destFilePath, SystemProperty.CONOHAREMOTEFILEPATH
                            + destFilePath, SystemProperty.CONOHAPPKPATH,
                    SystemProperty.CONOHAPPKPWD);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) throws FileNotFoundException,
            ClassNotFoundException, IOException
    {
        FileUploader r = new FileUploader();
        r.go();
    }
}
