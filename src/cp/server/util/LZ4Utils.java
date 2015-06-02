package cp.server.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4SafeDecompressor;

public class LZ4Utils
{
    private static void example() throws UnsupportedEncodingException
    {
        LZ4Factory factory = LZ4Factory.fastestInstance();

        String test = null;
        
        try
        {
            test = FileUtils.readStringFromFile("json_example.txt","UTF-8");
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        byte[] data = test.getBytes("UTF-8");
        final int decompressedLength = data.length;

        // compress data
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor
                .maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        int compressedLength = compressor.compress(data, 0, decompressedLength,
                compressed, 0, maxCompressedLength);
        
        String compressStr = new String(compressed,0,compressedLength, "UTF-8");
        byte[] transferedCompressed = new byte[compressedLength];
        transferedCompressed = compressStr.getBytes("UTF-8");

        // decompress data
        // - method 1: when the decompressed length is known
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        byte[] restored = new byte[decompressedLength];
        int compressedLength2 = decompressor.decompress(transferedCompressed, 0,
                restored, 0, decompressedLength);
        // compressedLength == compressedLength2
        
        System.out.println(new String(restored, "UTF-8"));

        // - method 2: when the compressed length is known (a little slower)
        // the destination buffer needs to be over-sized
        LZ4SafeDecompressor decompressor2 = factory.safeDecompressor();
        int decompressedLength2 = decompressor2.decompress(transferedCompressed, 0,
                compressedLength, restored, 0);
        // decompressedLength == decompressedLength2
    }
    
    public static void main(String args[]) throws UnsupportedEncodingException
    {
        example();
    }


}
