package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Pair{
	ServerSocket subServer;
	Socket nClient;
	
	public Pair(Socket c, ServerSocket s){
		subServer = s; nClient = c;
	}
	public void close(){
		try {
			subServer.close(); subServer = null;
			nClient.close(); nClient = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public boolean isReady() throws IOException{
		BufferedReader in = new BufferedReader(new InputStreamReader(nClient.getInputStream()));
		return Integer.valueOf(in.readLine()) == BingoSignal.GAME_READY;
	}
	public InputStreamReader getInputStreamReader() throws IOException{
		return new InputStreamReader(nClient.getInputStream());
	}
	public OutputStream getOutPutStream() throws IOException{
		return nClient.getOutputStream();
	}
}