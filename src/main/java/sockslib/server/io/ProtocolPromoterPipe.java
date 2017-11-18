package sockslib.server.io;

import java.io.IOException;
import java.net.Socket;
import java.net.URLConnection;

//$Id$

public class ProtocolPromoterPipe extends SocketPipe {

	public static final String INPUT_PIPE_NAME = "INPUT_PIPE";
	public static final String OUTPUT_PIPE_NAME = "OUTPUT_PIPE";
	public static final String ATTR_SOURCE_SOCKET = "SOURCE_SOCKET";
	public static final String ATTR_DESTINATION_SOCKET = "DESTINATION_SOCKET";
	public static final String ATTR_PARENT_PIPE = "PARENT_PIPE";

	private URLConnection connection;
	private Socket socket;
	private StreamPipe tcp2HttpsPipe;
	private StreamPipe https2TcpPipe;
	private PipeListener listener = new PipeListenerImp();

	public ProtocolPromoterPipe(Socket socket, URLConnection connection) throws IOException {
		this.socket = socket;
		this.connection = connection;

		tcp2HttpsPipe = new StreamPipe(socket.getInputStream(), connection.getOutputStream(), OUTPUT_PIPE_NAME);
		tcp2HttpsPipe.setAttribute(ATTR_SOURCE_SOCKET, socket);
		tcp2HttpsPipe.setAttribute(ATTR_DESTINATION_SOCKET, connection);
		
//		https2TcpPipe = new StreamPipe(connection.getInputStream(), socket.getOutputStream(), INPUT_PIPE_NAME);
//		https2TcpPipe.setAttribute(ATTR_SOURCE_SOCKET, connection);
//		https2TcpPipe.setAttribute(ATTR_DESTINATION_SOCKET, socket);

		tcp2HttpsPipe.addPipeListener(listener );
//		https2TcpPipe.addPipeListener(listener);
		tcp2HttpsPipe.setAttribute(ATTR_PARENT_PIPE, this);
//		https2TcpPipe.setAttribute(ATTR_PARENT_PIPE, this);
	}

}
