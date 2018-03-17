package party.detection.unknown.hook.impl;
/**
 * @author GenericSkid
 * @since 12/27/2017
 */

import java.net.SocketAddress;

import party.detection.unknown.hook.Getter;

public interface NetworkManager {
	@Getter("a")
	SocketAddress  getAddress();
	
	// TODO: Hook for packet
	//void channelRead0(ChannelHandlerContext context, Packet packet);

	// TODO: Hook for packet
	//void sendPacket(Packet packet);
}
