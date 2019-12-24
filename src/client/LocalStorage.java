package client;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class LocalStorage {
	//key=-1记录上次操作信息
	Map<Integer, Map<String, String>> recordsMap;//<-1,<"lastOpt"="ADD","lastTime"="xxx">>
	Map<String, String> lastOptMap=new HashMap<String, String>();
	String path="./userdata.dat";
	ObjectOutputStream out;
	ObjectInputStream in;
	static LocalStorage Obj=new LocalStorage();

	public LocalStorage() {
	}

	//添加一条记录(不含id)(memoID自动+1)
	int addRecord(Map<String, String> newRecord) {
		if(recordsMap==null) {
			recordsMap=new HashMap<Integer, Map<String,String>>();
		}
		int memoID=getMaxKey()+1;
		newRecord.put("memoID",String.valueOf(memoID));
		recordsMap.put(memoID,newRecord);

		//更新操作记录
		if(lastOptMap==null) {lastOptMap=new HashMap<String, String>();}
		lastOptMap.put("lastOpt","ADD");
		lastOptMap.put("lastTime",String.valueOf(System.currentTimeMillis()));
		recordsMap.put(-1,lastOptMap);
		writeFile();
		return memoID;
	}
	//删除一条记录
	void deleteRecord(int id) {
		recordsMap.remove(id);

		//更新操作记录
		lastOptMap.put("lastOpt","DELETE");
		lastOptMap.put("lastTime",String.valueOf(System.currentTimeMillis()));
		recordsMap.put(-1,lastOptMap);
		writeFile();
	}
	//修改记录
	void updateRecord(int id,Map<String, String> newRecord) {
		recordsMap.put(id,newRecord);

		//更新操作记录
		lastOptMap.put("lastOpt","UPDATE");
		lastOptMap.put("lastTime",String.valueOf(System.currentTimeMillis()));
		recordsMap.put(-1,lastOptMap);
		writeFile();
	}

	//按id获取
	Map<String,String> getByID(int id){
		return recordsMap.get(id);
	}
	//按日期获取
	Vector<Map<String,String>> getByDate(long start,long end){
		if(recordsMap==null) {
			loadFile();
		}
		Vector<Map<String, String>> results=new Vector<Map<String,String>>();
		for (int id : recordsMap.keySet()) {
			if(id==-1)continue;//跳过key=1
			Map<String, String> currentRecord=recordsMap.get(id);
			if(Long.valueOf(currentRecord.get("startTime"))<end
					&&Long.valueOf(currentRecord.get("endTime"))>start) {
				results.add(currentRecord);
			}
		}
		return results;
	}

	//写入文件
	void writeFile() {
		try {
			out = new ObjectOutputStream(new FileOutputStream(path));
			out.writeObject(recordsMap);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//从文件中加载
	@SuppressWarnings("unchecked")
	void loadFile() {
		try {
			in = new ObjectInputStream(new FileInputStream(path));
			recordsMap=(Map<Integer, Map<String,String>>)in.readObject();
			System.out.println(recordsMap);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File Not Exists!");
			recordsMap=new HashMap<Integer, Map<String,String>>();
		}finally {
			try {
				if(in!=null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//数据库同步
	void sync() {
		//连接失败则不同步
		if(CalendarClient.Obj.connect()==false) {
			return;
		}
		loadFile();
		lastOptMap=recordsMap.get(-1);
		//获取服务器操作记录
		Map<String,String> serverLastOpt=CalendarClient.Obj.lastOpt();

		Vector<Map<String,String>> serverRecords=
				CalendarClient.Obj.getByDate(0, Long.valueOf("9999999999999"));
		Map<Integer, Map<String, String>>serverMap=new HashMap<Integer, Map<String,String>>();
		//将服务器数据转成map
		for (Map<String, String> map : serverRecords) {
			serverMap.put(Integer.valueOf(map.get("memoID")),map);
		}

		//服务器最后修改时间比本地早（本地向服务器同步）
		if(lastOptMap!=null
				&&(serverLastOpt.isEmpty()
						||Long.valueOf(serverLastOpt.get("lastTime"))
							<Long.valueOf(lastOptMap.get("lastTime")))){
			//本地向服务器同步
			//同步新增
			for (int key : recordsMap.keySet()) {
				if(key==-1)continue;//key不等于-1！！！
				//有相同id的记录
				if(serverMap.containsKey(key)) {
					//服务器修改时间早
					if(Long.valueOf(serverMap.get(key).get("editTime"))
							< Long.valueOf(recordsMap.get(key).get("editTime"))) {
						CalendarClient.Obj.updateRecord(recordsMap.get(key));//更新服务器记录
					}//服务器修改时间晚
					else if (Long.valueOf(serverMap.get(key).get("editTime"))
							> Long.valueOf(recordsMap.get(key).get("editTime"))) {
						updateRecord(key, serverMap.get(key));//覆盖本地记录
					}
				}else {//本地有服务器没有的记录
					CalendarClient.Obj.addRecord(recordsMap.get(key));//添加服务器记录
				}
			}
			//同步删除
			for(int key:serverMap.keySet()) {
				//本地已被删除的记录
				if(!recordsMap.containsKey(key)) {
					CalendarClient.Obj.deleteRecord(key);
				}
			}
		}else {
			//服务器向本地同步
			//覆盖本地记录
			recordsMap.clear();
			recordsMap.put(-1,lastOptMap);
			recordsMap.putAll(serverMap);
			writeFile();
		}

	}
	//获取最大key
	private int getMaxKey() {
		int maxKey=0;
		for (int key : recordsMap.keySet()) {
			if(key>maxKey) {
				maxKey=key;
			}
		}
		return maxKey;
	}
	public static void main(String[] args) {
		LocalStorage loc=new LocalStorage();
		Map<String,String> item=new HashMap<String, String>();
		item.put("memoID", "201");
		item.put("title", "local");
		//loc.recordsMap=new HashMap<Integer, Map<String,String>>();
		//loc.recordsMap.put(201, item);
		//loc.addRecord(item);
		//loc.writeFile();
		loc.loadFile();
	}
}
