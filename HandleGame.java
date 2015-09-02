package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HandleGame extends Thread{
	private HandleCreate.Pair Player1, Player2;
	private boolean isRunning = true;
	public HandleGame(HandleCreate.Pair creator){
		Player1 = creator;
	}
	public void addParticipant(HandleCreate.Pair guest){
		Player2 = guest;
	}
	public void finish(){
		isRunning = false;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Player1.close();
		Player2.close();
		this.interrupt();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+" is started !");
		try {
			if( ! (Player1.isReady() && Player2.isReady()) ){
				System.out.println("CONNECT failed !");
				return;
			}else{
				new MsgDeliver(Player1, Player2).start();
				new MsgDeliver(Player2, Player1).start();
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
			try{
				while(isRunning){
					out.println(in.readLine());
					out.flush();
				}
				in.close();
				out.close();
			} catch (IOException e){
				e.printStackTrace();
				return;
			}
			super.run();
		}
	}
}