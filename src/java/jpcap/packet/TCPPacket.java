package jpcap.packet;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class represents TCP packet.
 */
public class TCPPacket extends IPPacket {
    private static final long serialVersionUID = -8856988406589484129L;

    /**
     * Source port number
     */
    public int src_port;
    /**
     * Destination port number
     */
    public int dst_port;
    /**
     * Sequence number
     */
    public long sequence;
    /**
     * ACK number
     */
    public long ack_num;
    /**
     * URG flag
     */
    public boolean urg;
    /**
     * ACK flag
     */
    public boolean ack;
    /**
     * PSH flag
     */
    public boolean psh;
    /**
     * RST flag
     */
    public boolean rst;
    /**
     * SYN flag
     */
    public boolean syn;
    /**
     * FIN flag
     */
    public boolean fin;

    // added by Damien Daspit 5/7/01
    /**
     * RSV1 flag
     */
    public boolean rsv1;
    /**
     * RSV2 flag
     */
    public boolean rsv2;
    // *****************************

    /**
     * Window size
     */
    public int window;
    /**
     * Urgent pointer
     */
    public short urgent_pointer;

    /**
     * TCP option
     */
    public byte[] option;

    /**
     * Creates a TCP packet.
     *
     * @param rsv1     RSV1 flag
     * @param rsv2     RSV2 flag
     * @param src_port Source port number
     * @param dst_port Destination port number
     * @param sequence sequence number
     * @param ack_num  ACK number
     * @param urg      URG flag
     * @param ack      ACK flag
     * @param psh      PSH flag
     * @param rst      RST flag
     * @param syn      SYN flag
     * @param fin      FIN flag
     * @param window   window size
     * @param urgent   urgent pointer
     */
    public TCPPacket(int src_port, int dst_port, long sequence, long ack_num,
                     boolean urg, boolean ack, boolean psh, boolean rst,
                     boolean syn, boolean fin, boolean rsv1, boolean rsv2,
                     int window, int urgent) {
        this.src_port = src_port;
        this.dst_port = dst_port;
        this.sequence = sequence;
        this.ack_num = ack_num;
        this.urg = urg;
        this.ack = ack;
        this.psh = psh;
        this.rst = rst;
        this.syn = syn;
        this.fin = fin;
        // added by Damien Daspit 5/7/01
        this.rsv1 = rsv1;
        this.rsv2 = rsv2;
        // *****************************
        this.window = window;
        urgent_pointer = (short) urgent;
    }

    void setValue(int src, int dst, long seq, long ack_num, boolean urg, boolean ack,
                  boolean psh, boolean rst, boolean syn, boolean fin, boolean rsv1, boolean rsv2,
                  int win, short urp) {
        src_port = src;
        dst_port = dst;
        sequence = seq;
        this.ack_num = ack_num;
        this.urg = urg;
        this.ack = ack;
        this.psh = psh;
        this.rst = rst;
        this.syn = syn;
        this.fin = fin;
        // added by Damien Daspit 5/7/01
        this.rsv1 = rsv1;
        this.rsv2 = rsv2;
        // *****************************
        window = win;
        urgent_pointer = urp;
    }

    void setOption(byte[] option) {
        this.option = option;
    }

    String findPid(int src_port) throws IOException, InterruptedException {

    	String cmd = "netstat -ano -p TCP | findstr " + dst_ip.getHostAddress() +":"+ dst_port;
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
	    	String[] slice = output.replaceAll(" +", " ").split(" ");
	    	int pid = Integer.parseInt(slice[slice.length-1].trim());
	    	
	    	cmd = "tasklist /FO CSV | findstr \"" + pid + "\"";
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
     * Returns a string representation of this packet<BR>
     * <p/>
     * <BR>
     * Format: src_port > dst_port seq(sequence) win(window) [ack ack_num] [S][F][P]
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
        return super.toString() + " TCP " + processInfo + " " + 
                src_port + " > " + dst_port + " seq(" + sequence +
                ") win(" + window + ")" + (ack ? " ack " + ack_num : "") + " " +
                (syn ? " S" : "") + (fin ? " F" : "") + (psh ? " P" : "") +
                (rst ? " R" : "") + (urg ? " U" : "");
    }
}
