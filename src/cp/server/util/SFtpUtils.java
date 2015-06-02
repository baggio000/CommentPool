package cp.server.util;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SFtpUtils
{
    public static int DEFAULT_SFTP_PORT = 22;

    /*
     * args[0]: hostName args[1]: username args[2]: password args[3]:
     * localFilePath args[4]: remoteFilePath
     */
    public static void main(String[] args)
    {
        // if (args.length < 5)
        // throw new RuntimeException(
        // "Error. Please enter "
        // + "args[0]: hostName, args[1]: username, args[2]: password, "
        // + "args[3]: localFilePath, args[4]: remoteFilePath.");

        // String hostName = "commentpool-baggio000.rhcloud.com";
        // String username = "529ddb964382eca06f0004b0";
        // String password = "";
        // String localFilePath = "json.txt";
        // String remoteFilePath = "/jbossews/webapps/json.txt";
        // String ppkPath = "baggio_openssh1.ppk";
        // String ppkPwd = "Admin123";

        String hostName = "117.78.4.89";
        String username = "root";
        String password = "Admin123";
        String localFilePath = "json.txt";
        String remoteFilePath = "/apache-tomcat-7/webapps/test/json.txt";
        String ppkPath = null;
        String ppkPwd = null;

        upload(hostName, DEFAULT_SFTP_PORT, username, password, localFilePath,
                remoteFilePath, ppkPath, ppkPwd);
        // exist(hostName, username, password, remoteFilePath);
        // download(hostName, username, password, localFilePath,
        // remoteFilePath);
        // delete(hostName, username, password, remoteFilePath);

        // upload(args[0], args[1], args[2], args[3], args[4]);
        // exist(args[0], args[1], args[2], args[4]);
        // download(args[0], args[1], args[2], args[3], args[4]);
        // delete(args[0], args[1], args[2], args[4]);
    }

    public static void upload(String hostName, int port, String username,
            String password, String localFilePath, String remoteFilePath,
            String ppkPath, String ppkPwd)
    {

        File f = new File(localFilePath);
        if (!f.exists())
            throw new RuntimeException("Error. Local file not found");

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try
        {
            manager.init();

            // Create local file object
            FileObject localFile = manager.resolveFile(f.getAbsolutePath());

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(
                    createConnectionString(hostName, port, username, password,
                            remoteFilePath),
                    createDefaultOptions(ppkPath, ppkPwd));

            // FileObject root = manager
            // .resolveFile(
            // "sftp://529ddb964382eca06f0004b0@commentpool-baggio000.rhcloud.com/jbossews/webapps/test",
            // createDefaultOptions());
            //
            // FileObject ftpFile = manager.resolveFile(root, "json.txt");

            // Copy local file to sftp server
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

            if (remoteFile.exists())
            {
                System.out.println("File upload to " + hostName + " filename:"
                        + localFilePath + " successfully.");
            }
            else
            {
                System.out.println("File upload to " + hostName + " filename:"
                        + localFilePath + " failed.");
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            manager.close();
        }
    }

    public static void download(String hostName, int port, String username,
            String password, String localFilePath, String remoteFilePath,
            String ppkPath, String ppkPwd)
    {

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try
        {
            manager.init();

            String downloadFilePath = localFilePath.substring(0,
                    localFilePath.lastIndexOf("."))
                    + "_downlaod_from_sftp"
                    + localFilePath.substring(localFilePath.lastIndexOf("."),
                            localFilePath.length());

            // Create local file object
            FileObject localFile = manager.resolveFile(downloadFilePath);

            // Create remote file object
            FileObject remoteFile = manager.resolveFile(
                    createConnectionString(hostName, port, username, password,
                            remoteFilePath),
                    createDefaultOptions(ppkPath, ppkPwd));

            // Copy local file to sftp server
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

            System.out.println("File download success");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            manager.close();
        }
    }

    public static void delete(String hostName, int port, String username,
            String password, String remoteFilePath, String ppkPath,
            String ppkPwd)
    {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try
        {
            manager.init();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(
                    createConnectionString(hostName, port, username, password,
                            remoteFilePath),
                    createDefaultOptions(ppkPath, ppkPwd));

            if (remoteFile.exists())
            {
                remoteFile.delete();
                System.out.println("Delete remote file success");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            manager.close();
        }
    }

    public static boolean exist(String hostName, int port, String username,
            String password, String remoteFilePath, String ppkPath,
            String ppkPwd)
    {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try
        {
            manager.init();

            // Create remote object
            FileObject remoteFile = manager.resolveFile(
                    createConnectionString(hostName, port, username, password,
                            remoteFilePath),
                    createDefaultOptions(ppkPath, ppkPwd));

            System.out.println("File exist: " + remoteFile.exists());

            return remoteFile.exists();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            manager.close();
        }
    }

    public static String createConnectionString(String hostName, int port,
            String username, String password, String remoteFilePath)
    {
        // result: "sftp://user:123456@domainname.com:port/resume.pdf
        if (password == null)
        {
            return "sftp://" + username + "@" + hostName + ":" + port + "/"
                    + remoteFilePath;
        }

        return "sftp://" + username + ":" + password + "@" + hostName + ":"
                + port + "/" + remoteFilePath;
    }

    public static FileSystemOptions createDefaultOptions(String ppkPath,
            String ppkPwd) throws FileSystemException
    {
        // Create SFTP options
        FileSystemOptions opts = new FileSystemOptions();

        // // SSH Key checking
        // SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(
        // opts, "no");

        // Root directory set to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);

        // Timeout is count by Milliseconds
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

        if (ppkPath != null)
        {
            File sshKey = new File(ppkPath);
            SftpFileSystemConfigBuilder.getInstance().setIdentities(opts,
                    new File[]
                    { sshKey });

            PublicKeyAuthUserInfo userInfo = new PublicKeyAuthUserInfo(ppkPwd,
                    "");

            SftpFileSystemConfigBuilder.getInstance().setUserInfo(opts,
                    userInfo);
        }

        return opts;
    }

}