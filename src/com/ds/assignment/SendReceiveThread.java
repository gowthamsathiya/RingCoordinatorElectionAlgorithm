package com.ds.assignment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;

public class SendReceiveThread implements Runnable{
	int MAX_LEN = 60; 
	
	private DatagramSocket mySocket;
	private DatagramPacket packet; 
	private int pid;
	private int coordId;
	private boolean stopToken = false;
	private CommunicationData data;
	
	@Override
	public void run() {
		try{ 
			pid = Process.ProcessID;
			//if(pid == 4) Process.processTypeLabel.setText("hudo");
			//System.out.println("socket port for"+pid+CommunicationData.port[Process.ProcessID]);
			data = new CommunicationData();
			mySocket = new DatagramSocket(data.getPort(pid)); 
			 
			 //sendMessage();
			 checkMessage();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void checkMessage() throws IOException, InterruptedException{
		sendInitMessage();
		while(true){
			//System.out.println("Thread id "+pid+" Process ID "+Process.ProcessID);
			byte[ ] recvBuffer = new byte[MAX_LEN];
			//System.out.println("byte array "+recvBuffer);
			packet = new DatagramPacket(recvBuffer, MAX_LEN);
			mySocket.receive(packet);
			//sendACK(packet);
			//System.out.println("byte array after receiving data "+recvBuffer);
			//System.out.println("byte array length "+recvBuffer.length);
			String message = new String(recvBuffer);
			//System.out.println("type of mess received in "+pid+message);
			if(!message.substring(0, 4).equals("INIT")&&!message.substring(0, 5).equals("CRASH")&&!message.substring(0, 5).equals("RPORT")){
				Process.conversationTextArea.append("Message received : "+message+"\n");
			}
			if(message.substring(0, 5).equals("ELECT")){
				System.out.println(pid+" received "+message);
				if(Character.getNumericValue(message.charAt(5)) == pid){
					System.out.println(pid+" One complete round end of token");
					Process.conversationTextArea.append("One complete round end of token. Electing Co ordinator\n");
					int max = 0;
					message = message.trim();
					for(int count = 5 ; count<message.length(); count++){
						int npid = Character.getNumericValue(message.charAt(count));
						if(npid>max) max = npid;
					}
					Process.conversationTextArea.append("Process "+max+" is elected as Co-ordinator\n");
					System.out.println(pid+" sending"+"COORD"+max+pid);
					Thread.currentThread().sleep(1000);
					sendMessage("COORD"+max+pid);
					/**
					int success = receiveACK("COORD"+max+pid);
					while(success != 0){
						sendMessage("COORD"+max+pid);
						success = receiveACK("COORD"+max+pid);
					}
					**/
				}
				else{
					Process.conversationTextArea.append("Passing ELECT to next process\n");
					System.out.println(pid+" sending to next "+message.trim()+pid);
					Thread.currentThread().sleep(1000);
					sendMessage(message.trim()+pid);
					/**
					int success = receiveACK(message.trim()+pid);
					while(success != 0){
						sendMessage(message.trim()+pid);
						success = receiveACK(message.trim()+pid);
					}
					**/
				}
			}
			else if(message.substring(0, 5).equals("COORD")){
				if(Character.getNumericValue(message.charAt(5)) == pid){
					Process.conversationTextArea.append("I am elected as Co-ordinator\n");
					Process.processIconLabel.setIcon(new ImageIcon("C:\\Users\\Gowtham\\workspace\\RingElection\\co-ordinator-icon.jpg"));
					Process.processTypeLabel.setText("Co-ordinator");
					if(Character.getNumericValue(message.charAt(6)) == pid){
						coordId = Character.getNumericValue(message.charAt(5));
						Process.conversationTextArea.append("Elected COORD is conveyed to every one. Deleting message\n");
					}
					else{
						Process.conversationTextArea.append("Passing COORD to next process\n");
						Thread.currentThread().sleep(1000);
						sendMessage(message);
						/**
						int success = receiveACK(message);
						while(success != 0){
							sendMessage(message);
							success = receiveACK(message);
						}
						**/
					}
				}
				else{
					Process.processIconLabel.setIcon(new ImageIcon("C:\\Users\\Gowtham\\workspace\\RingElection\\process.jpg"));
					Process.processTypeLabel.setText("Process");
					
					if(Character.getNumericValue(message.charAt(6)) == pid){
						coordId = Character.getNumericValue(message.charAt(5));
						Process.conversationTextArea.append("Elected COORD is conveyed to every one. Deleting message\n");
					}
					else{
						Process.conversationTextArea.append("Passing COORD to next process\n");
						Thread.currentThread().sleep(1000);
						sendMessage(message);
						/**
						int success = receiveACK(message);
						while(success != 0){
							sendMessage(message);
							success = receiveACK(message);
						}
						**/
					}
				}
				if (Character.getNumericValue(message.charAt(5)) != pid){
					coordId = Character.getNumericValue(message.charAt(5));
					Process.conversationTextArea.append("process "+coordId+" is elected as Co-ordinator\n");
					if(Character.getNumericValue(message.charAt(6)) == pid){
						coordId = Character.getNumericValue(message.charAt(5));
						Process.conversationTextArea.append("Elected COORD is conveyed to every one. Deleting message\n");
					}
					else{
						Process.conversationTextArea.append("Passing COORD to next process\n");
						Thread.currentThread().sleep(1000);
						sendMessage(message);
						/**
						int success = receiveACK(message);
						while(success != 0){
							sendMessage(message);
							success = receiveACK(message);
						}
						**/
					}
				}
				
			}
			else if(message.substring(0, 5).equals("CRASH")){
				System.out.println("Crash received "+message);
				requestRemovePort(Character.getNumericValue(message.charAt(5)));
				Thread.currentThread().sleep(5000);
				sendMessage("ELECT");
			}
			else if(message.substring(0, 5).equals("RPORT")){
				removePort(Character.getNumericValue(message.charAt(5)));
			}
			else if(message.substring(0, 4).equals("INIT")){
				addPort(Character.getNumericValue(message.charAt(4)),Integer.parseInt(message.substring(5).trim()));
			}
			//System.out.println(pid+" receving "+message);
		}
	}
	int retry=0;
	public void sendMessage(String message) throws IOException, InterruptedException{
		//Thread.currentThread().sleep(3000);
		//String message = "message from "+pid;
		//System.out.println("sending "+message);
		if(message.equals("ELECT")){
			Process.conversationTextArea.append("Sending ELECT from "+this.pid+"\n");
			System.out.println("Sending ELECT from "+this.pid);
			message = message+pid;
			//Process.conversationTextArea.append("Sending "+message+"\n");
		}
		InetAddress receiverHost = InetAddress.getByName("localhost");
		Process.conversationTextArea.append("Sending "+message+"\n");
		System.out.println("message to be sent "+ message);
		byte[] sendBuffer = message.getBytes( );
		System.out.println(pid+"sending to "+data.getNextPort(pid+retry));
		DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, 
				 receiverHost, data.getNextPort(pid+retry)); 
		mySocket.send(packet);
		/**
		byte[ ] ackrecvBuffer = new byte[MAX_LEN];
		packet = new DatagramPacket(ackrecvBuffer, MAX_LEN);
		mySocket.receive(packet);
		String ackreceived = new String(ackrecvBuffer);
		System.out.println(ackreceived);
		if(ackreceived.trim().equalsIgnoreCase("ACK")){
			//ack received successfully
			retry = 0;
			System.out.println("resetting retry to "+retry);
			Process.conversationTextArea.append("resetting retry to "+retry);
		}
		else{
			retry = 1;
			System.out.println("setting retry to "+retry);
			Process.conversationTextArea.append("setting retry to "+retry);
			sendMessage(message);
		}
		**/
	}
	
	public int receiveACK(String message) throws IOException, InterruptedException{
		byte[ ] ackrecvBuffer = new byte[MAX_LEN];
		packet = new DatagramPacket(ackrecvBuffer, MAX_LEN);
		mySocket.receive(packet);
		String ackreceived = new String(ackrecvBuffer);
		System.out.println(ackreceived);
		if(ackreceived.trim().equalsIgnoreCase("ACK")){
			//ack received successfully
			retry = 0;
			System.out.println("resetting retry to "+retry);
			Process.conversationTextArea.append("resetting retry to "+retry);
			return retry;
		}
		else{
			retry++;
			System.out.println("setting retry to "+retry);
			Process.conversationTextArea.append("setting retry to "+retry);
			sendMessage(message);
			return retry;
		}
	}
	
	public void sendACK(DatagramPacket sender) throws IOException{
		String message = "ACK";
		byte[] ACKBUF = message.getBytes();
		DatagramPacket packet = new DatagramPacket(ACKBUF, ACKBUF.length, 
				 sender.getAddress(), sender.getPort());
		mySocket.send(packet);
	}
	
	public void onProcessClose() throws IOException, InterruptedException{
		System.out.println("Closing "+pid);
		String content;
		File input = new File("CrashProcess.txt");
		FileWriter fileWritter = new FileWriter(input.getName(),true);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(pid);
        bufferWritter.close();
		sendMessage("CRASH"+pid);
		mySocket.close();
		
	}
	
	public void requestRemovePort(int rpid) throws IOException, InterruptedException{
		data.removePort(rpid);
		ArrayList<Integer> allports = data.getAllPort();
		int myPort = data.getPort(pid);
		for(int i=0;i<allports.size();i++){
			if(allports.get(i)!=myPort){
				InetAddress receiverHost = InetAddress.getByName("localhost");
				//	System.out.println("message to be sent "+ message);
				String message = "RPORT"+rpid+pid; 
				byte[] sendBuffer = message.getBytes( );
				System.out.println(pid+"sending to "+allports.get(i));
				DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, 
					 receiverHost, allports.get(i)); 
				mySocket.send(packet);
			}
		}
	}
	
	public void sendInitMessage() throws IOException, InterruptedException{
		//sendMessage("INIT"+pid+data.getPort(pid));
		ArrayList<Integer> allports = data.getAllPort();
		int myPort = data.getPort(pid);
		for(int i=0;i<allports.size();i++){
			if(allports.get(i)!=myPort){
				InetAddress receiverHost = InetAddress.getByName("localhost");
				//	System.out.println("message to be sent "+ message);
				String message = "INIT"+pid+data.getPort(pid); 
				byte[] sendBuffer = message.getBytes( );
				System.out.println(pid+"sending to "+allports.get(i));
				DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, 
					 receiverHost, allports.get(i)); 
				mySocket.send(packet);
			}
		}
		//sendMessage("ELECT");
	}
	
	public void removePort(int rpid){
		data.removePort(rpid);
		data.printAllPorts();
	}
	
	public void addPort(int npid,int nport){
		data.addPort(npid, nport);
	}

}
