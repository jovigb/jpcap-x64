package jpcap.api;

import jpcap.packet.Packet;

public interface PacketHandler {
    void handle(Packet packet);
}
