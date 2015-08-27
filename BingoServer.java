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
				new BingoHandle(client).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class BingoHandle extends Thread {
		private Socket guest;
		
		public BingoHandle(Socket guest) throws IOException{
			this.guest = guest;
			
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
			PrintWriter pw = new PrintWriter(guest.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(guest.getInputStream()));
			int signal = Integer.valueOf(in.readLine());
			
			switch(signal){
			case BingoSignal.QUERY:
				//Begin signal
				pw.println("Q_START");
				pw.flush();
				for(String room : Room.getRooms())
					pw.println(room);
				//Ending signal
				pw.println("Q_DONE");
				pw.flush();
				
				//send back
				break;
			case BingoSignal.CREATE:
				int index = Room.addRoom(in.readLine(), guest);
				pw.println("Q_OK " + String.valueOf(index));
				break;
			case BingoSignal.CONNECT:
				//send to both
				int mIndex = Integer.valueOf(in.readLine());
				new HandleGame(guest, Room.getRoomMaster(mIndex)).start();
				new HandleGame(Room.getRoomMaster(mIndex), guest).start();
				break;
			default:
				//garbage
				break;
			}
			
			in.close();
			in = null;
			pw.close();
			pw = null;
		}
	}

}
