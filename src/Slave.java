/**
 * Main process for Slave
 * @author Henrik Akesson & Fabien Salathe
 */

import java.io.IOException;
import java.net.*;

public class Slave {

    private static long delta;
    private static MulticastSocket socket;
    private static InetAddress group;
    private static byte[] bytes = new byte[Long.BYTES];
    private static long masterTime;
    private static long localTime;
    private static DatagramPacket packet;
    private static final String MASTER_HOST = "localhost";
    private static final int MASTER_PORT = 4446;
    private static final int MULTICAST_PORT = 4445;
    private static final String MULTICAST_HOST = "228.5.6.7";

    public static void main(String[] args) throws Exception {
        delta = 0;
        socket = new MulticastSocket(MULTICAST_PORT);

        group = InetAddress.getByName(MULTICAST_HOST);

        packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(MASTER_HOST), MASTER_PORT);

        socket.joinGroup(group);

        while (true) {

            // Step 1 : Wait for master time
            listenForMasterTime();

            // Step 2 : Calculate difference between master time and local time
            localTime = System.nanoTime() + delta;

            // Step 3 : send the difference to master
            sendTimeToMaster();

            // Step 4 : Wait for official delta
            listenForMasterTime();
            delta = masterTime - localTime;

            // Step 5 : Change local time
            System.out.println("Adjusted delta = " + delta);
            System.out.println("Time is : " + (System.nanoTime() + delta));
        }
    }

    private static void listenForMasterTime() throws IOException {
        socket.receive(packet);
        masterTime = ByteUtils.bytesToLong(packet.getData());
        System.out.println("Received master time = " + masterTime);
    }

    private static void sendTimeToMaster() throws IOException {

        delta = localTime - masterTime;
        System.out.println("Local Time = " + localTime);
        System.out.println("Delta = " + delta);

        bytes = ByteUtils.longToBytes(delta);

        packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(MASTER_HOST), MASTER_PORT);
        socket.send(packet);

    }
}