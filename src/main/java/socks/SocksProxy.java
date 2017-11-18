package socks;

import java.io.IOException;

import sockslib.server.SocksProxyServer;
//$Id$
import sockslib.server.SocksServerBuilder;

public class SocksProxy {
	public static void main(String[] args) throws IOException {
		SocksProxyServer proxyServer = SocksServerBuilder.buildAnonymousSocks5Server(); 
	    proxyServer.start();// Creat a SOCKS5 server bind at port 1080
	}
}
