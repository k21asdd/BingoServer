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
		Socket nClient;
		try {
			ServerSocket subServer = new ServerSocket(0);
			PrintWriter out = new PrintWriter(oClient.getOutputStream());
			System.out.println("New port ?" + subServer.getLocalPort());
			//read data from old socket port
			String data = new BufferedReader(new InputStreamReader(oClient.getInputStream())).readLine();
			System.out.println(data);
			out.println(subServer.getLocalPort());
			out.flush();
			nClient = subServer.accept();
			out = new PrintWriter(nClient.getOutputStream()); 
			out.println(BingoSignal.CREATE);
			int index = Room.addRoom(data, new Pair(nClient, subServer));
			out.println("Q_OK");
			out.println(index);
			out.flush();
			oClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}
	
}
