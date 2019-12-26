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
		setTitle("My Calendar");    //设置显示窗口标题
		setSize(880,660);    //设置窗口显示尺寸
		setLocation(100,50);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //设置窗口关闭

		JPanel panel =new JPanel();
		add(panel);
		panel.setLayout(new BorderLayout(0,0));
		panel.setOpaque(true);
		panel.setBackground(Color.white);
		//上方面板
		up.setOpaque(false);
		timeLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
		timeLabel.setOpaque(false);
		up.add(timeLabel);

		lunarLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
		lunarLabel.setOpaque(false);
		String lunarDay=lc.getLunarDate(
				cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH)+1, 
				cal.get(Calendar.DATE), true);
		if(lunarDay.contains("月"))lunarDay="初一";
		lunarLabel.setText("  农历"+lc.getLunarMonth()+lunarDay);
		up.add(lunarLabel);

		//启动线程
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

		setVisible(true);    //设置窗口是否可见

		reloadTimer();
	}


	//重新加载定时器
	void reloadTimer() {
		Calendar c=Calendar.getInstance();
		long start=c.getTimeInMillis();
		//从本地获取从现在起之后的日程
		long end=Long.valueOf("9999999999999");
		Vector<Map<String, String>> records =LocalStorage.Obj.getByDate(start, end);
		//		for (Map<String, String> map : records) {
		//			if(map.get("alarm").contentEquals("0")) {
		//				records.remove(map);//将不提醒的删除
		//			}
		//		}

		//使用迭代器方法，不会报错
		Iterator<Map<String, String>> iterator = records.iterator();
		while(iterator.hasNext()){
			Map<String, String> map = iterator.next();
			if(map.get("alarm").contentEquals("0"))
				iterator.remove();   //将不提醒的删除
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
				new MainWindow();    //创建一个实例化对象
			}
		});
	}

	class CurrentTimeThread extends Thread{
		public void run() {
			while(true) {
				date = Calendar.getInstance().getTime();
				// 创建SimpleDateFormat对象，指定目标格式
				SimpleDateFormat sdf = new SimpleDateFormat("今天是yyyy年MM月dd日  HH:mm:ss");   
				// 将日期转换为指定格式的字符串
				String timeText = sdf.format(date); 

				timeLabel.setText(timeText);
				// 将Calendar对象转换为Date对象
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}