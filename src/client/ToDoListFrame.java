package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.JTextComponent;


public class ToDoListFrame extends JFrame{
	MyTimer timer=MyTimer.mytimer;
	ImageIcon saveIcon=new ImageIcon("./src/icon/save.png");
	ImageIcon delIcon=new ImageIcon("./src/icon/del.png");
	Box verticalBox=Box.createVerticalBox();
	Box titleBox =Box.createHorizontalBox();

	MyButton saveButton=new MyButton("����",saveIcon);
	MyButton delButton=new MyButton("ɾ��",delIcon);

	JTextField nameTextField=new JTextField(30);
	JTextField addressTextField=new JTextField(30);
	JTextField beginTextField=new JTextField(10);
	JTextField endTextField=new JTextField(10);

	ArrayList<String> timeSetItems=new ArrayList<String>();
	JComboBox<String>timeStartComboBox;
	JComboBox<String>timeFinishComboBox;
	JCheckBox isAllDayBox=new JCheckBox("ȫ��");

	JTextArea detailTextArea=new JTextArea();
	JScrollPane scrollPane=new JScrollPane(detailTextArea);
	String[] comboItems= {"��","׼ʱ","��ǰ5����","��ǰ10����","��ǰ15����","��ǰ30����","��ǰ1Сʱ","��ǰ2Сʱ","��ǰ5Сʱ","��ǰ12Сʱ","��ǰ1��"};
	int[] earlyTime= {-1,0,5,10,15,30,60,120,300,720,1440};//��Ӧ����ǰʱ�䣨���ӣ�

	JComboBox<String> alarmComboBox=new JComboBox<String>(comboItems);

	Color titlebarColor=new Color(232,232,232);
	JLabel errorLabel=new JLabel("���ڸ�ʽ����");
	MonthView monthView;

	int memoID;
	//id(-1��ʾ����)
	public ToDoListFrame(String titleString,MonthView mv,int id) {
		monthView=mv;
		memoID=id;
		//����ʱ���б�
		for(int h=0;h<24;h++) {
			for(int m=0;m<60;m+=30) {
				timeSetItems.add(String.format("%02d:%02d",h,m));
			}
		}

		this.setTitle(titleString);
		this.add(verticalBox);
		verticalBox.setBackground(Color.white);
		verticalBox.setOpaque(true);
		//verticalBox.add(Box.createHorizontalStrut(300)); 

		verticalBox.add(titleBox);


		//������
		saveButton.setPreferredSize(new Dimension(80, 40));
		saveButton.setColor(titlebarColor);
		delButton.setPreferredSize(new Dimension(80, 40));
		delButton.setColor(titlebarColor);

		JLabel imgLabel=new JLabel(new ImageIcon("./src/icon/alarm.png"));
		JLabel remindLabel=new JLabel("���ѣ�");
		remindLabel.setFont(new Font("΢���ź�", 0, 15));
		alarmComboBox.setMaximumSize(alarmComboBox.getPreferredSize());
		alarmComboBox.setFont(new Font("΢���ź�", 0, 12));

		//���水ť
		titleBox.add(saveButton);
		saveButton.addActionListener(new ButtonActionListener());
		//ɾ����ť
		titleBox.add(delButton);
		delButton.addActionListener(new ButtonActionListener());

		titleBox.add(Box.createRigidArea(new Dimension(15, 5)));
		titleBox.add(imgLabel);
		titleBox.add(remindLabel);
		titleBox.add(alarmComboBox);

		titleBox.setOpaque(true);
		titleBox.setBackground(titlebarColor);
		titleBox.add(Box.createHorizontalGlue());
		//titleBox.add(Box.createVerticalStrut(20));

		//�����
		//����
		Box nameEditBox=Box.createHorizontalBox();
		nameEditBox.add(Box.createHorizontalStrut(10)); 
		JLabel nameLabel=new JLabel("����");
		nameLabel.setFont(new Font("΢���ź�", Font.BOLD, 18));
		nameEditBox.add(nameLabel);
		nameEditBox.add(Box.createHorizontalStrut(5)); 
		nameEditBox.add(nameTextField);
		nameEditBox.add(Box.createVerticalStrut(30)); 
		nameTextField.setFont(new Font("΢���ź�", 0, 15));
		nameTextField.setMaximumSize(nameTextField.getPreferredSize());
		nameTextField.addFocusListener(
				new JTextFieldHintListener(nameTextField, "�������¼�����"));

		//�ص�
		Box addressEditBox=Box.createHorizontalBox();
		addressEditBox.add(Box.createHorizontalStrut(10)); 
		JLabel addressLabel=new JLabel("�ص�");
		addressLabel.setFont(new Font("΢���ź�", Font.BOLD, 18));
		addressEditBox.add(addressLabel);
		addressEditBox.add(Box.createHorizontalStrut(5)); 
		addressEditBox.add(addressTextField);
		addressEditBox.add(Box.createVerticalStrut(30)); 
		addressTextField.setFont(new Font("΢���ź�", 0, 15));
		addressTextField.setMaximumSize(addressTextField.getPreferredSize());
		addressTextField.addFocusListener(
				new JTextFieldHintListener(addressTextField, "�������ַ"));

		//��ʼʱ��
		Box beginDateBox=Box.createHorizontalBox();
		beginDateBox.add(Box.createHorizontalStrut(10)); 
		JLabel beginLabel=new JLabel("��ʼ");
		beginLabel.setFont(new Font("΢���ź�", Font.BOLD, 18));
		beginDateBox.add(beginLabel);
		beginDateBox.add(Box.createHorizontalStrut(5)); 
		beginDateBox.add(beginTextField);
		beginDateBox.add(Box.createVerticalStrut(30)); 
		beginTextField.setFont(new Font("΢���ź�", 0, 15));
		beginTextField.setMaximumSize(beginTextField.getPreferredSize());
		beginTextField.addFocusListener(
				new JTextFieldHintListener(beginTextField, this.getTitle()));

		timeStartComboBox =new JComboBox<String>(timeSetItems.toArray(new String[0]));
		timeStartComboBox.setMaximumSize(timeStartComboBox.getPreferredSize());
		timeStartComboBox.setFont(new Font("΢���ź�", 0, 14));
		timeStartComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timeFinishComboBox.setSelectedIndex((timeStartComboBox.getSelectedIndex()+1)%48);

			}
		});

		errorLabel.setMaximumSize(errorLabel.getPreferredSize());

		//������ʾ
		errorLabel.setForeground(Color.RED);
		beginDateBox.add(errorLabel);
		errorLabel.setVisible(false);

		//beginDateBox.add(Box.createHorizontalStrut(10)); 
		//ȫ�츴ѡ��
		isAllDayBox.setOpaque(false);
		isAllDayBox.setFocusPainted(false);
		isAllDayBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox source=(JCheckBox)e.getSource();
				if(source.isSelected()) {
					endTextField.setEnabled(false);
					timeStartComboBox.setEnabled(false);
					timeFinishComboBox.setEnabled(false);
				}
				else {
					endTextField.setEnabled(true);
					timeStartComboBox.setEnabled(true);
					timeFinishComboBox.setEnabled(true);
				}
			}
		});

		beginDateBox.add(timeStartComboBox);
		beginDateBox.add(isAllDayBox);
		beginDateBox.add(Box.createHorizontalStrut(80)); 

		//����ʱ��
		Box endDateBox=Box.createHorizontalBox();
		endDateBox.add(Box.createHorizontalStrut(10)); 
		JLabel endLabel=new JLabel("����");
		endLabel.setFont(new Font("΢���ź�", Font.BOLD, 18));
		endDateBox.add(endLabel);
		endDateBox.add(Box.createHorizontalStrut(5)); 
		endDateBox.add(endTextField);
		endDateBox.add(Box.createVerticalStrut(30)); 
		endTextField.setFont(new Font("΢���ź�", 0, 15));
		endTextField.setMaximumSize(endTextField.getPreferredSize());
		endTextField.addFocusListener(
				new JTextFieldHintListener(endTextField, this.getTitle()));

		timeFinishComboBox =new JComboBox<String>(timeSetItems.toArray(new String[0]));
		timeFinishComboBox.setMaximumSize(timeFinishComboBox.getPreferredSize());
		timeFinishComboBox.setFont(new Font("΢���ź�", 0, 14));
		timeFinishComboBox.setSelectedIndex(1);
		endDateBox.add(timeFinishComboBox);
		endDateBox.add(Box.createRigidArea(isAllDayBox.getMaximumSize()));
		endDateBox.add(Box.createHorizontalStrut(80)); 

		//�¼�����
		detailTextArea.setLineWrap(true);
		detailTextArea.setFont(new Font("΢���ź�", 0, 15));
		detailTextArea.addFocusListener(
				new JTextFieldHintListener(detailTextArea, "�¼�����"));

		scrollPane.setPreferredSize(new Dimension(200,300));

		verticalBox.add(nameEditBox);
		verticalBox.add(addressEditBox);
		verticalBox.add(beginDateBox);
		verticalBox.add(endDateBox);
		verticalBox.add(scrollPane);

		//verticalBox.add(Box.createVerticalGlue());
		verticalBox.add(Box.createVerticalGlue());
		//panel.add(Box.createVerticalGlue());
		//nameEditPanel.setAlignmentX( Component.LEFT_ALIGNMENT);
		//��������Ļ��λ��
		this.setLocation(150,70);
		//�����С
		this.setSize(500,600);
		//��ʾ����
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		if(memoID!=-1) {
			loadRecord(memoID);
		}
	}

	void loadRecord(int id) {
		//Map<String, String> record=CalendarClient.Obj.getByID(id);
		Map<String, String> record=LocalStorage.Obj.getByID(id);
		nameTextField.setText(record.get("title"));
		addressTextField.setText(record.get("address"));
		detailTextArea.setText(record.get("detail"));
		long startTimestamp=Long.valueOf(record.get("startTime"));
		long endTimestamp=Long.valueOf(record.get("endTime"));
		long remindTimestamp=Long.valueOf(record.get("remindTime"));

		//��������
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		beginTextField.setText(dateFormat.format(startTimestamp));
		endTextField.setText(dateFormat.format(endTimestamp));

		//����ʱ��
		int startIndex=timeSetItems.indexOf(timeFormat.format(startTimestamp));
		timeStartComboBox.setSelectedIndex(startIndex);
		int endIndex=timeSetItems.indexOf(timeFormat.format(endTimestamp));
		timeFinishComboBox.setSelectedIndex(endIndex);

		//��������
		if(record.get("alarm").contentEquals("1")) {
			long remindMinutes=(startTimestamp-remindTimestamp)/1000/60;
			for(int i=0;i<earlyTime.length;i++) {//ƥ��ѡ��
				if(remindMinutes==earlyTime[i]) {
					alarmComboBox.setSelectedIndex(i);
				}
			}
		}
	}
	class ButtonActionListener implements ActionListener{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			//���水ť
			if(e.getActionCommand().equals("����")) {
				//�ȼ�����ںϷ���
				String startDateStr=beginTextField.getText();
				String endDateStr=endTextField.getText();
				Date startDate;
				Date endDate;
				try {
					startDate= sdf.parse(startDateStr);
					endDate=sdf.parse(endDateStr);
				} catch (ParseException e1) {
					e1.printStackTrace();
					errorLabel.setText("���ڸ�ʽ����");
					errorLabel.setVisible(true);
					return;
				}

				String title=nameTextField.getText();
				//����Ϊ����ʾ
				if(title.equals("�������¼�����")) {
					nameTextField.setForeground(Color.red);
					return;
				}

				String address=addressTextField.getText();
				if(address.equals("�������ַ")) {
					address="";
				}
				String detail=detailTextArea.getText();	
				if(detail.equals("�¼�����")) {
					detail="";
				}
				boolean isAllDay=isAllDayBox.isSelected();
				int startTimeindex=timeStartComboBox.getSelectedIndex();
				int endTimeindex=timeFinishComboBox.getSelectedIndex();

				int remindTime=earlyTime[alarmComboBox.getSelectedIndex()];

				//����ʱ��������룩
				long startTimeStamp=startDate.getTime()+startTimeindex*30*60*1000;
				long endTimeStamp=endDate.getTime()+endTimeindex*30*60*1000;
				if(!isAllDay&&startTimeStamp>endTimeStamp) {
					errorLabel.setText("����ʱ������ڿ�ʼʱ��");
					errorLabel.setVisible(true);
					return;
				}
				if(isAllDay) {//ȫ���¼�
					startTimeStamp=startDate.getTime();
					endTimeStamp=startTimeStamp+24*60*60*1000-1;
				}
				long remindTimeStamp=startTimeStamp-remindTime*60*1000;

				//������
				if(remindTime==-1) {
					remindTimeStamp=startTimeStamp;
				}
				
				Map<String,String> newRecord=new HashMap<String, String>();
				newRecord.put("title",title);
				newRecord.put("address",address);
				newRecord.put("detail",detail);
				newRecord.put("startTime",String.valueOf(startTimeStamp));
				newRecord.put("endTime",String.valueOf(endTimeStamp));
				newRecord.put("remindTime",String.valueOf(remindTimeStamp));
				newRecord.put("alarm",String.valueOf(remindTime==-1?"0":"1"));
				newRecord.put("editTime",String.valueOf(System.currentTimeMillis()));//�޸�ʱ��
				
				//д�����ݿ�
				if(memoID==-1) {//����
					//����д��
					int result=LocalStorage.Obj.addRecord(newRecord);
					//���ݿ�д��
					try {
						CalendarClient.Obj.addRecord(newRecord);
					}catch (Exception e1) {
						System.out.println("Connect To Server Failed!");
					}
					if(result!=-1) {
						memoID=result;
						//������ʱ��
						if(remindTime!=-1) {
							timer.addTimer(memoID, remindTimeStamp, title, address, startTimeStamp, endTimeStamp);
						}
					} else{
						memoID=-1;
						System.out.println("������¼ʧ��");
					}
				}else {//�޸�
					timer.cancelTimer(memoID);
					newRecord.put("memoID",String.valueOf(memoID));
					//����д��
					LocalStorage.Obj.updateRecord(memoID, newRecord);
					//���ݿ�д��
					try{
						CalendarClient.Obj.updateRecord(newRecord);
					}catch (Exception e2) {
						System.out.println("Connect To Server Failed!");
					}
					if(remindTime!=-1) {
						timer.addTimer(memoID, remindTimeStamp, title, address, startTimeStamp, endTimeStamp);
					}
				}
				//ˢ�½���
				monthView.setCalendar(monthView.calender);
				LeftPanel.leftPanel.setTaskList();
				dispose();
			}
			
			//ɾ����ť
			else if (e.getActionCommand().equals("ɾ��")) {
				timer.cancelTimer(memoID);
				//����ɾ��
				LocalStorage.Obj.deleteRecord(memoID);
				//���ݿ�ɾ��
				try {
					CalendarClient.Obj.deleteRecord(memoID);
				}catch (Exception e2) {
					System.out.println("Connect To Server Failed!");
				}
				//ˢ�½���
				monthView.setCalendar(monthView.calender);
				LeftPanel.leftPanel.setTaskList();
				dispose();
			}	
		}
	}


	class JTextFieldHintListener implements FocusListener {
		private String hintText;
		private JTextComponent textField;
		public JTextFieldHintListener(JTextComponent jTextField,String hintText) {
			this.textField = jTextField;
			this.hintText = hintText;
			jTextField.setText(hintText);  //Ĭ��ֱ����ʾ
			jTextField.setForeground(Color.GRAY);
		}

		@Override
		public void focusGained(FocusEvent e) {
			errorLabel.setVisible(false);
			//��ȡ����ʱ�������ʾ����
			String temp = textField.getText();
			if(temp.equals(hintText)) {
				textField.setText("");
				textField.setForeground(Color.BLACK);
			}
		}

		@Override
		public void focusLost(FocusEvent e) {	
			//ʧȥ����ʱ��û���������ݣ���ʾ��ʾ����
			String temp = textField.getText();
			if(temp.equals("")) {
				textField.setForeground(Color.GRAY);
				textField.setText(hintText);
			}	
		} 
	}
}



