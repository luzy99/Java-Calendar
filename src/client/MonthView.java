package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.*;
import java.util.*;

public class MonthView extends JPanel implements ActionListener{
	TaskListPanel[] dayPanels =new TaskListPanel[35];
	private JLabel[] dayTitle =new JLabel[7];
	private String[] DAYS="Sun Mon Tue Wed Thu Fri Sat".split(" ");
	//final private String[] DAYS="�� һ �� �� �� �� ��".split(" ");

	Calendar calender=Calendar.getInstance();//��ǰҳ��
	Calendar today=Calendar.getInstance();//���������
	private int date;
	private int month;//0-11
	private int year;
	private int dayOfMonth;
	private int dayOfLastMonth;
	int startDay;//��ǰ�µĿ�ʼ����
	
	int border=3;

	public MonthView() {
		this.setLayout(new BorderLayout());//�ܲ���
		this.setOpaque(false);
		JPanel titlePanel=new JPanel(new GridLayout(1,7,5,5));//������
		//titlePanel.setOpaque(false);
		titlePanel.setBackground(new Color(52,152,219));
		for(int i=0;i<7;i++){
			dayTitle[i]=new JLabel(DAYS[i],JLabel.CENTER);
			dayTitle[i].setForeground(Color.white);
			dayTitle[i].setFont(new Font("΢���ź�", Font.BOLD, 13));
			titlePanel.add(dayTitle[i]);
		}

		//��������
		JPanel datePanel=new JPanel(new GridLayout(5,7,border,border));//5��7�У���϶Ϊ5
		for(int i=0;i<35;i++){
			dayPanels[i]=new TaskListPanel(this);
			datePanel.add(dayPanels[i]);
		}
		datePanel.setBackground(new Color(210,210,243));
		//���ò���
		this.add(titlePanel,BorderLayout.NORTH);
		this.add(datePanel,BorderLayout.CENTER);

		setCalendar(today);
		
		this.setSize(240,320);
		setVisible(true);
		//	System.out.println(instance.get(Calendar.MONTH));
	}

	public void setCalendar(Calendar cal) {
		setVisible(false);
		
		date=cal.get(Calendar.DATE);
		month=cal.get(Calendar.MONTH);
		year=cal.get(Calendar.YEAR);
		//��ʼ������
		calender=(Calendar)cal.clone();

		calender.set(Calendar.DATE,1);
		startDay=calender.get(Calendar.DAY_OF_WEEK);//��ǰ�µĿ�ʼ����
		
		calender.roll(Calendar.DATE,-1);
		dayOfMonth=calender.get(Calendar.DAY_OF_MONTH);//��ǰ�µ�����

		//��ȡ�ϸ��µ�����
		calender.roll(Calendar.MONTH, -1);
		calender.set(Calendar.DATE,1);
		calender.roll(Calendar.DATE,-1);
		dayOfLastMonth=calender.get(Calendar.DAY_OF_MONTH);//�ϸ��µ�����

		//����ϸ��µ�����
		for(int i=0;i<startDay-1;i++) {
			dayPanels[startDay-i-2].setDateList(year,month+1,dayOfLastMonth-i);
			dayPanels[startDay-i-2].setEnabled(false);
			String toolTip=
					String.format("%d-%d-%d", month==0?year-1:year,(month-1+12)%12+1,dayOfLastMonth-i);
			dayPanels[startDay-i-2].setToolTipText(toolTip);
		}
		//�������µ�����
		for(int i=1;i<=dayOfMonth;i++) {
			dayPanels[(startDay+i-2)%35].setDateList(year,month+1,i);
			String toolTip=String.format("%d-%d-%d", year,month+1,i);
			dayPanels[(startDay+i-2)%35].setToolTipText(toolTip);
			dayPanels[(startDay+i-2)%35].setEnabled(true);
		}
		//����¸��µ�����
		for(int i=1;i<35-startDay-dayOfMonth+2;i++) {
			dayPanels[i+startDay+dayOfMonth-2].setDateList(year,month+1,i);
			dayPanels[i+startDay+dayOfMonth-2].setEnabled(false);
			String toolTip=
					String.format("%d-%d-%d", month==11?year+1:year,(month+1)%12+1,i);
			dayPanels[i+startDay+dayOfMonth-2].setToolTipText(toolTip);
		}
		calender=(Calendar)cal.clone();
		
		if(today.get(Calendar.YEAR)==year&&today.get(Calendar.MONTH)==month) {
			dayPanels[startDay+date-2].setBackground(new Color(161,170,212));
		}
		
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="<-"){
			calender.add(Calendar.MONTH, -1);
			setCalendar(calender);
		}
		else if (e.getActionCommand()=="->") {
			calender.add(Calendar.MONTH, +1);
			setCalendar(calender);
		}
		else {
			setCalendar(today);
		}
	}
}
