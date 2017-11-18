//$Id$
/**
 * 
 */
package sockslib.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import sockslib.Constants;
import sockslib.common.SocksException;
import sockslib.server.msg.CommandMessage;
import sockslib.server.msg.CommandResponseMessage;
import sockslib.server.msg.ServerReply;

/**
 * @author naran-3387
 *
 */
public class Tcp2HttpsHandler extends Socks5Handler {
	
	private final static String destinationURL = "http://" + Constants.RPROXY_HOST + ":" + Constants.HTTP_PORT + Constants.RPROXY_URI;
	
	@Override
	public void doConnect(Session session, CommandMessage commandMessage) throws SocksException, IOException {
		ServerReply reply = null;
		Socket socket = session.getSocket();
		socket.setTcpNoDelay(true);
		HttpURLConnection httpConn = null;
		InetAddress bindAddress = null;
		int bindPort = 0;
		InetAddress remoteServerAddress = commandMessage.getInetAddress();
		int remoteServerPort = commandMessage.getPort();

		// set default bind address.
		byte[] defaultAddress = { 0, 0, 0, 0 };
		bindAddress = InetAddress.getByAddress(defaultAddress);
		// DO connect
		try {
			// Connect directly.
			// if (getProxy() == null) {
			// socket = new Socket(remoteServerAddress, remoteServerPort);
			// } else {
			// socket = new SocksSocket(getProxy(), remoteServerAddress, remoteServerPort);
			// }
			httpConn = (HttpURLConnection) new URL(destinationURL).openConnection();
			httpConn.addRequestProperty(Constants.AUTH_HEADER_NAME, Constants.AUTH_HEADER_VALUE);
			httpConn.addRequestProperty(Constants.DFS_IP, remoteServerAddress.toString());
			httpConn.addRequestProperty(Constants.DFS_PORT, Integer.toString(remoteServerPort));
			httpConn.setDoOutput(true);

			bindAddress = socket.getLocalAddress();
			bindPort = socket.getLocalPort();
			reply = ServerReply.SUCCEEDED;

		} catch (IOException e) {
			if (e.getMessage().equals("Connection refused")) {
				reply = ServerReply.CONNECTION_REFUSED;
			} else if (e.getMessage().equals("Operation timed out")) {
				reply = ServerReply.TTL_EXPIRED;
			} else if (e.getMessage().equals("Network is unreachable")) {
				reply = ServerReply.NETWORK_UNREACHABLE;
			} else if (e.getMessage().equals("Connection timed out")) {
				reply = ServerReply.TTL_EXPIRED;
			} else {
				reply = ServerReply.GENERAL_SOCKS_SERVER_FAILURE;
			}
			logger.info("SESSION[{}] connect {} [{}] exception:{}", session.getId(), new InetSocketAddress(remoteServerAddress, remoteServerPort), reply, e.getMessage());
		}

		CommandResponseMessage responseMessage = new CommandResponseMessage(getVersion(), reply, bindAddress, bindPort);
		session.write(responseMessage);
		if (reply != ServerReply.SUCCEEDED) { // 如果返回失败信息，则退出该方法。
			session.close();
			return;
		}

//		Pipe pipe = new ProtocolPromoPipeTemp(session.getSocket(), httpConn);
//		pipe.start();
//		httpOut.close();
//		httpConn.getInputStream();
//		pipe.setName("SESSION[" + session.getId() + "]");
//		pipe.setBufferSize(getBufferSize());
//		if (getSocksProxyServer().getPipeInitializer() != null) {
//			pipe = getSocksProxyServer().getPipeInitializer().initialize(pipe);
//		}
////		pipe.start(); // This method will build tow thread to run tow internal pipes.
//
//		// wait for pipe exit.
//		while (pipe.isRunning()) {
//			try {
//				Thread.sleep(getIdleTime());
//			} catch (InterruptedException e) {
//				pipe.stop();
//				session.close();
//				logger.info("SESSION[{}] closed", session.getId());
//			}
//		}
		
		doCopy(session.getInputStream(), httpConn.getOutputStream());
		httpConn.getOutputStream().close();
		doCopy(httpConn.getInputStream(), session.getOutputStream());
		session.getSocket().shutdownInput();
		
	}
	
	private void doCopy(InputStream is, OutputStream os) {
		StringBuffer finalStr = new StringBuffer();
		byte[] buffer = new byte[50];
		try {
			int read = 0;
             while ((read = is.read(buffer)) != -1) {
            	 String str = new String(buffer);
            	 finalStr.append(str);
                 os.write(buffer, 0, read);//, offset, read);
             }
//		String str = new String(buffer);
//   	 	finalStr.append(str);
//		is.read(buffer);
//		
//		str = new String(buffer);
//   	 	finalStr.append(str);
//		is.read(buffer);
		
//		os.write(buffer, 0, read);
        os.flush();
     } catch(Exception e) {
             System.out.println("Exception occurred while piping te streams :: " + e);
     }
	}
}
