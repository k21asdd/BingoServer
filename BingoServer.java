package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class BingoServer {
	
	
	private final int Sport = 5566; 
	private ServerSocket server;
	
	public static void main(String args[]){
		
		new BingoServer().run();
	}
	public BingoServer(){
		try {
			
			server = new ServerSocket(Sport);
			
		} catch(BindException e){
			System.out.println("Port is been used !");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		while(server != null){
			try {
				Socket client = server.accept();
				new BingoDispatch(client).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class BingoDispatch extends Thread {
		private Socket client;
		
		public BingoDispatch(Socket guest) throws IOException{
			this.client = guest;
			
		}
		public void run(){
			System.out.println(Thread.currentThread().getName()+" is started !");
			try {
				handleMessage();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(Thread.currentThread().getName()+" is closed !");
			}
		}
//		Message format, always two lines
//		signal\r\n
//		data\r\n
//			QUERY : Time ?
//			CREATE : UserName RoomName Grid
//			CONNECT : Index
//		Room store format
//			UserName RoomName Grid Index
		private void handleMessage() throws IOException{
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			int signal = Integer.valueOf(in.readLine());
			switch(signal){
			case BingoSignal.QUERY:{
				//Begin signal
				PrintWriter pw = new PrintWriter(client.getOutputStream());
				pw.println("Q_START");
				pw.flush();
				for(String room : Room.getRooms())
					pw.println(room);
				//Ending signal
				pw.println("Q_DONE");
				pw.flush();
				//send back
				break;
			}
			case BingoSignal.CREATE:{
				new HandleCreate(client).start(); 
				break;
			}
			case BingoSignal.CONNECT:{
				//send to both
				int mIndex = Integer.valueOf(in.readLine());
				ServerSocket nServer = new ServerSocket(0);
				Socket player1,player2;
				pw.println(BingoSignal.CONNECT);
				pw.println(nServer.getLocalPort());
				pw.flush();
				pw.close();
				pw = new PrintWriter(Room.getRoomMaster(mIndex).getOutputStream());
				pw.println(BingoSignal.CONNECT);
				pw.println(nServer.getLocalPort());
				pw.flush();
				System.out.println("Start connect");
				player1 = nServer.accept();
				player2 = nServer.accept();
				new HandleGame(player1, player2).start();
				new HandleGame(player2, player1).start();
				System.out.println("End connect");
				break;
			}
			default:
				//garbage
				break;
			}
			
			in.close();
			in = null;
			pw.close();
			pw = null;
			guest.close();
		}
	}
	private class 

}
