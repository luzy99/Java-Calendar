package client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class LeftPanel extends JPanel{
	MonthView monthView;
	Calendar cal=Calendar.getInstance();
	MyCalendar smallCalender;
	JPanel switchBar;

	JLabel dateLabel=new JLabel();
	LunarCalendar lc=new LunarCalendar();
	String[] dayOfWeek= {"������","����һ","���ڶ�","������","������","������","������"};

	MyButton newEvent =new MyButton("+ �½��ճ�   ");
	JList<String> taskList=new JList<String>();
	static JList<String> currentSelect;
	Vector<String> taskStrs =new Vector<String>();
	ArrayList<Integer> iDList=new ArrayList<Integer>();

	public LeftPanel(MonthView my) {
		monthView=my;
		smallCalender=new MyCalendar(this);
		Box verticalBox=Box.createVerticalBox();    //��������Box����
		
		this.add(verticalBox);
		//verticalBox.add(Box.createHorizontalStrut(160)); 
		switchBar=new DateSwitchBar(monthView,smallCalender);

		verticalBox.add(switchBar);
		smallCalender.setPreferredSize(new Dimension(WIDTH, 120));
		verticalBox.add(smallCalender);

		dateLabel.setFont(new Font("΢���ź�", Font.BOLD, 15));
		dateLabel.setForeground(Color.WHITE);
		dateLabel.setOpaque(false);
		//dateLabel.setMaximumSize(new Dimension(50,10));

		Box dateBox=Box.createHorizontalBox(); 
		dateBox.add(Box.createHorizontalStrut(5));
		dateBox.add(dateLabel);
		dateBox.add(Box.createHorizontalGlue());
		verticalBox.add(dateBox);

		//�½��ճ�
		verticalBox.add(Box.createVerticalStrut(5));
		newEvent.setFont(new Font("΢���ź�", Font.BOLD, 18));
		newEvent.setForeground(Color.white);
		newEvent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new ToDoListFrame(
						smallCalender.selectedButton.getToolTipText(),monthView,-1);
			}
		});
		verticalBox.add(newEvent);
		verticalBox.add(Box.createVerticalStrut(5));

		//��ǰ�ճ�
		taskList.setForeground(Color.gray);
		taskList.setSelectionBackground(new Color(52,152,219));
		taskList.setFont(new Font("΢���ź�", 0, 12));

		//���˫���¼�
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
				if(taskList.getLastVisibleIndex()!=-1&&e.getClickCount()==2) {//˫��
					//�����µĴ���
					System.out.println(iDList);
					if(iDList.isEmpty())return;
					JFrame frame = new ToDoListFrame(smallCalender.selectedButton.getToolTipText(),
							monthView,
							iDList.get(taskList.getSelectedIndex()));
				}
			}
		});
		JScrollPane jsp=new JScrollPane(taskList);
		jsp=new JScrollPane(taskList);
		JScrollBar jsb=new JScrollBar();
		jsb.setPreferredSize(new Dimension(3, jsb.getPreferredSize().height));
		jsp.setVerticalScrollBar(jsb);
		jsp.setVerticalScrollBarPolicy(                
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setBorder(null);
		taskList.setBackground(new Color(52,152,219));
		taskList.setForeground(Color.white);
		taskList.setFont(new Font("΢���ź�", 0, 14));
		
		setTaskList();
		//taskList.setMaximumSize(new Dimension(160,160));
		//jsp.setMaximumSize(new Dimension(160,160));
		verticalBox.add(jsp);
		updateLunar();
		this.setBackground(new Color(52,152,219));
		this.setVisible(true);
	}
	void setTaskList() {

		//�����ڻ�ȡ��¼
		Calendar c=Calendar.getInstance();
		c.clear();
		c.set(Calendar.YEAR,cal.get(Calendar.YEAR));
		c.set(Calendar.MONTH,cal.get(Calendar.MONTH));
		c.set(Calendar.DATE,cal.get(Calendar.DATE));
		long start=c.getTimeInMillis();
		c.roll(Calendar.DATE, 1);
		long end=c.getTimeInMillis();

		taskStrs.clear();

		//�ӱ��ػ�ȡ
		Vector<Map<String, String>> records =LocalStorage.Obj.getByDate(start, end);
		iDList.clear();
		//System.out.println(records);
		for(Map<String, String> record: records) {
			taskStrs.add(record.get("title"));
			iDList.add(Integer.valueOf(record.get("memoID")));
		}
		//����ճ�
		if(taskStrs.isEmpty()) {
			taskStrs.add("");
		}
		taskList.setListData(taskStrs);
		
	}

	//����ũ��
	void updateLunar(){
		String lunarDay=lc.getLunarDate(
				cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH)+1, 
				cal.get(Calendar.DATE), true);
		if(lunarDay.contains("��"))lunarDay="��һ";
		dateLabel.setText("ũ��"+lc.getLunarMonth()+lunarDay+" "
				+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
	}
}
