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
	Map<Integer, Map<String, String>> recordsMap;
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
		writeFile();
		return memoID;
	}
	//ɾ��һ����¼
	void deleteRecord(int id) {
		recordsMap.remove(id);
		writeFile();
	}
	//�޸ļ�¼
	void updateRecord(int id,Map<String, String> newRecord) {
		recordsMap.put(id,newRecord);
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
		Vector<Map<String,String>> serverRecords=
				CalendarClient.Obj.getByDate(0, Long.valueOf("9999999999999"));
		Map<Integer, Map<String, String>>serverMap=new HashMap<Integer, Map<String,String>>();
		//������������ת��map
		for (Map<String, String> map : serverRecords) {
			serverMap.put(Integer.valueOf(map.get("memoID")),map);
		}

		//�����������ͬ��
		//ͬ������
		for (int key : recordsMap.keySet()) {
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
