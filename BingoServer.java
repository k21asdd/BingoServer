package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class BingoServer {
	
	
	private final int Sport = 55166; 
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
//			QUERY : 
//			CREATE : RoomName UserName Grid
//			CONNECT : Index
//			TEARDOWN : Index
//		Room store format
//			UserName RoomName Grid Index
		private void handleMessage() throws IOException{
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter pw = new PrintWriter(client.getOutputStream());
			System.out.println(Thread.currentThread().getName()+" Wait for Signal");
			String ss = in.readLine();
			int signal = Integer.valueOf(ss);
			switch(signal){
			case BingoSignal.QUERY:
				//Begin signal
				System.out.println(Thread.currentThread().getName()+" QUERY");
				pw.println(BingoSignal.QUERY);
				/*for(String info: Room.getRoomsInfo())
					pw.println(info);*/
				for(String s : Room.getRoomsInfo())
					pw.println(s);
				pw.println("Q_DONE");
				pw.flush();
				System.out.println(Thread.currentThread().getName()+" QUERY DONE");
				for(String s : Room.getRoomsInfo())
					System.out.println(s);
				//Ending signal
				//send back
				break;
			case BingoSignal.CREATE:{
				System.out.println(Thread.currentThread().getName()+" CREATE");
				new HandleCreate(client).start(); 
				System.out.println(Thread.currentThread().getName()+" CREATE DONE");
				break;
			}
			case BingoSignal.TEARDOWN :{
				System.out.println(Thread.currentThread().getName()+" TEARDOWN");
				for(String s : Room.getRoomsInfo())
					System.out.println(s);
				int index = Integer.valueOf(in.readLine());
				Room.removeRoom(index);
				pw.println(BingoSignal.TEARDOWN);
				pw.flush();
				System.out.println(Thread.currentThread().getName()+" TEARDOWN DONE");
				break;
			}
			case BingoSignal.CONNECT:{
				//send to both
				System.out.println(Thread.currentThread().getName()+" CONNECT");
				int mIndex = Integer.valueOf(in.readLine());
				ServerSocket nServer = new ServerSocket(0);
				pw.println(nServer.getLocalPort()); pw.flush();
				Socket participant = nServer.accept();
				System.out.println(Thread.currentThread().getName()+" ACCEPT");
				Pair guest = new Pair(participant, nServer);
				Room.getRoom(mIndex).addParticipant(guest);
				System.out.println(Thread.currentThread().getName()+" GUEST");
				Room.getRoom(mIndex).GameRoomStart();
				System.out.println(Thread.currentThread().getName()+" CONNECT DONE");
				break;
			} 
			default:
				//garbage
				System.out.println(ss);
				break;
			}

		}
	}
}
