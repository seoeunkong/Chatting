import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.KeyAdapter;

import java.awt.event.KeyEvent;

import java.awt.event.WindowAdapter;

import java.awt.event.WindowEvent;

import java.io.DataInputStream;

import java.io.DataOutputStream;

import java.io.IOException;

import java.io.InputStream;

import java.io.OutputStream;

import java.net.Socket;

import java.net.UnknownHostException;



import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.JTextArea;

import javax.swing.JTextField;



public class ClientFrame extends JFrame{

	JTextArea textArea; //멤버 참조변수

	JTextField tfMsg;
	
	
	JButton btnSend;
	JButton btnExit;
	
	MainFrame main;
	

	Socket socket;

	DataInputStream dis;

	DataOutputStream dos;	

	static String ipNumber;
	static int portNumber;

	public ClientFrame() {

		setTitle("Client");

		setBounds(450, 400, 500, 350);
		
		JPanel Panel = new JPanel();
		
		add(Panel,BorderLayout.NORTH);

		textArea = new JTextArea();		
		textArea.setEditable(false); //쓰기 금지

		JScrollPane scrollPane = new JScrollPane(textArea);

		add(scrollPane,BorderLayout.CENTER);

		JPanel msgPanel = new JPanel();
		
		msgPanel.setLayout(new BorderLayout());
		
		
		JPanel miniPanel=new JPanel();
		
		miniPanel.setLayout(new GridLayout(0, 2));

		
		tfMsg = new JTextField();

		btnSend = new JButton("Send");
		btnExit=new JButton("Exit");
		
		
		msgPanel.add(tfMsg, BorderLayout.CENTER);
		
		miniPanel.add(btnSend);
		miniPanel.add(btnExit);
		
		
		add(msgPanel,BorderLayout.SOUTH);
		msgPanel.add(miniPanel,BorderLayout.EAST);

		
		ipNumber=main.ipNumber;
		portNumber=main.portNumber;

		//send 버튼 클릭에 반응하는 리스너 추가
		btnSend.addActionListener(new ActionListener() {			

			@Override

			public void actionPerformed(ActionEvent e) {

				sendMessage();

			}

		});
		
		//exit 버튼 클릭에 반응하는 리스너 추가
		btnExit.addActionListener(new ActionListener() {			

			@Override

			public void actionPerformed(ActionEvent e) {
				
				try {

					if(dos != null) dos.close();

					if(dis != null) dis.close();

					if(socket != null) socket.close();
					
					System.exit(0);

				} catch (IOException e1) {					

					e1.printStackTrace();

				}
				
			}

		});
		
		
		//엔터키 눌렀을 때 반응하기

		tfMsg.addKeyListener(new KeyAdapter() {

			//키보드에서 키 하나를 눌렀을때 자동으로 실행되는 메소드..

			@Override

			public void keyPressed(KeyEvent e) {				

				super.keyPressed(e);

				

			//입력받은 키가 엔터인지 알아내기, KeyEvent 객체가 키에대한 정보 갖고있음

				int keyCode = e.getKeyCode();

				switch(keyCode) {

				case KeyEvent.VK_ENTER:

					sendMessage();

					break;

				}

			}

		});

		

		setVisible(true);

		tfMsg.requestFocus();

		

		//서버와 연결하는 네트워크 작업 
		ClientThread clientThread = new ClientThread();
		clientThread.setDaemon(true);
		clientThread.start();

		

		addWindowListener(new WindowAdapter() {			

			@Override //클라이언트 프레임에 window(창) 관련 리스너 추가

			public void windowClosing(WindowEvent e) {				

				super.windowClosing(e);

				try {

					if(dos != null) dos.close();

					if(dis != null) dis.close();

					if(socket != null) socket.close();

				} catch (IOException e1) {					

					e1.printStackTrace();

				}

			}			

		});

	}

	

	// 서버와 연결하는 네트워크 작업 스레드

	class ClientThread extends Thread {

		@Override

		public void run() {

			try {

				//내 컴퓨터 서버 주소:172.30.1.7, 포트번호: 10001
				socket = new Socket(ipNumber, portNumber);
				textArea.append("서버에 접속됐습니다.\n");

				//데이터 전송을 위한 스트림 생성(입추력 모두)
				InputStream is = socket.getInputStream();

				OutputStream os = socket.getOutputStream();

				dis = new DataInputStream(is);

				dos = new DataOutputStream(os);	

				

				while(true) {//상대방 메시지 받기

					String msg = dis.readUTF();

					textArea.append(" [SERVER] : " + msg + "\n");

					textArea.setCaretPosition(textArea.getText().length());

				}

			} catch (UnknownHostException e) {

				textArea.append("서버 주소가 이상합니다.\n");

			} catch (IOException e) {

				textArea.append("서버와 연결이 끊겼습니다.\n");

			}

		}

	}

	

	//메시지 전송하는 기능 메소드
		void sendMessage() {	

			String msg = tfMsg.getText(); 

			tfMsg.setText(""); //입력 후 빈칸으로

			textArea.append(" [CLIENT] : " + msg + "\n");

			textArea.setCaretPosition(textArea.getText().length());

			
			//상대방(Server)에게 메시지 전송하기
			//아웃풋 스트림을 통해 상대방에 데이터 전송
			Thread t = new Thread() {

				@Override

				public void run() {
					try { 

						dos.writeUTF(msg);
						dos.flush(); 			

					} catch (IOException e) {

						e.printStackTrace();

					}

				}

			};

			t.start();			

		}

}//class
