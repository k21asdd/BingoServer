package Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;

public final class Room {
	private static ArrayList<Socket> SocketArr = new ArrayList<Socket>();
	private static ArrayList<String> rooms =  new ArrayList<String>();
	private static Stack<Integer> mStack = new Stack<Integer>();
	
	private Room(){}
	public synchronized static int addRoom(String s, Socket guest){
		int index;
		if(mStack.isEmpty()){
			index = rooms.size();
			rooms.add(s + " " + rooms.size());
			SocketArr.add(guest);
		}
		else{
			index = mStack.pop();
			rooms.add(index, s + " " + index);
			SocketArr.add(index, guest);
		}
		return index;
	}
	public synchronized static void removeRoom(int index){
		rooms.set(index, null);
		SocketArr.set(index, null);
		mStack.push(Integer.valueOf(index));
	}
	public synchronized static Socket getRoomMaster(int index){
		return SocketArr.get(index);
	}
	public synchronized static String[] getRooms(){
		return rooms.toArray(new String[rooms.size()]);
	}
}
