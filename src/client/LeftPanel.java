package client;

import java.awt.*;
import java.util.Calendar;

import javax.swing.*;

public class LeftPanel extends JPanel{
	MonthView monthView;
	Calendar cal=Calendar.getInstance();
	MyCalendar smallCalender;
	JPanel switchBar;
	
	JLabel dateLabel=new JLabel();
	LunarCalendar lc=new LunarCalendar();
	String[] dayOfWeek= {"������","����һ","���ڶ�","������","������","������","������"};
	
	public LeftPanel(MonthView my) {
		monthView=my;
		smallCalender=new MyCalendar(this);
		Box verticalBox=Box.createVerticalBox();    //��������Box����
        this.add(verticalBox);
        verticalBox.add(Box.createHorizontalStrut(185)); 
		switchBar=new DateSwitchBar(monthView,smallCalender);

		verticalBox.add(switchBar);
		smallCalender.setPreferredSize(new Dimension(WIDTH, 120));
		verticalBox.add(smallCalender,BorderLayout.CENTER);
		
		dateLabel.setFont(new Font("΢���ź�", Font.BOLD, 15));
		dateLabel.setForeground(Color.WHITE);
		dateLabel.setOpaque(false);
		//dateLabel.setMaximumSize(new Dimension(50,10));
		
		Box dateBox=Box.createHorizontalBox(); 
		dateBox.add(Box.createHorizontalStrut(5));
		dateBox.add(dateLabel);
		dateBox.add(Box.createHorizontalGlue());
		verticalBox.add(dateBox);
		updateInfo();
		this.setBackground(new Color(52,152,219));
		this.setVisible(true);
	}
	void updateInfo(){
		String lunarDay=lc.getLunarDate(
				cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH)+1, 
				cal.get(Calendar.DATE), true);
		dateLabel.setText("ũ��"+lc.getLunarMonth()+lunarDay+" "
				+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
	}
}
