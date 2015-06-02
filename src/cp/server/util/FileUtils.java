package cp.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

public class FileUtils
{
    public static void writeStringToFile(String fileName, String str,
            boolean isAppend) throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, isAppend)));

        try
        {
            bw.write(str);
            bw.flush();
        }
        finally
        {
            bw.close();
        }
    }

    public static void writeStringToFile(String fileName, String str,
            String encoding, boolean isAppend) throws IOException
    {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName, isAppend), encoding));

        try
        {
            bw.write(str);
            bw.flush();
        }
        finally
        {
            bw.close();
        }
    }

    public static void writeOjectToFile(String fileName, Object obj)
            throws FileNotFoundException, IOException
    {

        ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(
                        new File(fileName))));

        try
        {
            oos.writeObject(obj);
            oos.flush();
        }
        finally
        {
            oos.close();
        }
    }

    public static Object readObjectFromFile(String fileName)
            throws FileNotFoundException, IOException, ClassNotFoundException
    {
        Object obj = null;

        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
                new FileInputStream(new File(fileName))));

        try
        {
            obj = ois.readObject();

        }
        finally
        {
            ois.close();
        }

        return obj;
    }

    public static String readStringFromFile(String fileName, String encoding)
            throws IOException
    {
        String line;
        StringBuilder buff = new StringBuilder();
        BufferedReader bis = null;

        try
        {
            bis = new BufferedReader(new InputStreamReader(new FileInputStream(
                    fileName), encoding));

            while ((line = bis.readLine()) != null)
            {
                buff.append(line);
            }

        }
        finally
        {
            if (bis != null)
            {
                bis.close();
            }
        }

        return buff.toString();
    }

    public static String readStringFromFile(String fileName) throws IOException
    {
        String line;
        StringBuilder buff = new StringBuilder();
        BufferedReader bis = null;

        try
        {
            bis = new BufferedReader(new InputStreamReader(new FileInputStream(
                    fileName), "GBK"));

            while ((line = bis.readLine()) != null)
            {
                buff.append(line);
            }

        }
        finally
        {
            if (bis != null)
            {
                bis.close();
            }
        }

        return buff.toString();
    }

    public static void deleteFile(String fileName) throws IOException
    {
        new File(fileName).delete();
    }
}
