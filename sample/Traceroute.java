import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

public class Traceroute {
	public static void main(String[] args) throws Exception{
		if(args.length<2){
			System.out.println("Usage: java Traceroute <device index (e.g., 0, 1..)> <target host address>");
			System.exit(0);
		}
		
		//initialize Jpcap
		NetworkInterface device=JpcapCaptor.getDeviceList()[Integer.parseInt(args[0])];
		JpcapCaptor captor=JpcapCaptor.openDevice(device,2000,false,5000);
		InetAddress thisIP=null;
		for(NetworkInterfaceAddress addr:device.addresses)
			if(addr.address instanceof Inet4Address){
				thisIP=addr.address;
				break;
			}
		
		//obtain MAC address of the default gateway
		InetAddress pingAddr=InetAddress.getByName("www.microsoft.com");
		captor.setFilter("tcp and dst host "+pingAddr.getHostAddress(),true);
		byte[] gwmac=null;
		while(true){
			new URL("http://www.microsoft.com").openStream().close();
			Packet ping=captor.getPacket();
			if(ping==null){
				System.out.println("cannot obtain MAC address of default gateway.");
				System.exit(-1);
			}else if(Arrays.equals(((EthernetPacket)ping.datalink).dst_mac,device.mac_address))
					continue;
			gwmac=((EthernetPacket)ping.datalink).dst_mac;
			break;
		}
		
		//create ICMP packet
		ICMPPacket icmp=new ICMPPacket();
		icmp.type=ICMPPacket.ICMP_ECHO;
		icmp.seq=100;
		icmp.id=0;
		icmp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,0,0,IPPacket.IPPROTO_ICMP,
				thisIP,InetAddress.getByName(args[1]));
		icmp.data="data".getBytes();
		
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_IP;
		ether.src_mac=device.mac_address;
		ether.dst_mac=gwmac;
		icmp.datalink=ether;
		
		captor.setFilter("icmp and dst host "+thisIP.getHostAddress(),true);
		JpcapSender sender=captor.getJpcapSenderInstance();
		//JpcapSender sender=JpcapSender.openDevice(device);
		sender.sendPacket(icmp);
		while(true){
			ICMPPacket p=(ICMPPacket) captor.getPacket();
			//System.out.println("received "+p);
			if(p==null){
				System.out.println("Timeout");
			}else if(p.type==ICMPPacket.ICMP_TIMXCEED){
				p.src_ip.getHostName();
				System.out.println(icmp.hop_limit+": "+p.src_ip);
				icmp.hop_limit++;
			}else if(p.type==ICMPPacket.ICMP_UNREACH){
				p.src_ip.getHostName();
				System.out.println(icmp.hop_limit+": "+p.src_ip);
				System.exit(0);
			}else if(p.type==ICMPPacket.ICMP_ECHOREPLY){
				p.src_ip.getHostName();
				System.out.println(icmp.hop_limit+": "+p.src_ip);
				System.exit(0);
			}else continue;
			sender.sendPacket(icmp);
		}
	}
}
