import java.net.InetAddress;

import jpcap.*;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.UDPPacket;

class SendUDP
{
	public static void main(String[] args) throws java.io.IOException{
		NetworkInterface[] devices = JpcapCaptor.getDeviceList();
		if(args.length<1){
			System.out.println("Usage: java SentUDP <device index (e.g., 0, 1..)>");
			for(int i=0;i<devices.length;i++)
				System.out.println(i+":"+devices[i].name+"("+devices[i].description+")");
			System.exit(0);
		}
		int index=Integer.parseInt(args[0]);
		JpcapSender sender=JpcapSender.openDevice(devices[index]);

		UDPPacket p=new UDPPacket(12345,54321);
		p.setIPv4Parameter(0,false,false,false,0,false,false,false,0,1010101,100,IPPacket.IPPROTO_UDP,
			InetAddress.getByName("www.yahoo.com"),InetAddress.getByName("www.google.com"));
		p.data="data".getBytes();

		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_IP;
		ether.src_mac=new byte[]{(byte)0,(byte)1,(byte)2,(byte)3,(byte)4,(byte)5};
		ether.dst_mac=new byte[]{(byte)0,(byte)6,(byte)7,(byte)8,(byte)9,(byte)10};
		p.datalink=ether;

		for(int i=0;i<10;i++)
			sender.sendPacket(p);
	}
}