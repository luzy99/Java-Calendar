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
	//final private String[] DAYS="日 一 二 三 四 五 六".split(" ");

	Calendar calender=Calendar.getInstance();//当前页面
	Calendar today=Calendar.getInstance();//今天的日期
	private int date;
	private int month;//0-11
	private int year;
	private int dayOfMonth;
	private int dayOfLastMonth;
	int startDay;//当前月的开始星期
	
	int border=3;

	public MonthView() {
		this.setLayout(new BorderLayout());//总布局
		this.setOpaque(false);
		JPanel titlePanel=new JPanel(new GridLayout(1,7,5,5));//标题栏
		//titlePanel.setOpaque(false);
		titlePanel.setBackground(new Color(52,152,219));
		for(int i=0;i<7;i++){
			dayTitle[i]=new JLabel(DAYS[i],JLabel.CENTER);
			dayTitle[i].setForeground(Color.white);
			dayTitle[i].setFont(new Font("微软雅黑", Font.BOLD, 13));
			titlePanel.add(dayTitle[i]);
		}

		//日期网格
		JPanel datePanel=new JPanel(new GridLayout(5,7,border,border));//5行7列，间隙为5
		for(int i=0;i<35;i++){
			dayPanels[i]=new TaskListPanel(this);
			datePanel.add(dayPanels[i]);
		}
		datePanel.setBackground(new Color(210,210,243));
		//设置布局
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
		//初始化日历
		calender=(Calendar)cal.clone();

		calender.set(Calendar.DATE,1);
		startDay=calender.get(Calendar.DAY_OF_WEEK);//当前月的开始星期
		
		calender.roll(Calendar.DATE,-1);
		dayOfMonth=calender.get(Calendar.DAY_OF_MONTH);//当前月的天数

		//获取上个月的天数
		calender.roll(Calendar.MONTH, -1);
		calender.set(Calendar.DATE,1);
		calender.roll(Calendar.DATE,-1);
		dayOfLastMonth=calender.get(Calendar.DAY_OF_MONTH);//上个月的天数

		//填充上个月的日期
		for(int i=0;i<startDay-1;i++) {
			dayPanels[startDay-i-2].setDateList(year,month+1,dayOfLastMonth-i);
			dayPanels[startDay-i-2].setEnabled(false);
			String toolTip=
					String.format("%d-%d-%d", month==0?year-1:year,(month-1+12)%12+1,dayOfLastMonth-i);
			dayPanels[startDay-i-2].setToolTipText(toolTip);
		}
		//填充这个月的日期
		for(int i=1;i<=dayOfMonth;i++) {
			dayPanels[(startDay+i-2)%35].setDateList(year,month+1,i);
			String toolTip=String.format("%d-%d-%d", year,month+1,i);
			dayPanels[(startDay+i-2)%35].setToolTipText(toolTip);
			dayPanels[(startDay+i-2)%35].setEnabled(true);
		}
		//填充下个月的日期
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
