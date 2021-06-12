package server.server;

import server.models.ChatRoom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 和数据库操作相关的方法类.
 * 存放数据库连接和设计数据库操作的方法
 * @see ServerThread
 * @see GetDBConnection#connectDB(String, String, String)
 */
public class SQLServer {
    private static final Connection con = GetDBConnection.connectDB("cloudchat","root","111111");

    public static Connection getCon() {
        return con;
    }

    /**
     * 验证登录信息.
     */
    public static boolean userVerifyMatch(String name,String pwd){
        try {
            String sqlModel="select * from user_info where name_user=? and pwd_user=?";
            PreparedStatement sql = con.prepareStatement(sqlModel);
            sql.setString(1,name);
            sql.setString(2,pwd);
            ResultSet r=sql.executeQuery();
            return !r.next();
        } catch (SQLException s){
            System.out.println("SQL -- 用户账号密码验证时出错");
            return false;
        }
    }

    /**
     * 验证注册时用户名存在信息.
     */
    public static boolean isUserExist(String name) {
        try {
            PreparedStatement sql=con.prepareStatement("select * from user_info where name_user=?");
            sql.setString(1,name);
            ResultSet r = sql.executeQuery();
            return r.next();
        }catch (SQLException e){
            System.out.println("SQL -- 验证用户名重复时出错");
            return false;
        }
    }

    /**
     * 验证注册时邮箱存在信息.
     */
    public static boolean isMailExist(String email) {
        try {
            PreparedStatement sql=con.prepareStatement("select * from user_info where email_user=?");
            sql.setString(1,email);
            ResultSet r = sql.executeQuery();
            return r.next();
        }catch (SQLException e){
            System.out.println("SQL -- 验证邮箱重复时出错");
            return false;
        }
    }

    /**
     * 注册信息入库.
     */
    public static boolean register(String name,String pwd, String mail){
        try {
            PreparedStatement sql=con.prepareStatement("insert into " +
                    "user_info(name_user,pwd_user,email_user, isban_user) values(?,?,?,false)");
            sql.setString(1, name);
            sql.setString(2, pwd);
            sql.setString(3, mail);
            int i = sql.executeUpdate();
            return i != 0;
        }catch (SQLException s){
            System.out.println("SQL -- 数据库用户注册时出错");
            return false;
        }
    }

    /**
     * 房间删除时的数据库信息删除.
     */
    public static void deleteRoom(String roomName){
        try {
            PreparedStatement sql1=con.prepareStatement("delete from chat_room where name_room=?");
            sql1.setString(1,roomName);
            PreparedStatement sql2=con.prepareStatement("delete from chat_room_user where name_room=?");
            sql2.setString(1,roomName);
            sql1.executeUpdate();
            sql2.executeUpdate();
        }
        catch (SQLException i){
            System.out.println("SQL -- 数据库删除房间时出错");
            i.printStackTrace();
        }
    }

    /**
     * 密码信息更新.
     */
    public static boolean updatePwd(String userName, String userPwd) {
        try {
            PreparedStatement sql=con.prepareStatement("update user_info set pwd_user=? where name_user=?");
            sql.setString(1, userPwd);
            sql.setString(2, userName);
            int flag = sql.executeUpdate();
            return flag == 1;
        } catch (SQLException s) {
            System.out.println("SQL -- 修改个人密码时出错");
            return false;
        }
    }

    /**
     * 房间名查重.
     */
    public static boolean roomNameRepeat(String RoomName){
        try {
            PreparedStatement sql = con.prepareStatement("select * from chat_room where name_room=?");
            sql.setString(1,RoomName);
            ResultSet r = sql.executeQuery();
            return r.next();
        } catch (SQLException s) {
            System.out.println("SQL -- 检测房间是否重名时出错");
            return true;
        }
    }

    /**
     * 房间信息插入.
     * @param msg 存放房间信息
     * @param userName 存放房主名
     * @param time 存放时间戳
     */
    public static boolean createRoom(String[] msg,String userName,Timestamp time){
        try {
            String roomName=msg[1];
            String pwd=msg[2];
            String description=msg[3];
            PreparedStatement sql=con.prepareStatement("insert into chat_room(name_room,pwd_room,id_master,description_room,create_time,num_room)" +
                    "values(?,?,?,?,?,1)");
            sql.setString(1,roomName);
            sql.setString(2,pwd);
            sql.setString(3,userName);
            sql.setString(4,description);
            sql.setTimestamp(5,time);
            int result = sql.executeUpdate();
            if (result != 0) System.out.println("server -- 插入聊天室成功");
        }catch (SQLException s){
            System.out.println("SQL -- 创建房间时出错");
            return false;
        }
        return true;
    }

    /**
     * 房间人数增加.
     */
    public static void addMemberNum(String RoomName){
        try {
            PreparedStatement sql=con.prepareStatement("update chat_room set num_room=num_room+1 where name_room=?");
            sql.setString(1,RoomName);
            sql.executeUpdate();
        }catch (SQLException s){
            System.out.println("SQL -- 增加房间内成员数时出错");
            s.printStackTrace();
        }
    }

    /**
     * 房间人数减少.
     */
    public static void minusMemberNum(String RoomName){
        try {
            PreparedStatement sql=con.prepareStatement("update chat_room set num_room=num_room-1 where name_room=?");
            sql.setString(1,RoomName);
            sql.executeUpdate();
        }catch (SQLException s){
            System.out.println("SQL -- 减少房间内成员时出错");
            s.printStackTrace();
        }
    }

    /**
     * 加入房间的房名密码验证.
     */
    public static boolean checkRoom(String RoomName, String Password){
        try {
            PreparedStatement sql2=con.prepareStatement("select * from chat_room where name_room=?");
            sql2.setString(1,RoomName);
            ResultSet r2=sql2.executeQuery();
            if(r2.next()){
                PreparedStatement sql1=con.prepareStatement("select * from chat_room where name_room=?");
                sql1.setString(1,RoomName);
                ResultSet r1 =sql1.executeQuery();
                r1.next();
                String pwd=r1.getString("pwd_room");
                return pwd.equals(Password);
            }else {
                return false;
            }
        }catch (SQLException s){
            System.out.println("SQL -- 验证房间信息时出错");
            return false;
        }
    }

    /**
     * 房间-用户信息插入.
     */
    public static boolean joinChatRoom(String RoomName,String userName){
        try {
            Timestamp time=new Timestamp(System.currentTimeMillis());
            PreparedStatement sql=con.prepareStatement("insert into chat_room_user(name_room,name_user,add_time) values(?,?,?)");
            sql.setString(1,RoomName);
            sql.setString(2,userName);
            sql.setTimestamp(3, time);
            int i=sql.executeUpdate();
            return i!=0;
        }catch (SQLException s){
            System.out.println("SQL -- 用户进入房间时出错");
            return false;
        }
    }

    /**
     * 退出房间时的用户-房间表更新.
     */
    public static boolean quitChatRoom(String RoomName,String UserID){
        try {
            PreparedStatement sql=con.prepareStatement("delete from chat_room_user where name_user=? and name_room=?");
            sql.setString(1,UserID);
            sql.setString(2,RoomName);
            int i = sql.executeUpdate();
            return i != 0;
        }catch (SQLException s){
            System.out.println("SQL -- 用户退出房间时出错");
            s.printStackTrace();
            return false;
         }
    }

    /**
     * 聊天记录表更新.
     */
    public static boolean chatInRoom(String[] msg,Timestamp time,String UserID,String RoomName){
        try {
            String Content=msg[1];
            PreparedStatement sql=con.prepareStatement("insert into chat_room_message(name_user,name_room,content_msg,time_msg) values(?,?,?,?)");
            sql.setString(1,UserID);
            sql.setString(2,RoomName);
            sql.setString(3,Content);
            sql.setTimestamp(4,time);
            int i=sql.executeUpdate();
            return i!=0;
        }catch (SQLException s) {
            System.out.println("SQL -- 用户在聊天室内发消息时出错");
            s.printStackTrace();
            return false;
        }
    }

    /**
     * 判断房主.
     */
    public static boolean isMaster(String roomName, String userName){
        try {
            PreparedStatement sql=con.prepareStatement("select * from chat_room where name_room=?");
            sql.setString(1, roomName);
            ResultSet r=sql.executeQuery();
            r.next();
            String master=r.getString("id_master");
            return master.equals(userName);
        } catch (SQLException s){
            System.out.println("SQL -- 判断房主时出错");
            s.printStackTrace();
            return false;
        }
    }

    /**
     * 获取房间列表.
     * @see ChatRoom
     */
    public static List<ChatRoom> getRoomList(){
        try {
            List<ChatRoom> rooms = new ArrayList<>();
            PreparedStatement sql=con.prepareStatement("select * from chat_room order by num_room desc");
            ResultSet r=sql.executeQuery();
            while(r.next()){
                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setRoomName(r.getString("name_room"));
                chatRoom.setMaster(r.getString("id_master"));
                chatRoom.setDescription(r.getString("description_room"));
                chatRoom.setNum(r.getInt("num_room"));
                chatRoom.setTime(r.getTimestamp("create_time"));
                rooms.add(chatRoom);
            }
            return rooms;
        }catch (SQLException s){
            System.out.println("SQL -- 获取所有房间时失败");
            s.printStackTrace();
            return null;
        }
    }

    /**
     * 用户封禁判断.
     */
    public static boolean userIsBan(String name){
        try {
            int isBan;
            PreparedStatement sql=con.prepareStatement("select * from user_info where name_user=?");
            sql.setString(1,name);
            ResultSet r=sql.executeQuery();
            r.next();
            isBan=r.getInt("isban_user");
            return isBan!=0;
        }catch (SQLException e){
            System.out.println("检测用户是否被ban时出错");
            return false;
        }
    }

    /**
     * 用户封禁属性开启.
     */
    public static boolean banUser(String name){
        try {
            PreparedStatement sql=con.prepareStatement("update user_info set isban_user=? where name_user=?");
            sql.setBoolean(1,true);
            sql.setString(2,name);
            int r = sql.executeUpdate();
            return r != 0;
        }catch (SQLException s){
            System.out.println("SQL -- 管理员封禁用户时出错");
            return false;
        }
    }

    /**
     * 用户封禁属性关闭.
     */
    public static boolean unlockUser(String name){
        try {
            PreparedStatement sql = con.prepareStatement("update user_info set isban_user=? where name_user=?");
            sql.setBoolean(1,false);
            sql.setString(2,name);
            int r=sql.executeUpdate();
            return r!=0;
        }catch (SQLException s){
            System.out.println("SQL -- 管理员解禁用户时出错");
            return false;
        }
    }
}
