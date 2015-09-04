package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

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
			PrintWriter pw = new PrintWriter(client.getOutputStream());
			System.out.println(Thread.currentThread().getName()+" Wait for Signal");
			int signal = Integer.valueOf(in.readLine());
			switch(signal){
			case BingoSignal.QUERY:
				//Begin signal
				System.out.println(Thread.currentThread().getName()+" QUERY");
				pw.println(BingoSignal.QUERY);
				/*for(String info: Room.getRoomsInfo())
					pw.println(info);*/
				pw.println("AAA");
				pw.println("BBB");
				pw.println("CCC");
				pw.println("Q_DONE");
				pw.flush();
				System.out.println(Thread.currentThread().getName()+" QUERY DONE");
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
				int index = Integer.valueOf(in.readLine());
				Room.removeRoom(index);
				pw.println(BingoSignal.TEARDOWN);
				pw.flush();
				System.out.println(Thread.currentThread().getName()+" TEARDOWN DONE");
			}
			case BingoSignal.CONNECT:{
				//send to both
				System.out.println(Thread.currentThread().getName()+" CONNECT");
				int mIndex = Integer.valueOf(in.readLine());
				ServerSocket nServer = new ServerSocket(0);
				pw.println(nServer.getLocalPort()); pw.flush();
				Socket participant = nServer.accept();
				Pair guest = new Pair(participant, nServer);
				new PrintWriter(Room.getRoom(mIndex).getCreator().getOutPutStream(), true).println(BingoSignal.CONNECT);
				Room.getRoom(mIndex).addParticipant(guest);
				Room.getRoom(mIndex).GameRoomStart();
				System.out.println(Thread.currentThread().getName()+" CONNECT DONE");
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
		}
	}
}
