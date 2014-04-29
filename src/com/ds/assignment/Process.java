package com.ds.assignment;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;

public class Process {

	private static JFrame frame;
	public static JTextArea conversationTextArea = new JTextArea();
	public static JLabel processNameLabel;
	public static JLabel processTypeLabel;
	public static JLabel processIconLabel;
	public static int ProcessID;
	private static SendReceiveThread bgThread;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Process window = new Process();
					window.frame.setVisible(true);
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void invokeProcess(final int id){
		System.out.println("Invoking process "+id);
		//ProcessID = id;
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					Process window = new Process();
					window.ProcessID = id;
					initialize();
					//System.out.println("Starting "+id);
					window.frame.setVisible(true);
					window.frame.getContentPane().setBackground(Color.WHITE);
					bgThread = new SendReceiveThread();
					//sample bgThread = new sample();
					Thread th = new Thread(bgThread);
					th.start();
					
					window.frame.addWindowListener(new WindowAdapter() {		//action on window close
						public void windowClosing(WindowEvent arg0) {
							//JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be green.");
							try{
								bgThread.onProcessClose();
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public Process(int id) {
		//initialize();
		ProcessID = id;
	}
	
	public Process() {
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private static void initialize() {
		System.out.println("process id "+ProcessID);
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 101, 414, 150);
		scrollPane.setViewportView(conversationTextArea);
		frame.getContentPane().add(scrollPane);
		
		/**
		conversationTextArea = new JTextArea();
		conversationTextArea.setColumns(20);
		conversationTextArea.setRows(5);
		conversationTextArea.setEditable(false);
		conversationTextArea.setLineWrap(true);
		**/
		
		processNameLabel = new JLabel("Process "+ProcessID);
		processNameLabel.setBounds(10, 11, 126, 14);
		frame.getContentPane().add(processNameLabel);
		
		processIconLabel = new JLabel("New label");
		processIconLabel.setIcon(new ImageIcon("C:\\Users\\Gowtham\\workspace\\RingElection\\process.jpg"));
		processIconLabel.setBounds(231, 14, 59, 53);
		frame.getContentPane().add(processIconLabel);
		
		processTypeLabel = new JLabel("Process\r\n");
		processTypeLabel.setBounds(305, 28, 119, 14);
		frame.getContentPane().add(processTypeLabel);
		
		JButton btnStartToken = new JButton("Start election");
		btnStartToken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					bgThread.sendMessage("ELECT");
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnStartToken.setBounds(10, 70, 150, 23);
		frame.getContentPane().add(btnStartToken);
	}
}
