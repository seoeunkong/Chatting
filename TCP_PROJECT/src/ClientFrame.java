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

	JTextArea textArea; //��� ��������

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

		
		ipNumber=main.ipNumber;
		portNumber=main.portNumber;

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
					
					System.exit(0);

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

		

		//������ �����ϴ� ��Ʈ��ũ �۾� 
		ClientThread clientThread = new ClientThread();
		clientThread.setDaemon(true);
		clientThread.start();

		

		addWindowListener(new WindowAdapter() {			

			@Override //Ŭ���̾�Ʈ �����ӿ� window(â) ���� ������ �߰�

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

	

	// ������ �����ϴ� ��Ʈ��ũ �۾� ������

	class ClientThread extends Thread {

		@Override

		public void run() {

			try {

				//�� ��ǻ�� ���� �ּ�:172.30.1.7, ��Ʈ��ȣ: 10001
				socket = new Socket(ipNumber, portNumber);
				textArea.append("������ ���ӵƽ��ϴ�.\n");

				//������ ������ ���� ��Ʈ�� ����(���߷� ���)
				InputStream is = socket.getInputStream();

				OutputStream os = socket.getOutputStream();

				dis = new DataInputStream(is);

				dos = new DataOutputStream(os);	

				

				while(true) {//���� �޽��� �ޱ�

					String msg = dis.readUTF();

					textArea.append(" [SERVER] : " + msg + "\n");

					textArea.setCaretPosition(textArea.getText().length());

				}

			} catch (UnknownHostException e) {

				textArea.append("���� �ּҰ� �̻��մϴ�.\n");

			} catch (IOException e) {

				textArea.append("������ ������ ������ϴ�.\n");

			}

		}

	}

	

	//�޽��� �����ϴ� ��� �޼ҵ�
		void sendMessage() {	

			String msg = tfMsg.getText(); 

			tfMsg.setText(""); //�Է� �� ��ĭ����

			textArea.append(" [CLIENT] : " + msg + "\n");

			textArea.setCaretPosition(textArea.getText().length());

			
			//����(Server)���� �޽��� �����ϱ�
			//�ƿ�ǲ ��Ʈ���� ���� ���濡 ������ ����
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
