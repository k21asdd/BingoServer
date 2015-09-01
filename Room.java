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
	private static ArrayList<Room> GameRooms = new ArrayList<Room>();
	private static Stack<Integer> mStack = new Stack<Integer>();
	private HandleGame mHandleGame;
	private String RoomInfo;
	private boolean GameStart = false;
	
	private Room(HandleCreate.Pair creator, String info){
		mHandleGame = new HandleGame(creator);
		RoomInfo = info;
	}
	private String getRoomInfo(){
			return RoomInfo;
	}
	public void addParticipant(HandleCreate.Pair guest){
		mHandleGame.addParticipant(guest);
	}
	public void GameRoomStart(){
		GameStart = true;
		mHandleGame.start();
	}
	private void deleteSelf(){
		if(GameStart)
			mHandleGame.finish();
		mHandleGame = null;
	}
	public static String[] getRoomsInfo(){
		synchronized (GameRooms) {
			String [] data = new String[GameRooms.size()];
			for(int i = 0 ; i < GameRooms.size() ; i++)
				data[i] = GameRooms.get(i).getRoomInfo();
			return data;
		}
	}
	public synchronized static int addRoom(String info, HandleCreate.Pair creator) throws IOException{
		int index;
		if(mStack.isEmpty()){
			index = GameRooms.size();
			GameRooms.add(new Room(creator, info + " " + index));
		}
		else{
			index = mStack.pop();
			GameRooms.add(index, new Room(creator, info + " " + index));
		}
		return index;
	}
	public synchronized static void removeRoom(int index){
		GameRooms.get(index).deleteSelf();
		GameRooms.set(index, null);
		mStack.push(Integer.valueOf(index));
	}
}
