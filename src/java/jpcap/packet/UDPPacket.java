package jpcap.packet;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents UDP packet.
 */
public class UDPPacket extends IPPacket {
    private static final long serialVersionUID = -3170544240823207254L;

    /**
     * Source port number
     */
    public int src_port;
    /**
     * Destination port number
     */
    public int dst_port;
    /**
     * packet length
     */
    public int length;

    /**
     * Creates a UDP packet.
     *
     * @param src_port source port number
     * @param dst_port destination port number
     */
    public UDPPacket(int src_port, int dst_port) {
        this.src_port = src_port;
        this.dst_port = dst_port;
    }

    void setValue(int src, int dst, int len) {
        src_port = src;
        dst_port = dst;
        length = len;
    }

    String findPid(int src_port) throws IOException, InterruptedException {

    	String cmd = "netstat -ano -p UDP | findstr " + src_ip.getHostAddress() +":"+ src_port;
    	System.out.println(cmd);
    	ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/C", cmd);
        Process process = builder.start();
        process.waitFor();
    	InputStream inputStream = process.getInputStream();
	    int bytesRead = -1;
	    byte[] bytes = new byte[1024];
	    String output = "";
	    while((bytesRead = inputStream.read(bytes)) > -1){
	        output = output + new String(bytes, 0, bytesRead);
	    }
	    if (!output.isEmpty()) {
	    	System.out.println(output);
	    	String[] slice = output.trim().replaceAll(" +", " ").split(" ");
	    	int pid = Integer.parseInt(slice[slice.length-1]);
	    	
	    	cmd = "tasklist /FO CSV | findstr \"" + pid + "\"";
	    	System.out.println(cmd);
	    	builder = new ProcessBuilder("cmd.exe", "/C", cmd);
	    	process = builder.start();
	        process.waitFor();
	    	inputStream = process.getInputStream();
	    	bytesRead = -1;
		    bytes = new byte[1024];
		    output = "";
		    while((bytesRead = inputStream.read(bytes)) > -1){
		        output = output + new String(bytes, 0, bytesRead);
		    }
		    if (!output.isEmpty()) {
		    	slice = output.split(",");
		    	return slice[0].replace("\"", "") + "("+ pid +")";
		    }
	    }
	    return "";
    }
    /**
     * Returns a string representation of this packet.<BR>
     * <p/>
     * <BR>
     * Format: src_port > dst_port
     *
     * @return a string representation of this packet
     */
    public String toString() {
    	String processInfo = "";
    	try {
    		processInfo = findPid(src_port);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return super.toString() + " UDP " + processInfo + " " + src_port + " > " + dst_port;
    }
}
