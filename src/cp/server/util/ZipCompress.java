package cp.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import cp.server.common.HotComment;

/**
 * 多文件压缩
 * 
 * @author Administrator
 * 
 */
public class ZipCompress
{
    private static final ZipCompress zipCompress = new ZipCompress();
    private static final int BUFFER = 1024;

    private ZipCompress()
    {
    }

    public static ZipCompress getInstance()
    {
        return zipCompress;
    }

    public synchronized void compress(Set<String> filePaths, String destFilePath)
    {
        try
        {
            // 输出校验流,采用Adler32更快
            CheckedOutputStream csum = new CheckedOutputStream(
                    new FileOutputStream(destFilePath), new Adler32());
            // 创建压缩输出流
            ZipOutputStream zos = new ZipOutputStream(csum);
            BufferedOutputStream out = new BufferedOutputStream(zos);
            // 设置Zip文件注释
            zos.setComment("A test of java Zipping");
            for (String s : filePaths)
            {
                System.out.println("Writing file " + s);
                // 针对单个文件建立读取流
                BufferedInputStream bin = new BufferedInputStream(
                        new FileInputStream(s));
                // ZipEntry ZIP 文件条目
                // putNextEntry 写入新条目，并定位到新条目开始处
                zos.putNextEntry(new ZipEntry(s));
                int c;
                while ((c = bin.read()) != -1)
                {
                    out.write(c);
                }
                bin.close();
                out.flush();
            }
            
            out.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public synchronized void decompress(String filePath, String zipFile)
    {
        ZipFile zf = null;

        try
        {
            zf = new ZipFile(zipFile);

            Enumeration e = zf.entries();
            while (e.hasMoreElements())
            {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                System.out.println("File name : " + ze2.getName());

                decompressFile(new File(filePath + ze2.getName()),
                        zf.getInputStream(ze2));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (zf != null)
            {
                try
                {
                    zf.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static void decompressFile(File destFile, InputStream fis)
            throws Exception
    {
        byte data[] = new byte[BUFFER];
        BufferedOutputStream bos = null;
        int count;

        try
        {
            bos = new BufferedOutputStream(new FileOutputStream(destFile));

            while ((count = fis.read(data, 0, BUFFER)) != -1)
            {
                bos.write(data, 0, count);
            }
        }
        finally
        {
            if (bos != null)
            {
                bos.close();
            }
        }
    }

    public static void main(String args[])
    {
        String[] filepaths =
        { "hotcmt\\hotComment", "hotcmt\\realTimeHotComment" };
        try
        {
            try
            {
                // 输出校验流,采用Adler32更快
                CheckedOutputStream csum = new CheckedOutputStream(
                        new FileOutputStream("D:\\test.zip"), new Adler32());
                // 创建压缩输出流
                ZipOutputStream zos = new ZipOutputStream(csum);
                BufferedOutputStream out = new BufferedOutputStream(zos);
                // 设置Zip文件注释
                zos.setComment("A test of java Zipping");
                for (String s : filepaths)
                {
                    System.out.println("Writing file " + s);
                    // 针对单个文件建立读取流
                    BufferedInputStream bin = new BufferedInputStream(
                            new FileInputStream(s));
                    // ZipEntry ZIP 文件条目
                    // putNextEntry 写入新条目，并定位到新条目开始处
                    zos.putNextEntry(new ZipEntry(s));
                    int c;
                    while ((c = bin.read()) != -1)
                    {
                        out.write(c);
                    }
                    bin.close();
                    out.flush();
                }
                
                out.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            ZipFile zf = new ZipFile("D:\\test.zip");

            Enumeration e = zf.entries();
            while (e.hasMoreElements())
            {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                System.out.println("File name : " + ze2.getName());

                decompressFile(new File("D:/test/" + ze2.getName()),
                        zf.getInputStream(ze2));
            }

            List<HotComment> hotCmts = (List<HotComment>) FileUtils
                    .readObjectFromFile("d:/test/hotcmt/hotComment");
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}