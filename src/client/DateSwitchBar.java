package client;

import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;

import javax.swing.*;

public class DateSwitchBar extends JPanel implements ActionListener{
	private MyButton leftButton=new MyButton("<-");
	private MyButton rightButton=new MyButton("->");
	private MyButton middleButton;
	int year;
	int month;
	Calendar calendar=Calendar.getInstance();
	MonthView mainCalendar;
	MyCalendar smallCalendar;

	public DateSwitchBar(MonthView main,MyCalendar small) {
		mainCalendar=main;
		smallCalendar=small;
		this.setLayout(new BorderLayout());
		middleButton=new MyButton("*-*");
		
		leftButton.setForeground(Color.white);
		rightButton.setForeground(Color.white);
		
		updateText();
		middleButton.setMargin(null);
		middleButton.setBorder(null);
		leftButton.setPreferredSize(new Dimension(30,20));

		leftButton.setBorder(null);
		rightButton.setPreferredSize(new Dimension(30,20));
		rightButton.setBorder(null);

		this.add(leftButton,BorderLayout.WEST);
		this.add(middleButton,BorderLayout.CENTER);
		this.add(rightButton,BorderLayout.EAST);

		leftButton.addActionListener(this);
		rightButton.addActionListener(this);
		middleButton.addActionListener(this);
		middleButton.addActionListener(mainCalendar);
		leftButton.addActionListener(mainCalendar);
		rightButton.addActionListener(mainCalendar);
		middleButton.addActionListener(smallCalendar);
		leftButton.addActionListener(smallCalendar);
		rightButton.addActionListener(smallCalendar);

		this.setBackground(new Color(52,152,219));
		this.setVisible(true);
	}
	public void updateText() {
		year=calendar.get(Calendar.YEAR);
		month=calendar.get(Calendar.MONTH);
		String text=String.format("%d-%d", year,month+1);
		middleButton.setText(text);
		middleButton.setForeground(Color.white);
		middleButton.setOpaque(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="<-"){
			calendar.add(Calendar.MONTH,-1);
		}
		else if (e.getActionCommand()=="->") {
			calendar.add(Calendar.MONTH,1);
		}
		else{
			calendar=(Calendar)mainCalendar.today.clone();
		}
		updateText();
	}
}

