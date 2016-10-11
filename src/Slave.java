/**
 * Main process for Slave
 *
 * @author Henrik Akesson & Fabien Salathe
 */

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger LOGGER = Logger.getLogger(Slave.class.getName());

    private static String masterHost;

    public static void main(String[] args) throws Exception {
        masterHost = MASTER_HOST;
        if (args.length > 0) {
            masterHost = args[1];
        }

        // We initialize the delay to 0
        delta = 0;
        socket = new MulticastSocket(MULTICAST_PORT);

        group = InetAddress.getByName(masterHost);

        packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(masterHost), MASTER_PORT);

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
            LOGGER.log(Level.INFO, "Adjusted delta = {0}", delta);
            LOGGER.log(Level.INFO, "Time is : {0}", System.nanoTime() + delta);
        }
//        Will never be reached because of the while(true) loop but should not be forgotten
//        socket.leaveGroup(group);
//        socket.close();
    }

    /**
     * Waits for DatagramPacket from Master and extracts its content
     * @throws IOException
     */
    private static void listenForMasterTime() throws IOException {
        socket.receive(packet);
        masterTime = ByteUtils.bytesToLong(packet.getData());
        LOGGER.log(Level.INFO, "Received master time = {0}", masterTime);
    }

    /**
     * Calculates delay and sends it to Master
     * @throws IOException
     */
    private static void sendTimeToMaster() throws IOException {

        delta = localTime - masterTime;
        LOGGER.log(Level.INFO, "Local time = {0}", localTime);
        LOGGER.log(Level.INFO, "Delta = {0}", delta);
        bytes = ByteUtils.longToBytes(delta);

        packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(masterHost), MASTER_PORT);
        socket.send(packet);

    }
}