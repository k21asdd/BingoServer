package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import Server.Pair;

public final class Room {
	private static ArrayList<Room> GameRooms = new ArrayList<Room>();
	private static Stack<Integer> mStack = new Stack<Integer>();
	private HandleGame mHandleGame;
	private String RoomInfo;
	private boolean full = false;
	private boolean GameStart = false;
	
	private Room(Pair creator, String info){
		mHandleGame = new HandleGame(creator);
		RoomInfo = info;
	}
	private String getRoomInfo(){
			return RoomInfo;
	}
	public Pair getCreator(){
		return mHandleGame.getCreator();
	}
	public void addParticipant(Pair guest){
		full = true;
		synchronized (mHandleGame) {
			mHandleGame.addParticipant(guest);
		}
	}
	public void deleteParticipant(){
		synchronized (mHandleGame) {
			mHandleGame.deleteParticipant();
		}
	}
	public void GameRoomStart(){
		if( !full ){
			System.out.println("Room not full");
			return;
		}
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
	public synchronized static Room getRoom(int index){
		return GameRooms.get(index);
	}
	public synchronized static int addRoom(String info, Pair creator) throws IOException{
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
