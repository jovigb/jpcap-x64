package jpcap.api;

import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.concurrent.*;

public class Capture implements AutoCloseable {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private final JpcapCaptor captor;
    private final BlockingQueue<Packet> queue = new LinkedBlockingDeque<>(10);
    private final CopyOnWriteArrayList<PacketHandler> handlers = new CopyOnWriteArrayList<>();

    private final Runnable captureWorker = new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                Packet packet = queue.poll();
                for (PacketHandler handler : handlers) {
                    handler.handle(packet);
                }
            }
        }
    };

    public Capture(JpcapCaptor captor) {
        this.captor = captor;
    }

    public void start() {
        EXECUTOR.execute(captureWorker);
        captor.loopPacket(-1, new PacketReceiver() {
            @Override
            public void receivePacket(Packet p) {
                queue.offer(p);
            }
        });
    }

    @Override
    public void close() throws Exception {
        captor.close();
    }

    public void addHandler(PacketHandler handler) {
        handlers.add(handler);
    }

    public void removeHandler(PacketHandler handler) {
        handlers.remove(handler);
    }

    public static Capture fromFile(String filename) {
        JpcapCaptor captor = null;
        try {
            captor = JpcapCaptor.openFile(filename);
        } catch (IOException e) {
            throw new CaptureException(e);
        }
        return new Capture(captor);
    }

    public static Capture fromInterface(NetworkInterface iface) {
        JpcapCaptor captor = null;
        try {
            captor = JpcapCaptor.openDevice(null, Integer.MAX_VALUE, true, 1000);
        } catch (IOException e) {
            throw new CaptureException(e);
        }
        return new Capture(captor);
    }

    public static void main(String... args) throws Exception {

        try (Capture c = Capture.fromFile("")) {
            c.addHandler(new PacketHandler() {
                @Override
                public void handle(Packet packet) {
                    System.out.println(packet);
                }
            });
            c.start();
        }

    }

}
