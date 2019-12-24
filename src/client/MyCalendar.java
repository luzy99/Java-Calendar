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
import java.util.Timer;

public class MyCalendar extends JPanel implements ActionListener{
	private MyButton[] dayButtons =new MyButton[35];
	private JLabel[] dayTitle =new JLabel[7];
	private String[] DAYS="日 一 二 三 四 五 六".split(" ");
	//final private String[] DAYS="日 一 二 三 四 五 六".split(" ");

	Calendar calendar;//当前日期
	Calendar today=Calendar.getInstance();//今天的日期
	private int date;
	private int month;//0-11
	private int year;
	private int dayOfMonth;
	private int dayOfLastMonth;
	int startDay;//当前月的开始星期
	
	int border=0;
	Color normalColor=new Color(52,152,219);
	Color hoverColor=normalColor.brighter();
	Color todayColor=new Color(135,206,250);
	Color selectColor=normalColor.darker();
	
	MyButton selectedButton;

	//Timer timer;
	LeftPanel leftPanel;
	public MyCalendar(LeftPanel c) {
		leftPanel=c;
		calendar=c.cal;
		this.setLayout(new BorderLayout());//总布局
		JPanel titlePanel=new JPanel(new GridLayout(1,7,0,0));//标题栏
		for(int i=0;i<7;i++){
			dayTitle[i]=new JLabel(DAYS[i],JLabel.CENTER);
			//dayTitle[i].setOpaque(true);
			dayTitle[i].setForeground(Color.white);
			titlePanel.add(dayTitle[i]);
			titlePanel.setBackground(normalColor);
		}

		//日期网格
		JPanel datePanel=new JPanel(new GridLayout(5,7,0,0));//5行7列，间隙为0
		for(int i=0;i<35;i++){
			//dayButtons[i]=new MyButton(String.valueOf(i));
			dayButtons[i]=new MyButton("*");
			dayButtons[i].setNormalColor(normalColor);
			dayButtons[i].setHoverColor(hoverColor);
			dayButtons[i].setSelectedColor(selectColor);
			dayButtons[i].setForeground(Color.white);
			datePanel.add(dayButtons[i]);
			
			dayButtons[i].addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				@Override
				public void mouseClicked(MouseEvent e) {
					MyButton source=(MyButton)e.getSource();
					if(selectedButton!=null) {
						selectedButton.setSelect(false);
					}
					selectedButton=source;
					calendar.set(Calendar.DATE, Integer.parseInt(selectedButton.getText()));
					leftPanel.cal=calendar;
					leftPanel.updateLunar();
					leftPanel.setTaskList();
					
					source.setSelect(true);
					if(e.getClickCount()==2) {//双击
						//创建新的窗口
						JFrame frame = new ToDoListFrame(
								source.getToolTipText(),leftPanel.monthView,-1);
						//source.getParent().setEnabled(false);
					}
				}
			});
		}
		datePanel.setBackground(new Color(210,210,243));
		//设置布局
		this.add(titlePanel,BorderLayout.NORTH);
		this.add(datePanel,BorderLayout.CENTER);

		setCalendar(today);
		
		this.setPreferredSize(new Dimension(240,600));
		setVisible(true);
		//	System.out.println(instance.get(Calendar.MONTH));
	}

	public void setCalendar(Calendar cal) {
		setVisible(false);
		
		date=cal.get(Calendar.DATE);
		month=cal.get(Calendar.MONTH);
		year=cal.get(Calendar.YEAR);
		//初始化日历
		calendar=(Calendar)cal.clone();

		calendar.set(Calendar.DATE,1);
		startDay=calendar.get(Calendar.DAY_OF_WEEK);//当前月的开始星期
		
		calendar.roll(Calendar.DATE,-1);
		dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);//当前月的天数

		//获取上个月的天数
		calendar.roll(Calendar.MONTH, -1);
		calendar.set(Calendar.DATE,1);
		calendar.roll(Calendar.DATE,-1);
		dayOfLastMonth=calendar.get(Calendar.DAY_OF_MONTH);//上个月的天数

		//填充上个月的日期
		for(int i=0;i<startDay-1;i++) {
			dayButtons[startDay-i-2].setText(String.valueOf(dayOfLastMonth-i));
			dayButtons[startDay-i-2].setEnabled(false);
			//dayButtons[startDay-i-2].setBackground(normalColor);
			String toolTip=
					String.format("%d-%d-%d", month==0?year-1:year,(month-1+12)%12+1,dayOfLastMonth-i);
			dayButtons[startDay-i-2].setToolTipText(toolTip);
		}
		//填充这个月的日期
		for(int i=1;i<=dayOfMonth;i++) {
			dayButtons[(startDay+i-2)%35].setText(String.valueOf(i));
			String toolTip=String.format("%d-%d-%d", year,month+1,i);
			dayButtons[(startDay+i-2)%35].setToolTipText(toolTip);
			//dayButtons[(startDay+i-2)%35].setBackground(normalColor);
			dayButtons[(startDay+i-2)%35].setEnabled(true);
		}
		//填充下个月的日期
		for(int i=1;i<35-startDay-dayOfMonth+2;i++) {
			dayButtons[i+startDay+dayOfMonth-2].setText(String.valueOf(i));
			dayButtons[i+startDay+dayOfMonth-2].setEnabled(false);
			String toolTip=
					String.format("%d-%d-%d", month==11?year+1:year,(month+1)%12+1,i);
			//dayButtons[i+startDay+dayOfMonth-2].setBackground(normalColor);
			dayButtons[i+startDay+dayOfMonth-2].setToolTipText(toolTip);
		}
		calendar=(Calendar)cal.clone();
		
		if(today.get(Calendar.YEAR)==year&&today.get(Calendar.MONTH)==month) {
			dayButtons[startDay+date-2].setNormalColor(todayColor);
			selectedButton=dayButtons[startDay+date-2];
		}
		
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(selectedButton!=null) {
			selectedButton.setSelected(false);
			selectedButton.setBackground(normalColor);
		}
		dayButtons[startDay+date-2].setNormalColor(normalColor);
		if(e.getActionCommand()=="<-"){
			calendar.add(Calendar.MONTH, -1);
			setCalendar(calendar);
		}
		else if (e.getActionCommand()=="->") {
			calendar.add(Calendar.MONTH, +1);
			setCalendar(calendar);
		}
		else {
			setCalendar(today);
		}
	}
}
