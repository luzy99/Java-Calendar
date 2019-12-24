package client;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TaskListPanel extends JPanel{
	Calendar cal;
	int month;
	int day;
	int year;
	
	ArrayList<Integer> iDList=new ArrayList<Integer>();
	JLabel dateLabel;
	LunarCalendar lc=new LunarCalendar();
	Vector<String> taskStrs =new Vector<String>();
	JList<String> taskList=new JList<String>();
	JScrollPane jsp;
	
	static JList<String> currentSelect;
	
	public TaskListPanel(MonthView mv) {
		this.setLayout(new BorderLayout());
		this.setBackground(Color.white);
		
		dateLabel=new JLabel("*");
		dateLabel.setFont(new Font("微软雅黑", Font.BOLD, 11));
		add(dateLabel,BorderLayout.NORTH);
		
		//taskList.setOpaque(false);
		taskList.setForeground(Color.gray);
		taskList.setSelectionBackground(new Color(52,152,219));
		taskList.setFont(new Font("微软雅黑", 0, 12));
		
		//添加事件监听
		taskList.addListSelectionListener(new ListSelectionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					JList<String> source=(JList<String>)e.getSource();
					//取消其他日程的选择
					if(currentSelect!=null&&currentSelect!=source) {
						currentSelect.clearSelection();
					}
	                currentSelect=source;
	            }
			}
		});
		//添加双击事件
		taskList.addMouseListener(new MouseListener() {
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
				if(taskList.getLastVisibleIndex()!=-1&&e.getClickCount()==2) {//双击
					String tt=String.format("%d-%d-%d", year,month,day);
					//创建新的窗口
					System.out.println(iDList);
					JFrame frame = new ToDoListFrame(tt,mv,
							iDList.get(taskList.getSelectedIndex()));
				}
			}
		});

		jsp=new JScrollPane(taskList);
		JScrollBar jsb=new JScrollBar();
		jsb.setPreferredSize(new Dimension(3, jsb.getPreferredSize().height));
		jsp.setVerticalScrollBar(jsb);
		jsp.setVerticalScrollBarPolicy(                
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setBorder(null);
		add(jsp,BorderLayout.CENTER);
	}
	
	void setDateList(int y,int m,int d) {
		year=y;
		month=m;
		day=d;
		
		String titleString =String.valueOf(day)+" "
				+lc.getLunarDate(year, month, day, false);
		dateLabel.setText(titleString);
		
		//按日期获取记录
		Calendar c=Calendar.getInstance();
		c.clear();
		c.set(year, month-1, day);
		long start=c.getTimeInMillis();
		c.roll(Calendar.DATE, 1);
		long end=c.getTimeInMillis();

		taskStrs.clear();
		//Vector<Map<String, String>> records =CalendarClient.Obj.getByDate(start, end);
		
		//从本地获取
		Vector<Map<String, String>> records =LocalStorage.Obj.getByDate(start, end);
		iDList.clear();
		//System.out.println(records);
		for(Map<String, String> record: records) {
			taskStrs.add(record.get("title"));
			iDList.add(Integer.valueOf(record.get("memoID")));
		}
		//添加日程
		taskList.setListData(taskStrs);
	}
}
