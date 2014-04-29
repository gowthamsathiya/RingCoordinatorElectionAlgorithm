package com.ds.assignment;

import java.util.ArrayList;
import java.util.Arrays;

public class CommunicationData {

	public static int[] processID = {0,1,2,3,4,5};
	
	//public static int[] port = {15000,15010,15020,15030,15040,15050};

	//public static ArrayList<Integer> port = new ArrayList(Arrays.asList(15000,15010,15020,15030,15040,15050));
	public static String host = "localhost"; 
	
	/**
	public static int getNextPort(int id){
		if(id >= 5)
			return port[0];
		else
			return port[id+1];
	}
	**/
	public ArrayList<Integer> port = new ArrayList<Integer>();
	
	CommunicationData(){
		port.add(15000);
		port.add(15010);
		port.add(15020);
		port.add(15030);
		port.add(15040);
		port.add(15050);
	}
	
	public void removePort(int pid){
		port.remove(pid);
		port.add(pid, 0);
	}
	
	public void addPort(int pid,int portid){
		port.remove(pid);
		port.add(pid, portid);
	}
	
	public int getNextPort(int id){
		if(id >= 5)
			return nextAvailablePort(0);
		else
			return nextAvailablePort(id+1);
	}
	
	public int nextAvailablePort(int id){
		for(int i=id;i<id+port.size();i++){
			if(port.get(i%6)!=0){ 
				System.out.println("Scanning port "+i%6);
				return port.get(i%6);
			}
		}
		return 1234;
	}
	
	public int getPort(int id){
		return port.get(id);
	}
	
	public ArrayList<Integer> getAllPort(){
		return port;
	}
	
	public void printAllPorts(){
		System.out.println(port.toString());
	}
}
