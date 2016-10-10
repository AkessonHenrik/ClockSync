/**
 * Main process for Master
 * @author Henrik Akesson & Fabien Salathe
 */

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class Master {

    // Number of slaves to listen to
    private static int numberOfSlaves = 3;

    // Byte array that will be used for DatagramPackets
    private static byte[] bytes = new byte[Long.BYTES];

    // Stores the master process time
    private static long masterTime;

    // Stores the largest time difference between slaves
    private static long delta;

    // Group socket, used to communicate to all Slaves
    private static MulticastSocket socket;
    private static InetAddress group;

    // DatagramPacket that is used throughout the process
    private static DatagramPacket packet;

    // long array that stores all Slaves' times
    private static long[] times;

    public static void main(String[] args) throws IOException, InterruptedException {

        group = InetAddress.getByName("228.5.6.7");
        socket = new MulticastSocket(4446);
        packet = new DatagramPacket(bytes, bytes.length);
        times = new long[numberOfSlaves];

        while (true) {

            masterTime = System.nanoTime();

            delta = 0;

            sendTimes();

            listenForTimes();
            System.out.println("Received deltas");

            delta = calcDelta(times);

            System.out.println("Largest delta = " + delta);

            sendTimes();

            // Wait for a while
            TimeUnit.SECONDS.sleep(4);
        }
    }

    /**
     * Listens for and stores all Slave's times
     *
     * @throws IOException
     */
    private static void listenForTimes() throws IOException {
        for (int i = 0; i < numberOfSlaves; i++) {
            socket.receive(packet);
            long time = ByteUtils.bytesToLong(packet.getData());
            times[i] = time;
        }
    }

    /**
     * Returns the largest time in the array.
     *
     * @param times
     * @return the largest time in the array
     */
    private static long calcDelta(long[] times) {
        long deltaTmp = times[0];
        for (long time : times) {
            System.out.println("Delta = " + time);
            if (time > deltaTmp)
                deltaTmp = time;
        }
        return deltaTmp;
    }

    /**
     * Sends Master's system time + delta through Multicast to all Slaves
     *
     * @throws IOException
     */
    private static void sendTimes() throws IOException {
        bytes = ByteUtils.longToBytes(masterTime + delta);
        socket.send(new DatagramPacket(bytes, bytes.length, group, 4445));
    }
}