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
	//key=-1��¼�ϴβ�����Ϣ
	Map<Integer, Map<String, String>> recordsMap;//<-1,<"lastOpt"="ADD","lastTime"="xxx">>
	Map<String, String> lastOptMap=new HashMap<String, String>();
	String path="./userdata.dat";
	ObjectOutputStream out;
	ObjectInputStream in;
	static LocalStorage Obj=new LocalStorage();

	public LocalStorage() {
	}

	//���һ����¼(����id)(memoID�Զ�+1)
	int addRecord(Map<String, String> newRecord) {
		if(recordsMap==null) {
			recordsMap=new HashMap<Integer, Map<String,String>>();
		}
		int memoID=getMaxKey()+1;
		newRecord.put("memoID",String.valueOf(memoID));
		recordsMap.put(memoID,newRecord);

		//���²�����¼
		if(lastOptMap==null) {lastOptMap=new HashMap<String, String>();}
		lastOptMap.put("lastOpt","ADD");
		lastOptMap.put("lastTime",String.valueOf(System.currentTimeMillis()));
		recordsMap.put(-1,lastOptMap);
		writeFile();
		return memoID;
	}
	//ɾ��һ����¼
	void deleteRecord(int id) {
		recordsMap.remove(id);

		//���²�����¼
		lastOptMap.put("lastOpt","DELETE");
		lastOptMap.put("lastTime",String.valueOf(System.currentTimeMillis()));
		recordsMap.put(-1,lastOptMap);
		writeFile();
	}
	//�޸ļ�¼
	void updateRecord(int id,Map<String, String> newRecord) {
		recordsMap.put(id,newRecord);

		//���²�����¼
		lastOptMap.put("lastOpt","UPDATE");
		lastOptMap.put("lastTime",String.valueOf(System.currentTimeMillis()));
		recordsMap.put(-1,lastOptMap);
		writeFile();
	}

	//��id��ȡ
	Map<String,String> getByID(int id){
		return recordsMap.get(id);
	}
	//�����ڻ�ȡ
	Vector<Map<String,String>> getByDate(long start,long end){
		if(recordsMap==null) {
			loadFile();
		}
		Vector<Map<String, String>> results=new Vector<Map<String,String>>();
		for (int id : recordsMap.keySet()) {
			if(id==-1)continue;//����key=1
			Map<String, String> currentRecord=recordsMap.get(id);
			if(Long.valueOf(currentRecord.get("startTime"))<end
					&&Long.valueOf(currentRecord.get("endTime"))>start) {
				results.add(currentRecord);
			}
		}
		return results;
	}

	//д���ļ�
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

	//���ļ��м���
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

	//���ݿ�ͬ��
	void sync() {
		//����ʧ����ͬ��
		if(CalendarClient.Obj.connect()==false) {
			return;
		}
		loadFile();
		lastOptMap=recordsMap.get(-1);
		//��ȡ������������¼
		Map<String,String> serverLastOpt=CalendarClient.Obj.lastOpt();

		Vector<Map<String,String>> serverRecords=
				CalendarClient.Obj.getByDate(0, Long.valueOf("9999999999999"));
		Map<Integer, Map<String, String>>serverMap=new HashMap<Integer, Map<String,String>>();
		//������������ת��map
		for (Map<String, String> map : serverRecords) {
			serverMap.put(Integer.valueOf(map.get("memoID")),map);
		}

		//����������޸�ʱ��ȱ����磨�����������ͬ����
		if(lastOptMap!=null
				&&(serverLastOpt.isEmpty()
						||Long.valueOf(serverLastOpt.get("lastTime"))
							<Long.valueOf(lastOptMap.get("lastTime")))){
			//�����������ͬ��
			//ͬ������
			for (int key : recordsMap.keySet()) {
				if(key==-1)continue;//key������-1������
				//����ͬid�ļ�¼
				if(serverMap.containsKey(key)) {
					//�������޸�ʱ����
					if(Long.valueOf(serverMap.get(key).get("editTime"))
							< Long.valueOf(recordsMap.get(key).get("editTime"))) {
						CalendarClient.Obj.updateRecord(recordsMap.get(key));//���·�������¼
					}//�������޸�ʱ����
					else if (Long.valueOf(serverMap.get(key).get("editTime"))
							> Long.valueOf(recordsMap.get(key).get("editTime"))) {
						updateRecord(key, serverMap.get(key));//���Ǳ��ؼ�¼
					}
				}else {//�����з�����û�еļ�¼
					CalendarClient.Obj.addRecord(recordsMap.get(key));//��ӷ�������¼
				}
			}
			//ͬ��ɾ��
			for(int key:serverMap.keySet()) {
				//�����ѱ�ɾ���ļ�¼
				if(!recordsMap.containsKey(key)) {
					CalendarClient.Obj.deleteRecord(key);
				}
			}
		}else {
			//�������򱾵�ͬ��
			//���Ǳ��ؼ�¼
			recordsMap.clear();
			recordsMap.put(-1,lastOptMap);
			recordsMap.putAll(serverMap);
			writeFile();
		}

	}
	//��ȡ���key
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
