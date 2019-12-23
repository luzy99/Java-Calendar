package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class CalendarClient {
	Socket client=null;
	String serverAddr="localhost";
	int serverPort=8888;
	PrintWriter out;
	InputStream in;
	static boolean status=true;
	static CalendarClient Obj=new CalendarClient();

	public static Map<String,String> stringToMap(String str){  
		str=str.substring(1, str.length()-1);  
		String[] strs=str.replace(", ",",").split(",");  
		Map<String,String> map = new HashMap<String, String>();  
		for (String string : strs) {  
			String key=string.split("=")[0]; 
			String value=new String();
			if(string.split("=").length!=1) {
				value=string.split("=")[1];  
			}
			map.put(key, value);  
		}  
		return map;  
	}  

	public CalendarClient() {
	}
	//连接服务器
	public boolean connect() {
		if(status==false)return false;
		try {
			client=new Socket(serverAddr,serverPort);
			System.out.println("Client: "+client);
			out=new PrintWriter(client.getOutputStream());
			status=true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			status=false;
		}
		return false;
	}
	//按ID获取
	public Map<String, String> getByID(int id) {
		connect();
		String param=String.format("{type=getByID,id=%d}", id);
		out.println(param);
		out.flush();
		System.out.println("<-getByID: "+param);

		Map<String,String> map=new HashMap<String, String>();
		//接收返回信息
		try {
			in = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String response = br.readLine();
			System.out.println("-> "+response);
			if(!response.isBlank()) {
				map=stringToMap(response);
			}
			System.out.println(map.get("title"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	//按日期获取
	public Vector<Map<String,String>> getByDate(long start,long end) {
		connect();
		String param=String.format("{type=getByDate,start=%d,end=%d}", start,end);
		out.println(param);
		out.flush();
		System.out.println("<-Send: "+param);

		//接收返回信息
		Vector<Map<String,String>> records=new Vector<Map<String,String>>();
		try {
			in = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String response;
			while ((response=br.readLine()).isBlank()==false) {
				System.out.println("-> "+response);
				records.add(stringToMap(response));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return records;
	}

	//添加记录
	public int addRecord(Map<String,String> newRecord) {
		connect();
		int result=-1;
		String dataString=newRecord.toString();
		String param=String.format("{type=add,%s}", 
				dataString.substring(1, dataString.length()-1));
		out.println(param);
		out.flush();
		System.out.println("<-Send: "+param);

		//接收返回信息
		try {
			in = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String response = br.readLine();
			System.out.println("-> "+response);
			//返回id
			result = Integer.valueOf(stringToMap(response).get("id"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	//修改记录
	public boolean updateRecord(Map<String,String> newRecord) {
		connect();
		String dataString=newRecord.toString();
		String param=String.format("{type=update,%s}", 
				dataString.substring(1, dataString.length()-1));
		out.println(param);
		out.flush();
		System.out.println("<-Send: "+param);

		//接收返回信息
		try {
			in = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String response = br.readLine();
			System.out.println("-> "+response);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	//删除记录
	public boolean deleteRecord(int id) {
		connect();

		String param=String.format("{type=delete,id=%d}", id);
		out.println(param);
		out.flush();
		System.out.println("<-Send: "+param);

		//接收返回信息
		try {
			in = client.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String response = br.readLine();
			System.out.println("-> "+response);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {

		CalendarClient c=new CalendarClient();
		c.getByID(51);
		long s=Long.valueOf("1576884600000");
		long e=Long.valueOf("1576933200000");

		c.getByDate(s, e);
		Map<String,String> map=new HashMap<String, String>();
		map.put("title","xzwnb");
		map.put("id","103");
		map.put("detail","666");
		c.updateRecord(map);
		c.deleteRecord(101);
	}

}
