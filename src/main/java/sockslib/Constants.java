//$Id$
package sockslib;

public interface Constants {
	
	//TCP2HTTP(US3 DC) Proxy constants
	String PROXY_HOST	= "127.0.0.1"; //IP or hostname
	int PROXY_TCP_PORT 	= 50020;
	
	//HTTP2TCP(Estancia) Proxy constants
	String RPROXY_HOST	= "localhost"; //IP or hostname
	String RPROXY_URI	= "/http2tcp";
	int HTTP_PORT 		= 7777;
	int RPROXY_TCP_PORT = 50020;
	
	int BUFFER_SIZE 	= 4096;
	String AUTH_HEADER_NAME = "Authorization";
	String AUTH_HEADER_VALUE = "SimpleAuth";
	String DFS_IP = "DFS_IP";
	String DFS_PORT = "DFS_PORT";
	String DESTINATION_URL = "";
}