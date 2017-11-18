//$Id$
package sockslib.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolPromoPipeTemp implements Pipe {

	protected static final Logger logger = LoggerFactory.getLogger(SocketPipe.class);

	public static final String INPUT_PIPE_NAME = "INPUT_PIPE";
	public static final String OUTPUT_PIPE_NAME = "OUTPUT_PIPE";
	public static final String ATTR_SOURCE_SOCKET = "SOURCE_SOCKET";
	public static final String ATTR_DESTINATION_SOCKET = "DESTINATION_SOCKET";
	public static final String ATTR_PARENT_PIPE = "PARENT_PIPE";

	private String name;
	private Map<String, Object> attributes = new HashMap<>();

	private URLConnection connection;
	private Socket socket;
	private StreamPipe tcp2HttpsPipe;
	private StreamPipe https2TcpPipe;
	private PipeListener listener = new PipeListenerImp();

	public ProtocolPromoPipeTemp(Socket socket, URLConnection connection) throws IOException {
		this.socket = socket;
		this.connection = connection;

		tcp2HttpsPipe = new StreamPipe(socket.getInputStream(), connection.getOutputStream(), OUTPUT_PIPE_NAME);
		tcp2HttpsPipe.setAttribute(ATTR_SOURCE_SOCKET, socket);
		tcp2HttpsPipe.setAttribute(ATTR_DESTINATION_SOCKET, connection);

		// https2TcpPipe = new StreamPipe(connection.getInputStream(), socket.getOutputStream(), INPUT_PIPE_NAME);
		// https2TcpPipe.setAttribute(ATTR_SOURCE_SOCKET, connection);
		// https2TcpPipe.setAttribute(ATTR_DESTINATION_SOCKET, socket);

		tcp2HttpsPipe.addPipeListener(listener);
		// https2TcpPipe.addPipeListener(listener);
		tcp2HttpsPipe.setAttribute(ATTR_PARENT_PIPE, this);
		// https2TcpPipe.setAttribute(ATTR_PARENT_PIPE, this);
	}

	@Override
	public boolean start() {
		//return tcp2HttpsPipe.start();
		doCopy();
		return true;
	}

	@Override
	public boolean stop() {
		return tcp2HttpsPipe.stop();
	}

	@Override
	public boolean close() {
		return tcp2HttpsPipe.close();
	}

	@Override
	public int getBufferSize() {
		return tcp2HttpsPipe.getBufferSize();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		tcp2HttpsPipe.setBufferSize(bufferSize);
	}

	@Override
	public boolean isRunning() {
		return tcp2HttpsPipe.isRunning();
	}

	@Override
	public void addPipeListener(PipeListener pipeListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePipeListener(PipeListener pipeListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;

	}

	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	private void doCopy(InputStream is, OutputStream os) {
		byte[] buffer = new byte[this.getBufferSize()];
		try {
			int read = 0;
             while ((read = is.read(buffer)) != -1) {
                     os.write(buffer, 0, read);//, offset, read);
             }
            os.flush();
     } catch(Exception e) {
             System.out.println("Exception occurred while piping te streams :: " + e);
     }
	}

	/**
	 * The class <code>PipeListenerImp</code> is a pipe listener.
	 *
	 * @author Youchao Feng
	 * @version 1.0
	 * @date Apr 15, 2015 9:05:45 PM
	 */
	private class PipeListenerImp implements PipeListener {

		@Override
		public void onStop(Pipe pipe) {
			StreamPipe streamPipe = (StreamPipe) pipe;
			logger.trace("Pipe[{}] stopped", streamPipe.getName());
			close();
		}

		@Override
		public void onStart(Pipe pipe) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTransfer(Pipe pipe, byte[] buffer, int bufferLength) {
		}

		@Override
		public void onError(Pipe pipe, Exception exception) {
			logger.info("{} {}", name, exception.getMessage());
		}

	}
}
