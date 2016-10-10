import java.net.*;

public class Slave {
    public static void main(String[] args) throws Exception {
        long delta = 0;
        MulticastSocket socket = new MulticastSocket(4445);

        InetAddress group = InetAddress.getByName("228.5.6.7"); // changer si autre machine

        socket.joinGroup(group);

        while (true) {

            // Step 1 : Wait for master time
            byte[] bytes = new byte[Long.BYTES];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), 4446);
            socket.receive(packet);

            long masterTime = ByteUtils.bytesToLong(packet.getData());
            if (masterTime < 0) {
                socket.leaveGroup(group);
                socket.close();
                return;
            }
            System.out.println("Received master time = " + masterTime);


            // Step 2 : Calculate difference between master time and local time
            long localTime = System.nanoTime() + delta;


            // Step 3 : send the difference to master
            delta = localTime - masterTime;
            System.out.println("Local Time = " + localTime);
            System.out.println("Delta = " + delta);

            bytes = ByteUtils.longToBytes(delta);


            packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("localhost"), 4446);
            socket.send(packet);


            // Step 4 : Wait for official delta
            socket.receive(packet);
            long masterTimePlusDeltaMax = ByteUtils.bytesToLong(packet.getData());
            System.out.println("Official time = " + masterTimePlusDeltaMax);
            delta = masterTimePlusDeltaMax - localTime;

            // Step 5 : Change local time
            System.out.println("Adjusted delta = " + delta);
            System.out.println("Time is : " + (System.nanoTime() + delta));
        }
    }
}