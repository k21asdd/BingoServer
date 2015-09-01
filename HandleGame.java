package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HandleGame extends Thread{
	private HandleCreate.Pair Player1, Player2;
	private boolean isRunning = true;
	private boolean isReady = false;
	public HandleGame(HandleCreate.Pair creator){
		Player1 = creator;
	}
	public void addParticipant(HandleCreate.Pair guest){
		Player2 = guest;
	}
	public void finish(){
		isRunning = false;
		this.interrupt();
	}
	public boolean isReady(){
		return isReady;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+" is started !");
		try {
			if( ! (Player1.isReady() && Player2.isReady()) ){
				System.out.println("CONNECT failed !");
				return;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private class MsgDeliver extends Thread{
		BufferedReader in;
		PrintWriter out;
		public MsgDeliver(HandleCreate.Pair p1, HandleCreate.Pair p2) throws IOException{
			in = new BufferedReader(p1.getInputStreamReader());
			out = new PrintWriter(p2.getOutPutStream());
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRunning){
				
			}
			super.run();
		}
	}
}