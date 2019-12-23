package client;

import java.sql.PreparedStatement;
import java.util.*;

public class MyTimer{
	//memoID,Timer
	TreeMap<Integer, Timer> timers=new TreeMap<Integer, Timer>();
	Timer timer;
//	int MaxID;
	
	Calendar cal=Calendar.getInstance();
	public MyTimer() {

	}
	void addTimer(
			int id,long remindTimestamp,
			String title,String address,long stime,long etime){
		Date date=new Date(remindTimestamp);
		timer = new Timer();
        timer.schedule(new AlarmTask(title,address,stime,etime), date);
        timers.put(id, timer);
	}
	void cancelTimer(int id) {
		try {
			if(timers.containsKey(id)) {
				timers.get(id).cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class AlarmTask extends TimerTask{
	Calendar currentCal=Calendar.getInstance();
	int memoId;
	String title;
	String address;
	long starttime;
	long endtime;
    public AlarmTask(String title,String address,long stime,long etime) {
    	this.title=title;
    	this.address=address;
    	this.starttime=stime;
    	this.endtime=etime;
    }
    
    @Override
    public void run() {
        System.out.println(title);
        System.out.println(address);
    }
}
