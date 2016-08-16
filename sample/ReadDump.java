import jpcap.*;
import jpcap.packet.Packet;

class ReadDump implements PacketReceiver {
	public void receivePacket(Packet packet) {
		//System.out.println(packet);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Runtime.getRuntime().freeMemory());
		JpcapCaptor jpcap = JpcapCaptor.openFile("test.cap");	
		System.out.println(Runtime.getRuntime().freeMemory());
		jpcap.loopPacket(-1, new ReadDump());
		System.out.println(Runtime.getRuntime().freeMemory());
		System.gc();
		System.out.println(Runtime.getRuntime().freeMemory());
	}
}
