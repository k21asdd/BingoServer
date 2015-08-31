package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HandleCreate extends Thread{
	Socket oClient;
	public HandleCreate(Socket c) {
		// TODO Auto-generated constructor stub
		oClient = c;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader in;
		PrintWriter pw;
		Socket nClient;
		try {
			ServerSocket subServer = new ServerSocket(0);
			//read data from old socket port
			String data = new BufferedReader(new InputStreamReader(oClient.getInputStream())).readLine();
			new PrintWriter(oClient.getOutputStream(), true).println(subServer.getLocalPort());
			nClient = subServer.accept();
			new PrintWriter(nClient.getOutputStream()).println(BingoSignal.CREATE);
			int index = Room.addRoom(data, new Pair(nClient, subServer));
			new PrintWriter(nClient.getOutputStream()).println("Q_OK" + " " + index );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}
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
	}
}
