import jpcap.*;
import jpcap.packet.Packet;

class Tcpdump implements PacketReceiver {
	public void receivePacket(Packet packet) {
		System.out.println(packet);
	}

	public static void main(String[] args) throws Exception {
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		if(args.length<1){
			System.out.println("usage: java Tcpdump <select a number from the following>");
			
			for (int i = 0; i < devices.length; i++) {
				System.out.println(i+" :"+devices[i].name + "(" + devices[i].description+")");
				System.out.println("    data link:"+devices[i].datalink_name + "("
						+ devices[i].datalink_description+")");
				System.out.print("    MAC address:");
				for (byte b : devices[i].mac_address)
					System.out.print(Integer.toHexString(b&0xff) + ":");
				System.out.println();
				for (NetworkInterfaceAddress a : devices[i].addresses)
					System.out.println("    address:"+a.address + " " + a.subnet + " "
							+ a.broadcast);
			}
		}else{
			JpcapCaptor jpcap = JpcapCaptor.openDevice(devices[Integer.parseInt(args[0])], 2000, false, 20);
	
			jpcap.loopPacket(-1, new Tcpdump());
		}
	}
}
