package client;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MainWindow extends JFrame
{
	JPanel up=new JPanel(new FlowLayout(FlowLayout.LEFT));
	JLabel timeLabel=new JLabel();
	JLabel lunarLabel=new JLabel();
	LunarCalendar lc=new LunarCalendar();

	JPanel middle;
	JPanel left;
	static MyTimer myTimer;
	Calendar cal=Calendar.getInstance();
	Date date=cal.getTime();

	static Connection conn = null;
	static MyTimer getTimer() {return myTimer;}
	public MainWindow(MyTimer t)
	{
		myTimer=t;
		setTitle("My Calendar");    //������ʾ���ڱ���
		setSize(880,660);    //���ô�����ʾ�ߴ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //���ô��ڹر�

		JPanel panel =new JPanel();
		add(panel);
		panel.setLayout(new BorderLayout(0,0));
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		//�Ϸ����
		up.setOpaque(false);
		timeLabel.setFont(new Font("΢���ź�", Font.BOLD, 18));
		timeLabel.setOpaque(false);
		up.add(timeLabel);

		lunarLabel.setFont(new Font("΢���ź�", Font.BOLD, 18));
		lunarLabel.setOpaque(false);
		String lunarDay=lc.getLunarDate(
				cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH)+1, 
				cal.get(Calendar.DATE), true);
		lunarLabel.setText("  ũ��"+lc.getLunarMonth()+lunarDay);
		up.add(lunarLabel);

		//�����߳�
		CurrentTimeThread th = new CurrentTimeThread();
		th.setDaemon(true);
		th.start();

		middle=new MonthView();
		left=new LeftPanel((MonthView)middle);
		panel.add(up,BorderLayout.NORTH);
		panel.add(left,BorderLayout.WEST);
		panel.add(middle,BorderLayout.CENTER);
		up.setPreferredSize(new Dimension(MAXIMIZED_HORIZ, 40));
		left.setPreferredSize(new Dimension(180,MAXIMIZED_VERT));

		setVisible(true);    //���ô����Ƿ�ɼ�
	}
	public static void main(String[] agrs)
	{
		try {
			String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			//lookAndFeel = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MyTimer t=new MyTimer();
				new MainWindow(t);    //����һ��ʵ��������
			}
		});
	}

	class CurrentTimeThread extends Thread{
		public void run() {
			while(true) {
				date = Calendar.getInstance().getTime();
				// ����SimpleDateFormat����ָ��Ŀ���ʽ
				SimpleDateFormat sdf = new SimpleDateFormat("������yyyy��MM��dd��  HH:mm:ss");   
				// ������ת��Ϊָ����ʽ���ַ���
				String timeText = sdf.format(date); 
				
				timeLabel.setText(timeText);
				// ��Calendar����ת��ΪDate����
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}