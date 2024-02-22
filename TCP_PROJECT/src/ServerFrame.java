
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
		
		textArea.setEditable(false); //���� ����
		
		
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

		//send ��ư Ŭ���� �����ϴ� ������ �߰�
		btnSend.addActionListener(new ActionListener() {			

			@Override

			public void actionPerformed(ActionEvent e) {

				sendMessage();

			}

		});
		
		//exit ��ư Ŭ���� �����ϴ� ������ �߰�
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
				

		
		//����Ű ������ �� �����ϱ�
		tfMsg.addKeyListener(new KeyAdapter() {

			//Ű���忡�� Ű �ϳ��� �������� �ڵ����� ����Ǵ� �޼ҵ�..
			@Override
			public void keyPressed(KeyEvent e) {				
				super.keyPressed(e);

			//�Է¹��� Ű�� �������� �˾Ƴ���, KeyEvent ��ü�� Ű������ ���� ��������
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

		

		//������ ������ �� �ֵ��� ���������� ����� ����� �� �ִ� �غ� �۾�
		ServerThread serverThread = new ServerThread();
		serverThread.setDaemon(true); //���� ������ ���� ����
		serverThread.start();

		

		addWindowListener(new WindowAdapter() {			

			@Override //Ŭ���̾�Ʈ �����ӿ� window(â) ���� ������ �߰�

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

	

	//���������� �����ϰ� Ŭ���̾�Ʈ�� ������ ����ϰ�,
	//����Ǹ� �޽����� ���������� �޴� ���� ����

	class ServerThread extends Thread {

		@Override

		public void run() {			

			try {  //���� ���� ���� �۾�

				//10001
				serverSocket = new ServerSocket(portnum);
				textArea.append("Ŭ���̾�Ʈ�� ������ ��ٸ��ϴ�.\n");				
				socket = serverSocket.accept();//Ŭ���̾�Ʈ�� �����Ҷ����� Ŀ��(������)�� ���
				textArea.append(socket.getInetAddress().getHostAddress() + "���� �����ϼ̽��ϴ�.\n");

				

				//����� ���� ��Ʈ�� ����

				dis = new DataInputStream(socket.getInputStream());

				dos = new DataOutputStream(socket.getOutputStream());

				

				while(true) {

					//������ ������ �����͸� �б�

					String msg = dis.readUTF();//������ ���������� ���

					textArea.append(" [CLIENT] : " + msg + "\n");

					textArea.setCaretPosition(textArea.getText().length());

				}				

				

			} catch (IOException e) {

				textArea.append("Ŭ���̾�Ʈ�� �������ϴ�.\n");

			}

		}

	}

	

	//�޽��� �����ϴ� ��� �޼ҵ�

	void sendMessage() {	

		String msg = tfMsg.getText(); 

		tfMsg.setText(""); //�Է� �� ��ĭ����

		textArea.append(" [SERVER] : " + msg + "\n");

		textArea.setCaretPosition(textArea.getText().length()); //��ũ�� ���󰡰�

		//����(Client)���� �޽��� �����ϱ�

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

