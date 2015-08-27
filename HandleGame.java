package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HandleGame extends Thread{
	private Socket guest;
	private Socket master;
	private BufferedReader in;
	public HandleGame(Socket guest, Socket master) throws IOException{
		this.guest = guest;
		this.master = master;
		in = new BufferedReader(new InputStreamReader(master.getInputStream()));
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+" is started !");
		String data;
		while(true){
			try {
				handleMessage(Integer.valueOf(in.readLine()),in.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(Thread.currentThread().getName()+" is closed !");
				break;
			}
		}
		try {
			new PrintWriter(master.getOutputStream(),true).println("AiaiBianOK");
			System.out.println(master.getPort()+" : close");
			master.close();
			master = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void handleMessage(int signal, String data) throws IOException{
		PrintWriter out= new PrintWriter(guest.getOutputStream());
		out.println(signal);
		out.println(data);
		out.flush();
		System.out.println("Send to "+guest.getPort());
		if(signal == BingoSignal.CLOSE){
			String s = in.readLine();
			if(s.equals("Q_CLOSE"))
				this.interrupt();
			else
				throw new IOException();
		}
	}
}
