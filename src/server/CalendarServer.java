package server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import client.MainWindow;

public class CalendarServer {
	ServerSocket server;
	int serverPort=8888;
	static Connection conn = null;

	public CalendarServer() {
		try {
			server=new ServerSocket(serverPort);
			System.out.println("ServerSocket: "+server);
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listen() {
		while(true) {
			try {
				Socket socket=server.accept();
				System.out.println("Socket: "+socket);
				new ClientThread(socket).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//连接数据库
	public static void connect() {

		try {
			//sqlite
			String url = "jdbc:sqlite:./sqlite/todolist.db";
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");

//			//MySQL
//			String url = "jdbc:mysql://localhost:3306/calendar?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";
//			conn = DriverManager.getConnection(url,"root","123456");
//			System.out.println("Connection to MySQL has been established.");
			
			Statement s = conn.createStatement();
			String sql=	"CREATE TABLE IF NOT EXISTS todolist ("
					+"memoID INTEGER PRIMARY KEY autoincrement,"
					+"title TEXT,"
					+"address TEXT,"
					+"startTime integer,"
					+"endTime integer,"
					+"detail TEXT,"
					+"alarm integer,"
					+"remindTime integer"
					+")";
			s.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}finally {
			//			try { if (conn != null) conn.close();
			//			} catch(SQLException e) {}
		}
	}
	public static void main(String[] args) {
		new CalendarServer().listen();
	}
}

class ClientThread extends Thread{
	Socket socket;
	PrintWriter out;
	public ClientThread(Socket s) {
		this.socket=s;
		try {
			socket.setSoTimeout(10000);
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void run() {
		InputStream in=null;
		try {
			in=socket.getInputStream();
			out=new PrintWriter(socket.getOutputStream());

			//接收客户端信息
			String request;
			BufferedReader rd=new BufferedReader(new InputStreamReader(in));
			request=rd.readLine();
			System.out.println("-> "+request);

			//判断请求类型
			Map<String,String> reqMap = stringToMap(request);
			String type=reqMap.get("type");
			//System.out.println("-> "+type);
			String response=new String();

			if(type.contentEquals("getByID")) {
				response=returnByID(reqMap.get("id"));
			}else if (type.contentEquals("getByDate")) {
				response=returnByDate(reqMap.get("start"),reqMap.get("end"));
			}
			else if (type.contentEquals("add")) {
				response=addRecord(request);
			}else if (type.contentEquals("update")) {
				response=updateRecord(request);
			}else if (type.contentEquals("delete")) {
				response=deleteRecord(reqMap.get("id"));
			}

			//返回消息
			out.println(response);
			out.flush();
			System.out.println("<- "+response);

		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(in!=null) {
					in.close();
				}
				socket.close();
				System.out.println("Close Socket: "+socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//按id返回
	String returnByID(String id) {
		Map<String, String> resultMap=new HashMap<String, String>();
		PreparedStatement s;

		try {
			s = CalendarServer.conn.prepareStatement ( 
					"SELECT * FROM todolist WHERE memoID=?");
			s.setString(1, id);

			ResultSet rs=s.executeQuery();
			ResultSetMetaData rm=rs.getMetaData();
			if (rs.next()) {
				for (int i=1;i<=rm.getColumnCount();i++) {
					resultMap.put(rm.getColumnName(i), rs.getString(i));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultMap.toString();
	}

	//按日期返回
	String returnByDate(String start,String end) {
		String responseStr=new String();
		Map<String, String> resultMap=new HashMap<String, String>();
		PreparedStatement s;

		try {
			s = CalendarServer.conn.prepareStatement ( 
					"SELECT * FROM todolist WHERE startTime < ? AND endTime > ?");
			s.setString(1, end);
			s.setString(2, start);

			ResultSet rs=s.executeQuery();
			ResultSetMetaData rm=rs.getMetaData();
			while (rs.next()) {
				resultMap.clear();
				for (int i=1;i<=rm.getColumnCount();i++) {
					resultMap.put(rm.getColumnName(i), rs.getString(i));
				}
				responseStr+=resultMap.toString()+"\n";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return responseStr;
	}

	//新增记录
	String addRecord(String data) {
		Map<String, String> dataMap=stringToMap(data);
		PreparedStatement s;
		String response=new String();

		try {
			int memoID=getMaxKey()+1;
			s = CalendarServer.conn.prepareStatement ( 
					"INSERT INTO todolist ("
							+ "title,address,startTime,endTime,detail,alarm,remindTime,memoID)"
							+ "VALUES(?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			s.setString (1, dataMap.get("title")); 
			s.setString (2, dataMap.get("address"));
			s.setString(3, dataMap.get("startTime")); 
			s.setString (4, dataMap.get("endTime"));
			s.setString (5, dataMap.get("detail")); 
			s.setString (6, dataMap.get("alarm"));
			s.setString (7, dataMap.get("remindTime")); 
			
			s.setInt(8, memoID); 
			s.executeUpdate();
			
//			ResultSet rs=s.getGeneratedKeys();//获取自动生成的主键
//			if (rs.next()) {
//				memoID=rs.getInt(1);
//				System.out.println("Server: 新增记录成功 id=" + memoID);
//				response=String.format("{status=OK,id=%d}", memoID);
//			}		
			
			response=String.format("{status=OK,id=%d}", memoID);
		} catch (SQLException e) {
			e.printStackTrace();
			response="{status=FAILED}";
		}
		return response;
	}

	//更新记录
	String updateRecord(String data) {
		Map<String, String> dataMap=stringToMap(data);
		Statement s;
		String response=new String();

		try {
			s=CalendarServer.conn.createStatement();
			String sql="UPDATE todolist SET %sWHERE memoID=%s";
			String items=new String();
			for (String key : dataMap.keySet()) {
				if(!key.contentEquals("type")&&!key.contentEquals("memoID")) {
					items+=(key+"=\'"+dataMap.get(key)+"\',");
				}
			}
			items=items.substring(0, items.length()-1)+" ";
			sql=String.format(sql,items,dataMap.get("memoID"));
			System.out.println(sql);
			s.executeUpdate(sql);

			response="{status=OK}";
			System.out.println("Server: UPDATE "+dataMap.get("memoID")+" SUCCEED");
		} catch (SQLException e) {
			e.printStackTrace();
			response="{status=FAILED}";
		}
		return response;
	}

	//删除记录
	String deleteRecord(String id) {
		PreparedStatement s;
		String response=new String();

		try {
			s=CalendarServer.conn.prepareStatement("DELETE FROM todolist WHERE memoID=?");
			s.setString(1, id);
			s.executeUpdate();

			response="{status=OK}";
			System.out.println("Server: DELETE "+id+" SUCCEED");
		} catch (SQLException e) {
			e.printStackTrace();
			response="{status=FAILED}";
		}
		return response;
	}
	
	//获取最大主键
	int getMaxKey() {
		int result=0;
		PreparedStatement s;
		try {
			s = CalendarServer.conn.prepareStatement ( 
					"SELECT max(memoID) FROM todolist");

			ResultSet rs=s.executeQuery();

			if (rs.next()) {
				result=rs.getShort(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
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
}
