package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class HandleGame extends Thread{
	private Pair Player1, Player2;
	private boolean isRunning = true;
	public HandleGame(Pair creator){
		Player1 = creator;
	}
	public Pair getCreator(){
		return Player1;
	}
	public void addParticipant(Pair guest){
		Player2 = guest;
	}
	public void deleteParticipant(){
		if(Player2 == null)return;
		Player2 = null;
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
				new MsgDeliver(Player1, Player2).start();
				new MsgDeliver(Player2, Player1).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class MsgDeliver extends Thread{
		BufferedReader in;
		PrintWriter out;
		public MsgDeliver(Pair p1, Pair p2) throws IOException{
			in = new BufferedReader(p1.getInputStreamReader());
			out = new PrintWriter(p2.getOutPutStream());
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				while(isRunning){
					String s = in.readLine();
					if(s.isEmpty())isRunning = false;
					System.out.println(this.getName() + " " + s);
					out.println(s);
					out.flush();
				}
			} catch (IOException e){
				e.printStackTrace();
				return;
			}
			super.run();
		}
	}
}