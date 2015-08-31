package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

import Server.HandleCreate.Pair;

public final class Room {
	private static ArrayList<HandleCreate.Pair> PairArr = new ArrayList<HandleCreate.Pair>();
	private static ArrayList<String> rooms =  new ArrayList<String>();
	private static Stack<Integer> mStack = new Stack<Integer>();
	
	private Room(){}
	public synchronized static int addRoom(String s, HandleCreate.Pair p) throws IOException{
		int index;
		if(mStack.isEmpty()){
			index = rooms.size();
			rooms.add(s + " " + rooms.size());
			PairArr.add(p);
		}
		else{
			index = mStack.pop();
			rooms.add(index, s + " " + index);
			PairArr.add(index, p);
		}
		return index;
	}
	public synchronized static void removeRoom(int index){
		rooms.set(index, null);
		PairArr.set(index, null);
		mStack.push(Integer.valueOf(index));
	}
	public synchronized static HandleCreate.Pair getRoomMaster(int index){
		return PairArr.get(index);
	}
	public synchronized static String[] getRooms(){
		return rooms.toArray(new String[rooms.size()]);
	}
	public class ControlSignal extends Thread{
		Socket guest;
		private BufferedReader in;
		private PrintWriter out;
		private boolean wait;
		public ControlSignal(Socket g) throws IOException {
			// TODO Auto-generated constructor stub
			guest = g;
			in = new BufferedReader(new InputStreamReader(guest.getInputStream()));
			out = new PrintWriter(guest.getOutputStream());
			out.println(BingoSignal.CREATE);
			out.flush();
			wait = true;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(wait){
				try {
					switch(Integer.valueOf(in.readLine())){
						case BingoSignal.TEARDOWN:
							wait = false;
							break;
						default:
							break;
					}
				} catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//called by CONNECT condition
		public void finsish(){
			wait = false;
			this.interrupt();
		}
	}
}
