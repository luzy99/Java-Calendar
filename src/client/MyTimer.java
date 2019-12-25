package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MyTimer{
	//memoID,Timer
	TreeMap<Integer, Timer> timers=new TreeMap<Integer, Timer>();
	static MyTimer mytimer=new MyTimer();
	Timer timer;
	
	Calendar cal=Calendar.getInstance();
	public MyTimer() {

	}
	void addTimer(
			int id,long remindTimestamp,
			String title,String address,long stime,long etime){
		Date date=new Date(remindTimestamp);
		timer = new Timer();
        timer.schedule(new AlarmTask(title,address,stime,etime), date);
        timers.put(id, timer);
	}
	void cancelTimer(int id) {
		try {
			if(timers.containsKey(id)) {
				timers.get(id).cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

class AlarmTask extends TimerTask{
	Calendar currentCal=Calendar.getInstance();
	int memoId;
	String title;
	String address;
	long starttime;
	long endtime;
	
	JLabel titleLabel;
	JLabel timeLabel;
	JLabel addrLabel;
	JButton okButton=new JButton(" ֪����  ");
	
    public AlarmTask(String title,String address,long stime,long etime) {
    	this.title=title;
    	this.address=address;
    	if(address.isBlank()) {
    		this.address="��";
    	}
    	this.starttime=stime;
    	this.endtime=etime;
    }
    
    @Override
    public void run() {
    	JFrame alarmFrame=new JFrame("�ճ�����");
    	alarmFrame.setUndecorated(true);
    	alarmFrame.setSize(400, 300);
    	alarmFrame.setLocation(400,200);
    	alarmFrame.setOpacity(0.9f);
    	alarmFrame.setBackground(new Color(200,200,230,230));
    	alarmFrame.setAlwaysOnTop(true);
    	
    	Box verticalBox=Box.createVerticalBox();    //��������Box����
    	alarmFrame.add(verticalBox);
    	verticalBox.add(Box.createVerticalStrut(10));
    	
    	JLabel tLabel=new JLabel("�ճ����ѣ�");
    	tLabel.setFont(new Font("΢���ź�", Font.BOLD, 28));
    	verticalBox.add(tLabel);
    	titleLabel=new JLabel(title);
    	titleLabel.setFont(new Font("΢���ź�", Font.BOLD, 28));
    	verticalBox.add(titleLabel);
    	verticalBox.add(Box.createVerticalStrut(10));
    	
    	SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:mm");
    	String startString =sdf.format(starttime);
    	String endString =sdf.format(endtime);
    	timeLabel=new JLabel(startString+" -- "+endString,JLabel.CENTER);
    	timeLabel.setFont(new Font("΢���ź�", 0, 18));
    	verticalBox.add(timeLabel);
    	verticalBox.add(Box.createVerticalStrut(10));
    	
    	addrLabel=new JLabel("�ص㣺"+address,JLabel.CENTER);
    	addrLabel.setFont(new Font("΢���ź�", 0, 18));
    	verticalBox.add(addrLabel);
    	verticalBox.add(Box.createVerticalGlue());
    	
    	okButton.setFocusPainted(false);
    	okButton.setPreferredSize(new Dimension(30,30));
    	okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				alarmFrame.dispose();
			}
		});
    	
    	verticalBox.add(Box.createRigidArea(new Dimension(80,0)));
    	verticalBox.add(okButton);
    	verticalBox.add(Box.createVerticalStrut(10));
    	alarmFrame.setVisible(true);
        System.out.println(title);
        System.out.println(address);
    }
}
