import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

import jpcap.*;
import jpcap.packet.*;

public class ARP {
	public static byte[] arp(InetAddress ip) throws java.io.IOException{
		//find network interface
		NetworkInterface[] devices=JpcapCaptor.getDeviceList();
		NetworkInterface device=null;

loop:	for(NetworkInterface d:devices){
			for(NetworkInterfaceAddress addr:d.addresses){
				if(!(addr.address instanceof Inet4Address)) continue;
				byte[] bip=ip.getAddress();
				byte[] subnet=addr.subnet.getAddress();
				byte[] bif=addr.address.getAddress();
				for(int i=0;i<4;i++){
					bip[i]=(byte)(bip[i]&subnet[i]);
					bif[i]=(byte)(bif[i]&subnet[i]);
				}
				if(Arrays.equals(bip,bif)){
					device=d;
					break loop;
				}
			}
		}
		
		if(device==null)
			throw new IllegalArgumentException(ip+" is not a local address");
		
		//open Jpcap
		JpcapCaptor captor=JpcapCaptor.openDevice(device,2000,false,3000);
		captor.setFilter("arp",true);
		JpcapSender sender=captor.getJpcapSenderInstance();
		
		InetAddress srcip=null;
		for(NetworkInterfaceAddress addr:device.addresses)
			if(addr.address instanceof Inet4Address){
				srcip=addr.address;
				break;
			}

		byte[] broadcast=new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
		ARPPacket arp=new ARPPacket();
		arp.hardtype=ARPPacket.HARDTYPE_ETHER;
		arp.prototype=ARPPacket.PROTOTYPE_IP;
		arp.operation=ARPPacket.ARP_REQUEST;
		arp.hlen=6;
		arp.plen=4;
		arp.sender_hardaddr=device.mac_address;
		arp.sender_protoaddr=srcip.getAddress();
		arp.target_hardaddr=broadcast;
		arp.target_protoaddr=ip.getAddress();
		
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_ARP;
		ether.src_mac=device.mac_address;
		ether.dst_mac=broadcast;
		arp.datalink=ether;
		
		sender.sendPacket(arp);
		
		while(true){
			ARPPacket p=(ARPPacket)captor.getPacket();
			if(p==null){
				throw new IllegalArgumentException(ip+" is not a local address");
			}
			if(Arrays.equals(p.target_protoaddr,srcip.getAddress())){
				return p.sender_hardaddr;
			}
		}
	}
		
	public static void main(String[] args) throws Exception{
		if(args.length<1){
			System.out.println("Usage: java ARP <ip address>");
		}else{
			byte[] mac=ARP.arp(InetAddress.getByName(args[0]));
			for (byte b : mac)
				System.out.print(Integer.toHexString(b&0xff) + ":");
			System.out.println();
			System.exit(0);
		}
	}
}
