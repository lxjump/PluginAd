package com.mess.loader.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {
    
    //byte 与 int 的相互转换
    public static byte intToByte(int x) {
        return (byte) x;
    }
    
    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
        
    }
    
    //byte 数组与 int 的相互转换
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }
    
    
    public static int byteArrayToIntBigEndian(byte[] byteArrary) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        // by choosing big endian, high order bytes must be put
        // to the buffer before low order bytes
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
        // since int are 4 bytes (32 bit), you need to put all 4, so put 0
        // for the high order bytes
        //        byteBuffer.put((byte)0x00);
        //        byteBuffer.put((byte)0x00);
        //        byteBuffer.put((byte)0x01);
        //        byteBuffer.put((byte)0x10);
        
        byteBuffer.put(byteArrary[3]);
        byteBuffer.put(byteArrary[2]);
        byteBuffer.put(byteArrary[1]);
        byteBuffer.put(byteArrary[0]);
        byteBuffer.flip();
        return byteBuffer.getInt();
    }
    
    
    public static int byteArrayToIntLittleEndian(byte[] byteArrary) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
        // by choosing little endian, low order bytes must be put
        // to the buffer before high order bytes
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        // since int are 4 bytes (32 bit), you need to put all 4, so put 0
        // for the low order bytes
        
        byteBuffer.put(byteArrary[0]);
        byteBuffer.put(byteArrary[1]);
        byteBuffer.put(byteArrary[2]);
        byteBuffer.put(byteArrary[3]);
        byteBuffer.flip();
        return byteBuffer.getInt();
    }
    
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
    
    private static ByteBuffer buffer = ByteBuffer.allocate(8);
    
    //byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }
    
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
    
}
