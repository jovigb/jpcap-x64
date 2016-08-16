import java.net.*;
import java.io.*;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.*;

class Sniffer {

	/* variables */
	JpcapCaptor captor;
	NetworkInterface[] list;
	String str, info;
	int x, choice;

	public static void main(String args[]) {
		new Sniffer();
	}

	public Sniffer() {

		/* first fetch available interfaces to listen on */
		list = JpcapCaptor.getDeviceList();
		System.out.println("Available interfaces: ");

		for (x = 0; x < list.length; x++) {
			System.out.println(x + " -> " + list[x].name + "(" + list[x].description +")"
					+" datalink: "+list[x].datalink_name + "(" + list[x].datalink_description+")");
			//print out its MAC address
			  System.out.print(" MAC address:");
			  for (byte b : list[x].mac_address)
			    System.out.print(Integer.toHexString(b&0xff) + ":");
			  System.out.println();

			//print out its IP address, subnet mask and broadcast address
			for (NetworkInterfaceAddress a : list[x].addresses)
			    System.out.println(" address:"+a.address + " " + a.subnet + " "+ a.broadcast);
		}
		System.out.println("-------------------------\n");
		choice = Integer.parseInt(getInput("Choose interface (0,1..): "));
		System.out.println("Listening on interface -> "  + list[choice].name + "(" + list[choice].description +")");
		System.out.println("-------------------------\n");

		/* Setup device listener */
		try {
			captor = JpcapCaptor.openDevice(list[choice], 65535, false, 20);
//			captor.processPacket(10,new PacketPrinter());
			/* listen for TCP/IP only */
//			captor.setFilter("tcp port 80 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0)", true);
			captor.setFilter("ip and tcp", true);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		/* start listening for packets */
		while (true) {

			Packet info = captor.getPacket();
			if (info != null)
				System.out.println(info);
//				System.out.println(((TCPPacket)info).toString());
//				System.out.print(getPacketText(info));
		}
	}

	/* get user input */
	public static String getInput(String q) {
		String input = "";
		System.out.print(q);
		BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
		try {
			input = bufferedreader.readLine();
		} catch (IOException ioexception) {
		}
		return input;
	}

	/* return packet data in true text */
	String getPacketText(Packet pack) {
		int i = 0, j = 0;
		byte[] bytes = new byte[pack.header.length + pack.data.length];

//		System.out.println(pack.header.length +", "+ pack.data.length);
		System.arraycopy(pack.header, 0, bytes, 0, pack.header.length);
		System.arraycopy(pack.data, 0, bytes, pack.header.length, pack.data.length);
		StringBuffer buffer = new StringBuffer();

		for (i = 0; i < bytes.length;) {
			for (j = 0; j < 8 && i < bytes.length; j++, i++) {
				String d = Integer.toHexString((int) (bytes[i] & 0xff));
				buffer.append((d.length() == 1 ? "0" + d : d) + " ");

				if (bytes[i] < 32 || bytes[i] > 126)
					bytes[i] = 46;
			}
		}
		return new String(bytes, i - j, j);
	}
	
	class PacketPrinter implements PacketReceiver {
		  //this method is called every time Jpcap captures a packet
		  public void receivePacket(Packet packet) {
		    //just print out a captured packet
		    System.out.println(packet);
		  }
		}
} /* end class */
