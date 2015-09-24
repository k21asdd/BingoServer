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
			PrintWriter out = new PrintWriter(oClient.getOutputStream());
			//read data from old socket port
			String data = new BufferedReader(new InputStreamReader(oClient.getInputStream())).readLine();
			System.out.println(data);
			if(data == null){
				System.out.println("Creation Abort");
				return;
			}
			//Create New port for specific client
			ServerSocket subServer = new ServerSocket(0);
			System.out.println("New port ?" + subServer.getLocalPort());
			out.println(subServer.getLocalPort());
			out.flush();
			//wait for client
			nClient = subServer.accept();
			System.out.println("Accept");
			oClient.close();
			//get output stream of new port
			out = new PrintWriter(nClient.getOutputStream()); 
			int index = Room.addRoom(data, new Pair(nClient, subServer));
			out.println("Q_OK");
			out.println(index);
			out.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.run();
	}
	
}
