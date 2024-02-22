
import java.awt.BorderLayout;

import java.awt.GridLayout;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;



import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class MainFrame extends JFrame {
	
	JTextField ipNum;
	JTextField port;
	JLabel ipLabel;
	JLabel portLabel;
	
	JButton btnUpdate;
	
	static String ipNumber;
	static int portNumber;
	

	public MainFrame() {

		

		setTitle("Chatting Project");

		setBounds(10, 50, 400, 250);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel fPanel = new JPanel();
		
		ipNum = new JTextField(10);
		port= new JTextField(10);
		btnUpdate = new JButton("Update");
		ipLabel=new JLabel("IP: ");
		portLabel=new JLabel("PORT: ");
		
		fPanel.add(ipLabel, BorderLayout.WEST);
		fPanel.add(ipNum, BorderLayout.WEST);
		fPanel.add(portLabel, BorderLayout.CENTER);
		fPanel.add(port, BorderLayout.CENTER);
		fPanel.add(btnUpdate, BorderLayout.EAST);
		
		add(fPanel,BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		JButton btnServer = new JButton("Server");
		JButton btnClient = new JButton("Client");
		
		add(panel,BorderLayout.SOUTH);
		
		//update��ư ������ ������ ��Ȱ��ȭ
		btnClient.setEnabled(false);
		btnServer.setEnabled(false);
		

		//��ưŬ�� �׼ǿ� �����ϱ� ���� ������ ��ü ���� �� �߰�

		btnServer.addActionListener(new ActionListener() {			

			@Override

			public void actionPerformed(ActionEvent e) {

				//ServerFrame ��ü ����

				ServerFrame frame = new ServerFrame();

			}

		});

		btnClient.addActionListener(new ActionListener() {			

			@Override

			public void actionPerformed(ActionEvent e) {

				//ClientFrame ��ü ����

				ClientFrame frame = new ClientFrame();

			}

		});
		
		//update ��ư Ŭ���� �����ϴ� ������ �߰�
			btnUpdate.addActionListener(new ActionListener() {			

					public void actionPerformed(ActionEvent e) {
						System.out.println(port.getText());
						
						//ip, port������ �������� �ʾ��� ���
						if(ipNum.getText().isEmpty()||port.getText().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Enter all", "Please fill all the blanks", JOptionPane.ERROR_MESSAGE);
						}
						else
							ipNumber=ipNum.getText();
							portNumber=Integer.parseInt(port.getText());
							ipNum.setText(null);
							port.setText(null);

							//��ư Ȱ��ȭ
							btnClient.setEnabled(true);
							btnServer.setEnabled(true);

					}

				});


		

		panel.add(btnServer);

		panel.add(btnClient);

		

		add(panel, BorderLayout.CENTER);

		

		setVisible(true);

	}

	public static void main(String[] args) {

		new MainFrame();
		

	}

	

}
