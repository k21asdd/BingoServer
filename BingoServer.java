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
	
	private static HashMap<Integer, Socket> MasterSocket = null;
	public static ArrayList<String> rooms = null;
	private final int Sport = 5566; 
	private ServerSocket server;
	
	public static void main(String args[]){
		new BingoServer().run();
	}
	public BingoServer(){
		try {
			server = new ServerSocket(Sport);
			if(rooms == null)
				rooms = new ArrayList<String>();
			if(MasterSocket == null)
				MasterSocket = new HashMap<Integer, Socket>();
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
	private static final class UniqNumberGen{
		private UniqNumberGen(){};
		private static int number = 0;
		private static Stack<Integer> mStack = null;
		public static int GenNumber(){
			synchronized (mStack) {
				if(mStack == null)mStack = new Stack<Integer>();
				if(mStack.isEmpty())return number++;
				return mStack.pop();
			}
		}
		public static void StoreNumber(int i) throws ArithmeticException{
			synchronized (mStack) {
				if(mStack == null)mStack = new Stack<Integer>();
				if(i < 0) throw new ArithmeticException();
				else mStack.push(Integer.valueOf(i));
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
		private void handleMessage() throws IOException{
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(guest.getInputStream()));
			int signal = Integer.valueOf(in.readLine());
			switch(signal){
			case BingoSignal.QUERY:
				PrintWriter pw = new PrintWriter(guest.getOutputStream());
				//Begin signal
				pw.println("Q_START");
				pw.flush();
				synchronized (BingoServer.rooms){
					for(String room : BingoServer.rooms)
						pw.println(room);
				}
				//Ending signal
				pw.println("Q_DONE");
				pw.flush();
				pw.close();
				pw = null;
				//send back
				break;
			case BingoSignal.CREATE:
				//add info to room
				int num = UniqNumberGen.GenNumber();
				//s = name room_name grid number
				synchronized (rooms) {
					String s = in.readLine();
					rooms.add(s + " " + num);
				}
				break;
			case BingoSignal.CONNECT:
				//send to both
				new HandleGame(guest, find Socket);
;				break;
			default:
				//garbage
				break;
			}
			in.close();
			in = null;
		}
	}
}
