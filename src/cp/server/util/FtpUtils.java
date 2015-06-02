package cp.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FtpUtils
{
    private FTPClient ftpClient = new FTPClient();

    public FTPClient getFtpClient()
    {
        return ftpClient;
    }

    public static final int BINARY_FILE_TYPE = FTP.BINARY_FILE_TYPE;
    public static final int ASCII_FILE_TYPE = FTP.ASCII_FILE_TYPE;

    public void connectServerDefault() throws SocketException, IOException
    {
        connectServer("127.0.0.1", 21, "ftpuser", "Admin123", "");
    }

    public void connectServer(String server, int port, String user,
            String password, String path) throws SocketException, IOException
    {
        ftpClient.connect(server, port);
        System.out.println("Connected to " + server + "." + ftpClient.getReplyCode());
        ftpClient.login(user, password);
        if (path.length() != 0)
        {
            ftpClient.changeWorkingDirectory(path);
        }
        
        ftpClient.mode(BINARY_FILE_TYPE);
        ftpClient.setFileType(BINARY_FILE_TYPE);
    }

    public void setFileType(int fileType) throws IOException
    {

        ftpClient.setFileType(fileType);
    }

    public void closeServer() throws IOException
    {
        if (ftpClient.isConnected())
        {
            ftpClient.disconnect();
        }
    }

    public boolean changeDirectory(String path) throws IOException
    {
        return ftpClient.changeWorkingDirectory(path);
    }

    public boolean createDirectory(String pathName) throws IOException
    {
        return ftpClient.makeDirectory(pathName);
    }

    public boolean removeDirectory(String path) throws IOException
    {
        return ftpClient.removeDirectory(path);
    }

    public boolean removeDirectory(String path, boolean isAll)
            throws IOException
    {

        if (!isAll)
        {
            return removeDirectory(path);
        }

        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr == null || ftpFileArr.length == 0)
        {
            return removeDirectory(path);
        }
        //
        for (FTPFile ftpFile : ftpFileArr)
        {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory() && (!ftpFile.getName().startsWith(".")))
            {
                removeDirectory(path + "/" + name, true);
            }
            else if (ftpFile.isFile())
            {
                deleteFile(path + "/" + name);
            }
            else if (ftpFile.isSymbolicLink())
            {

            }
            else if (ftpFile.isUnknown())
            {

            }
        }
        return ftpClient.removeDirectory(path);
    }

    public boolean isDirectoryExists(String path) throws IOException
    {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr)
        {
            if (ftpFile.isDirectory()
                    && ftpFile.getName().equalsIgnoreCase(path))
            {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public List<FTPFile> getFileList(String path) throws IOException
    {
        FTPFile[] ftpFiles = ftpClient.listFiles(path);

        return Arrays.asList(ftpFiles);
    }

    public boolean deleteFile(String pathName) throws IOException
    {
        return ftpClient.deleteFile(pathName);
    }

    public boolean uploadFile(String fileName, String newName)
            throws IOException
    {
        boolean flag = false;
        InputStream iStream = null;
        try
        {
            iStream = new FileInputStream(fileName);
            flag = ftpClient.storeFile(newName, iStream);
        }
        catch (IOException e)
        {
            flag = false;
            return flag;
        }
        finally
        {
            if (iStream != null)
            {
                iStream.close();
            }
        }
        return flag;
    }

    public boolean uploadFile(String fileName) throws IOException
    {
        return uploadFile(fileName, fileName);
    }

    public boolean uploadFile(InputStream iStream, String newName)
            throws IOException
    {
        boolean flag = false;
        try
        {
            flag = ftpClient.storeFile(newName, iStream);
        }
        catch (IOException e)
        {
            flag = false;
            return flag;
        }
        finally
        {
            if (iStream != null)
            {
                iStream.close();
            }
        }
        return flag;
    }

    public boolean download(String remoteFileName, String localFileName)
            throws IOException
    {
        boolean flag = false;
        File outfile = new File(localFileName);
        OutputStream oStream = null;
        try
        {
            oStream = new FileOutputStream(outfile);
            flag = ftpClient.retrieveFile(remoteFileName, oStream);
        }
        catch (IOException e)
        {
            flag = false;
            return flag;
        }
        finally
        {
            oStream.close();
        }
        return flag;
    }

    public InputStream downFile(String sourceFileName) throws IOException
    {
        return ftpClient.retrieveFileStream(sourceFileName);
    }

    public boolean createDirectories(String directory) throws IOException
    {

        if (directory.startsWith("/"))
        {
            if (!ftpClient.changeWorkingDirectory("/"))
            {
                throw new IOException("change to directory failed");
            }
        }
        String[] s = directory.split("\\/");

        for (String string : s)
        {
            if (string.trim().length() < 1)
            {
                continue;
            }
            System.out.println(string);
            if (ftpClient.makeDirectory(string))
            {
                ftpClient.changeWorkingDirectory(string);
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    public void disConnect() throws IOException
    {
        if (this.ftpClient.isConnected())
        {
            this.ftpClient.disconnect();
        }
    }

    public InputStream getInputStream(String fileName) throws IOException
    {
        return this.ftpClient.retrieveFileStream(fileName);
    }

    public OutputStream getOutputStream(String fileName) throws IOException
    {
        return this.ftpClient.storeFileStream(fileName);
    }

    // public boolean isFileExist(String targetFileName) {
    // boolean flag = false;
    // FTPFile[] ftpFileArr = ftpClient.(targetFileName);
    // for (FTPFile ftpFile : ftpFileArr) {
    // if (ftpFile.isDirectory()
    // && ftpFile.getName().equalsIgnoreCase(path)) {
    // flag = true;
    // break;
    // }
    // }
    // return flag;
    // }

    public static void main(String args[]) throws SocketException, IOException
    {
        FtpUtils ftp = new FtpUtils();
        ftp.connectServer("127.0.0.1", 21, "ftpuser", "Admin123", "");
        ftp.uploadFile("hotcmt/realTimeHotComment", "realTimeHotComment");
        ftp.disConnect();
    }

}