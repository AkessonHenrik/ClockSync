/**
 * Main process for Slave
 * @author Henrik Akesson & Fabien Salathe
 */

import java.io.*;
import java.net.*;
import java.util.Random;

public class Slave {


    // Delay to add when sending time to Master
    private static long delta;

    // Multicast group
    private static MulticastSocket socket;
    private static InetAddress group;

    // DatagramPacket used for all communications
    private static DatagramPacket packet;

    // Byte array that will be used to store DatagramPacket contents
    private static byte[] bytes = new byte[Long.BYTES];

    // Stores time sent by master
    private static long masterTime;


    // Stores local time
    private static long localTime;

    // Master host, change if different devices
    private static final String MASTER_HOST = "localhost";

    // Master port
    private static final int MASTER_PORT = 4446;

    // Multicast port
    private static final int MULTICAST_PORT = 4445;

    // Multicast address
    private static final String MULTICAST_HOST = "228.5.6.7";

    public static void main(String[] args) throws Exception {

        // We initialize the delay to 0
        delta = 0;
        socket = new MulticastSocket(MULTICAST_PORT);

        group = InetAddress.getByName(MULTICAST_HOST);

        packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(MASTER_HOST), MASTER_PORT);

        socket.joinGroup(group);
        Random r = new Random();
        int n = r.nextInt();
        PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream("Slave" + n +".txt")));

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
            long printTime = System.nanoTime() + delta;
            writer.println(printTime);
            writer.flush();
            System.out.println("Time is : " + (printTime));

        }
    }

    /**
     * Waits for DatagramPacket from Master and extracts its content
     * @throws IOException
     */
    private static void listenForMasterTime() throws IOException {
        socket.receive(packet);
        masterTime = ByteUtils.bytesToLong(packet.getData());
        System.out.println("Received master time = " + masterTime);
    }

    /**
     * Calculates delay and sends it to Master
     * @throws IOException
     */
    private static void sendTimeToMaster() throws IOException {

        delta = localTime - masterTime;
        System.out.println("Local Time = " + localTime);
        System.out.println("Delta = " + delta);

        bytes = ByteUtils.longToBytes(delta);

        packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(MASTER_HOST), MASTER_PORT);
        socket.send(packet);

    }
}