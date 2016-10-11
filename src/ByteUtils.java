/**
 * Class used to convert doubles to byte arrays and vice versa
 * Source: <url>
 * @author Henrik Akesson & Fabien Salathe
 */

import java.nio.ByteBuffer;

public class ByteUtils {
    /**
     * Converts double to byte array
     * @param x: double to convert
     * @return param converted to byte array
     */
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * Converts byte array to double
     * @param bytes: byte array to convert
     * @return double: byte array converted to double
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, Long.BYTES);
        buffer.flip();
        return buffer.getLong();
    }
}