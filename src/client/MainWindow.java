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

	Calendar cal=Calendar.getInstance();
	Date date=cal.getTime();

	static Connection conn = null;
	public MainWindow()
	{
		setTitle("My Calendar");    //������ʾ���ڱ���
		setSize(880,660);    //���ô�����ʾ�ߴ�
		setLocation(100,50);
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
		if(lunarDay.contains("��"))lunarDay="��һ";
		lunarLabel.setText("  ũ��"+lc.getLunarMonth()+lunarDay);
		up.add(lunarLabel);

		//�����߳�
		CurrentTimeThread th = new CurrentTimeThread();
		th.setDaemon(true);
		th.start();

		middle=new MonthView();
		LeftPanel.leftPanel=new LeftPanel((MonthView)middle);
		left=LeftPanel.leftPanel;
		panel.add(up,BorderLayout.NORTH);
		panel.add(left,BorderLayout.WEST);
		panel.add(middle,BorderLayout.CENTER);
		up.setPreferredSize(new Dimension(MAXIMIZED_HORIZ, 40));
		left.setPreferredSize(new Dimension(190,MAXIMIZED_VERT));

		setVisible(true);    //���ô����Ƿ�ɼ�

		reloadTimer();
	}


	//���¼��ض�ʱ��
	void reloadTimer() {
		Calendar c=Calendar.getInstance();
		long start=c.getTimeInMillis();
		//�ӱ��ػ�ȡ��������֮����ճ�
		long end=Long.valueOf("9999999999999");
		Vector<Map<String, String>> records =LocalStorage.Obj.getByDate(start, end);
		//		for (Map<String, String> map : records) {
		//			if(map.get("alarm").contentEquals("0")) {
		//				records.remove(map);//�������ѵ�ɾ��
		//			}
		//		}

		//ʹ�õ��������������ᱨ��
		Iterator<Map<String, String>> iterator = records.iterator();
		while(iterator.hasNext()){
			Map<String, String> map = iterator.next();
			if(map.get("alarm").contentEquals("0"))
				iterator.remove();   //�������ѵ�ɾ��
		}
		for (Map<String, String> map : records) {
			MyTimer.mytimer.addTimer(
					Integer.valueOf(map.get("memoID")), 
					Long.valueOf(map.get("remindTime")), 
					map.get("title"), map.get("address"), 
					Long.valueOf(map.get("startTime")),
					Long.valueOf(map.get("endTime")));
		}
	}

	public static void main(String[] agrs)
	{
		try {
			String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LocalStorage.Obj.sync();
				new MainWindow();    //����һ��ʵ��������
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