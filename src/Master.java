import java.net.*;
import java.util.concurrent.TimeUnit;

public class Master {
    public static boolean keepRunning = true;

    public static int numberOfSlaves = 3;

    public static void main(String[] args) throws Exception {
        InetAddress group = InetAddress.getByName("228.5.6.7");

        MulticastSocket socket = new MulticastSocket(4446);
        byte[] buff;
        DatagramPacket packet;


        long[] times = new long[numberOfSlaves];
        while (keepRunning) {
            // Step 1 : Send Master time to slaves
            long masterTime = System.nanoTime();
            buff = ByteUtils.longToBytes(masterTime);
            packet = new DatagramPacket(buff, buff.length, group, 4445);
            socket.send(packet);


            // Recevoir horloge
            for (int i = 0; i < numberOfSlaves; i++) {

                socket.receive(packet);

                System.out.println("Received deltas");

                long time = ByteUtils.bytesToLong(packet.getData());

                times[i] = time;
            }


            //Calcul
            long delta = calcDelta(times);
            System.out.println("Largest delta = " + delta);

            //Renvoyer le temps
            byte[] bytes = ByteUtils.longToBytes(masterTime + delta);
            socket.send(new DatagramPacket(bytes, bytes.length, group, 4445));
            if(!keepRunning) {
                bytes = ByteUtils.longToBytes(-1);
                socket.send(new DatagramPacket(bytes, bytes.length, group, 4445));
            }
            TimeUnit.SECONDS.sleep(4);
        }
        //socket.close();
    }

    private static long calcDelta(long[] times) {
        long delta = times[0];
        for (long time : times) {
            System.out.println("Delta = " + time);
            if (time > delta)
                delta = time;
        }
        return delta;
    }
}