
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

import java.net.ServerSocket;

import java.net.Socket;
import java.net.SocketImpl;

import javax.swing.JButton;

import javax.swing.JFrame;

import javax.swing.JPanel;

import javax.swing.JScrollPane;

import javax.swing.JTextArea;

import javax.swing.JTextField;



public class ServerFrame extends JFrame {

	JTextArea textArea; 

	JTextField tfMsg;

	JButton btnSend;
	JButton btnExit;

	
	ServerSocket serverSocket;

	Socket socket;

	DataInputStream dis;

	DataOutputStream dos;

	MainFrame main;
	
	static int portnum;

	public ServerFrame() {		

		setTitle("Server");
		setBounds(450, 50, 500, 350);

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

		portnum=main.portNumber;

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
							
							dispose();

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

		

		//상대방이 접속할 수 있도록 서버소켓을 만들고 통신할 수 있는 준비 작업
		ServerThread serverThread = new ServerThread();
		serverThread.setDaemon(true); //메인 끝나면 같이 종료
		serverThread.start();

		

		addWindowListener(new WindowAdapter() {			

			@Override //클라이언트 프레임에 window(창) 관련 리스너 추가

			public void windowClosing(WindowEvent e) {				

				super.windowClosing(e);

				try {

					if(dos != null) dos.close();

					if(dis != null) dis.close();

					if(socket != null) socket.close();

					if(serverSocket != null) serverSocket.close();
					

				} catch (IOException e1) {					

					e1.printStackTrace();

				}

			}			

		});
		
		

	}	

	

	//서버소켓을 생성하고 클라이언트의 연결을 대기하고,
	//연결되면 메시지를 지속적으로 받는 역할 수행

	class ServerThread extends Thread {

		@Override

		public void run() {			

			try {  //서버 소켓 생성 작업

				//10001
				serverSocket = new ServerSocket(portnum);
				textArea.append("클라이언트의 접속을 기다립니다.\n");				
				socket = serverSocket.accept();//클라이언트가 접속할때까지 커서(스레드)가 대기
				textArea.append(socket.getInetAddress().getHostAddress() + "님이 접속하셨습니다.\n");

				

				//통신을 위한 스트림 생성

				dis = new DataInputStream(socket.getInputStream());

				dos = new DataOutputStream(socket.getOutputStream());

				

				while(true) {

					//상대방이 보내온 데이터를 읽기

					String msg = dis.readUTF();//상대방이 보낼때까지 대기

					textArea.append(" [CLIENT] : " + msg + "\n");

					textArea.setCaretPosition(textArea.getText().length());

				}				

				

			} catch (IOException e) {

				textArea.append("클라이언트가 나갔습니다.\n");

			}

		}

	}

	

	//메시지 전송하는 기능 메소드

	void sendMessage() {	

		String msg = tfMsg.getText(); 

		tfMsg.setText(""); //입력 후 빈칸으로

		textArea.append(" [SERVER] : " + msg + "\n");

		textArea.setCaretPosition(textArea.getText().length()); //스크롤 따라가게

		//상대방(Client)에게 메시지 전송하기

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

}

