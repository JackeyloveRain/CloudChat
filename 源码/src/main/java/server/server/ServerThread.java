package server.server;

import server.common.CommonUtil;
import server.models.ChatRoom;
import server.models.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * 用户的服务线程类.
 * 继承Thread <br>
 * 线程内循环读入用户发向服务器的请求，并调用数据库类的相关方法来处理请求，并返回结果
 */
public class ServerThread extends Thread {
    Socket socket;                  // 线程独立的套接字
    DataOutputStream out = null;    // 线程独立的输出流
    DataInputStream in = null;      // 线程独立的输入流
    User user;                      // 线程用户缓存
    ChatRoom chatRoom;              // 线程所在聊天室缓存

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.chatRoom = new ChatRoom();
        try {
            // 获得线程的输入输出流
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写线程的run方法.
     * 循环读入并处理客户端请求
     * @see CommonUtil
     * @see SQLServer
     */
    @Override
    public void run() {
        while (true) {
            try {
                String message= in.readUTF();       // 线程阻塞读
                System.out.println("server -- get message: " + message);
                String[] msg=message.split(CommonUtil.SIGNAL_SPLIT);
                switch (msg[0]) {
                    case "LOGIN" -> UserVerify(msg);
                    case "REGISTER" -> Register(msg);
                    case "BAN_USER" -> BanUser(msg);
                    case "UNLOCK_USER" -> UnlockUser(msg);
                    case "CREATEROOM" -> CreateRoom(msg);
                    case "CHAT_IN_ROOM" -> ChatInRoom(msg);
                    case "JOIN_ROOM" -> JoinChatRoom(msg);
                    case "QUIT_ROOM" -> QuitChatRoom(msg);
                    case "UPDATE_PWD" -> UpdateUserPwd(msg);
                    case "ENTER_SEARCH"-> GetRoomList();
                }
            } catch (IOException e) {
                System.out.println("server -- 用户离开");
                if (user != null) {
                    if (!user.getRoomIn().equals("")) {
                        // 用户在房间内窗口意外关闭的异常处理
                        QuitChatRoomByAccident(user.getName(), chatRoom.getRoomName());
                    } else {
                        // 用户在非房间界面窗口意外关闭的异常处理
                        MainServer.getOnlineUsers().remove(user.getName());
                    }
                }
                break;
            }
        }
    }

    /**
     * 登录信息处理方法.
     * @see CommonUtil
     * @see SQLServer#userVerifyMatch(String, String)
     * @see SQLServer#userIsBan(String)
     */
    public void UserVerify(String[] msg) {
        try {
            String name = msg[1];
            String pwd = msg[2];
            // 数据库连接异常
            if (SQLServer.getCon()==null) {
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 检测用户名密码正确性
            else if (SQLServer.userVerifyMatch(name,pwd)) {
                out.writeUTF(CommonUtil.LOGIN_ERROR);
            }
            // 检测是否被封禁
            else if (SQLServer.userIsBan(name)) {
                out.writeUTF(CommonUtil.USER_BANNED);
            }
            // 成功登录
            else {
                User tempUser = MainServer.getOnlineUsers().get(name);
                if (tempUser == null || !tempUser.getOnline()) {
                    // 返回成功信息
                    out.writeUTF(CommonUtil.ACTION_SUCCESS);
                    // 服务器端存储用户信息
                    user = new User(name, pwd, socket, in, out);
                    MainServer.getOnlineUsers().put(name,user);
                    MainServer.getOnlineUsers().get(name).setOnline(true);
                } else {
                    // 返回重复登录的信息
                    out.writeUTF(CommonUtil.LOGIN_REPEAT);
                }
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 注册信息处理方法.
     * @see CommonUtil
     * @see SQLServer#isUserExist(String)
     * @see SQLServer#isMailExist(String)
     * @see SQLServer#register(String, String, String)
     */
    public void Register(String[] msg) {
        try {
            String name=msg[1];
            String pwd=msg[2];
            String mail=msg[3];
            // 数据库连接异常
            if(SQLServer.getCon()==null){
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 用户名存在判断
            else if(SQLServer.isUserExist(name)) {
                out.writeUTF(CommonUtil.REGISTER_USER_ERROR);
            }
            // 邮箱重复判断
            else if(SQLServer.isMailExist(mail)) {
                out.writeUTF(CommonUtil.REGISTER_MAIL_ERROR);
            }
            // 插入信息判断
            else if(SQLServer.register(name,pwd,mail)){
                out.writeUTF(CommonUtil.ACTION_SUCCESS);
            }
            // 返回错误信息
            else {
                out.writeUTF(CommonUtil.REGISTER_ERROR);
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 创建房间请求处理方法.
     * @see CommonUtil
     * @see SQLServer#roomNameRepeat(String)
     * @see SQLServer#createRoom(String[], String, Timestamp)
     * @see SQLServer#joinChatRoom(String, String)
     */
    public void CreateRoom(String[] msg){
        try {
            String UserID=user.getName();
            String roomName=msg[1];
            String pwd=msg[2];
            String description=msg[3];
            Timestamp time=new Timestamp(System.currentTimeMillis());
            // 数据库连接异常
            if (SQLServer.getCon() == null) {
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 房间名称重复判断
            else if(SQLServer.roomNameRepeat(roomName)){
                out.writeUTF(CommonUtil.ROOM_NAME_REPEAT);
            }
            // 插入信息判断
            else if(SQLServer.createRoom(msg,UserID,time)){
                out.writeUTF(CommonUtil.ACTION_SUCCESS);
                chatRoom=new ChatRoom(roomName,pwd,UserID,description);
                user.setRoomIn(roomName);       // 线程用户缓存更新
                chatRoom.getUsersInRoom().add(UserID);      // 线程用户所在房间缓存更新
                MainServer.getRooms().put(roomName, chatRoom);      // 服务器房间缓存更新
                MainServer.getOnlineUsers().get(UserID).setRoomIn(roomName);        // 服务器用户缓存更新
                SQLServer.joinChatRoom(chatRoom.getRoomName(),UserID);   // 数据库更新
            }
            else {
                out.writeUTF(CommonUtil.CREATEROOM_ERROR);
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 发送聊天室消息处理方法.
     * @see CommonUtil
     * @see SQLServer#userIsBan(String)
     * @see SQLServer#chatInRoom(String[], Timestamp, String, String)
     */
    public void ChatInRoom(String[] msg){
        try {
            String UserID=user.getName();
            String RoomName=chatRoom.getRoomName();
            Timestamp time=new Timestamp(System.currentTimeMillis());
            // 数据库连接判断
            if (SQLServer.getCon()==null) {
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 用户被封禁判断
            else if (SQLServer.userIsBan(UserID)) {
                out.writeUTF(CommonUtil.USER_BANNED);
            }
            // 用户聊天信息判断及发送
            else if (SQLServer.chatInRoom(msg,time,UserID,RoomName)){
                HashMap<String,User> tempMap = MainServer.getOnlineUsers();
                // 遍历在此房间的用户并发送消息
                for(String str : tempMap.keySet()) {
                    if (tempMap.get(str).getRoomIn().equals(RoomName)) {
                        String message = CommonUtil.SIGNAL_CHAT_IN_ROOM + CommonUtil.SIGNAL_SPLIT
                                + UserID + CommonUtil.SIGNAL_SPLIT
                                + msg[1] + CommonUtil.SIGNAL_SPLIT
                                + time + CommonUtil.SIGNAL_SPLIT
                                + CommonUtil.SIGNAL_END;
                        tempMap.get(str).getOutputStream().writeUTF(message);
                    }
                }
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 处理封禁用户请求的方法.
     * @see CommonUtil
     * @see SQLServer#banUser(String)
     */
    public void BanUser(String[] msg){
        try {
            String name=msg[1];
            // 数据库连接判断
            if (SQLServer.getCon()==null){
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 封禁用户成功判断
            else if (SQLServer.banUser(name)){
                out.writeUTF(CommonUtil.ACTION_SUCCESS);
            }
            // 返回错误消息
            else {
                out.writeUTF(CommonUtil.BAN_USER_ERROR);
            }
        } catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 处理解封用户请求的方法.
     * @see CommonUtil
     * @see SQLServer#banUser(String)
     */
    public void UnlockUser(String[] msg){
        try {
            String name=msg[1];
            // 数据库连接判断
            if (SQLServer.getCon()==null) {
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 解封成功判断
            else if (SQLServer.unlockUser(name)) {
                out.writeUTF(CommonUtil.ACTION_SUCCESS);
            }
            // 返回错误信息
            else {
                out.writeUTF(CommonUtil.UNLOCK_USER_ERROR);
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * 用户进入聊天室请求的处理方法.
     * @see CommonUtil
     * @see SQLServer#checkRoom(String, String)
     * @see SQLServer#joinChatRoom(String, String)
     * @see SQLServer#addMemberNum(String)
     */
    public void JoinChatRoom(String[] msg){
        try {
            String RoomName=msg[1];
            String Password=msg[2];
            String UserID=user.getName();
            // 数据库连接判断
            if(SQLServer.getCon()==null){
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 房间名及密码正确性判断
            else if(!SQLServer.checkRoom(RoomName,Password)){
                out.writeUTF(CommonUtil.ROOM_INFO_ERROR);
            }
            // 加入成功判断
            else if(SQLServer.joinChatRoom(RoomName,UserID)){
                // 数据库房间人数更新
                SQLServer.addMemberNum(RoomName);
                // 线程缓存更新
                chatRoom.setRoomName(RoomName);
                chatRoom.getUsersInRoom().add(UserID);
                user.setRoomIn(RoomName);
                // 服务器缓存更新
                MainServer.getOnlineUsers().get(UserID).setRoomIn(RoomName);
                MainServer.getRooms().get(RoomName).getUsersInRoom().add(UserID);
                out.writeUTF(CommonUtil.ACTION_SUCCESS);
            }
            // 返回错误信息
            else {
                out.writeUTF(CommonUtil.JOIN_ROOM_ERROR);
            }
        }catch (IOException i){
            System.out.println("用户进入聊天室时发生错误");
            i.printStackTrace();
        }
    }

    /**
     * 获取当前全部聊天室信息.
     * @see CommonUtil
     * @see SQLServer#getRoomList()
     */
    public void GetRoomList() {
        try {
            List<ChatRoom> rooms = SQLServer.getRoomList();
            if ( rooms != null && rooms.size() != 0) {
                // 设置开始列表的信号
                out.writeUTF(CommonUtil.SIGNAL_LIST_START);
                System.out.println("server -- send message: " + CommonUtil.SIGNAL_LIST_START);
                // 开始传输列表
                for (ChatRoom room : rooms) {
                    String RoomName = room.getRoomName();
                    Integer RoomNum = room.getNum();
                    String Master = room.getMaster();
                    String Description=room.getDescription();
                    Timestamp time = room.getTime();
                    //封装信息
                    String message = CommonUtil.SIGNAL_ROOM_LIST + CommonUtil.SIGNAL_SPLIT +
                            RoomName + CommonUtil.SIGNAL_SPLIT +
                            Description+CommonUtil.SIGNAL_SPLIT+
                            Master + CommonUtil.SIGNAL_SPLIT +
                            RoomNum + CommonUtil.SIGNAL_SPLIT +
                            time + CommonUtil.SIGNAL_SPLIT + CommonUtil.SIGNAL_END;
                    //发送信息
                    out.writeUTF(message);
                    System.out.println("server -- send message: " + message);
                }
                out.writeUTF(CommonUtil.SIGNAL_LIST_END);    //设置结束列表信号
                out.flush();
                System.out.println("server -- send message " + CommonUtil.SIGNAL_LIST_END);
            } else {
                out.writeUTF(CommonUtil.ROOM_LIST_EMPTY);
                System.out.println("server -- send message: " + CommonUtil.ROOM_LIST_EMPTY);
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 用户自然退出房间请求.
     * @see CommonUtil
     * @see SQLServer#quitChatRoom(String, String)
     * @see SQLServer#isMaster(String, String)
     * @see SQLServer#deleteRoom(String)
     * @see SQLServer#minusMemberNum(String)
     */
    public void QuitChatRoom(String[] msg){
        try {
            String RoomName = msg[1];
            String UserID = msg[2];
            // 连接异常
            if (SQLServer.getCon()==null){
                System.out.println(CommonUtil.DATABASE_ERROR);
            }
            // 执行退出
            else if (SQLServer.quitChatRoom(RoomName,UserID)) {
                if (SQLServer.isMaster(RoomName, UserID)) {
                    // 删除关联数据库
                    SQLServer.deleteRoom(RoomName);
                    // 删除服务器端缓存信息
                    ChatRoom hostRoom = MainServer.getRooms().get(RoomName);
                    for (String userName : hostRoom.getUsersInRoom()) {
                        MainServer.getOnlineUsers().get(userName).setRoomIn("");
                        // 返回结束信息
                        if (!userName.equals(UserID)) {
                            // 向房间内非房主成员发送房间关闭的信息
                            MainServer.getOnlineUsers().get(userName).getOutputStream().writeUTF(CommonUtil.ROOM_CLOSED);
                        } else {
                            // 向房主本人发送自然关闭房间信息
                            MainServer.getOnlineUsers().get(userName).getOutputStream().writeUTF(CommonUtil.QUIT_ROOM_NORMAL);
                        }
                    }
                    // 清空服务器缓存的房间信息
                    MainServer.getRooms().remove(RoomName);
                    // 删除关联客户线程缓存
                    chatRoom.getUsersInRoom().clear();
                }
                else {
                    SQLServer.minusMemberNum(RoomName);     // 减少房间人数
                    // 从该房间房间成员列表移出
                    MainServer.getRooms().get(RoomName).getUsersInRoom().remove(UserID);
                    MainServer.getOnlineUsers().get(UserID).setRoomIn("");
                    MainServer.getOnlineUsers().get(UserID).getOutputStream().writeUTF(CommonUtil.QUIT_ROOM_NORMAL);
                }
                user.setRoomIn("");     // 线程缓存更新
                chatRoom.getUsersInRoom().remove(UserID); // 线程缓存更新
                System.out.println("server -- quit chatting" +"CommonUtil.ACTION_SUCCESS");      // 输出调试信息
            }
            else {
                System.out.println(CommonUtil.QUIT_ROOM_ERROR);
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    /**
     * 房间窗口意外关闭的处理方法.
     * @see CommonUtil
     * @see SQLServer#quitChatRoom(String, String)
     * @see SQLServer#isMaster(String, String)
     * @see SQLServer#deleteRoom(String)
     * @see SQLServer#minusMemberNum(String)
     */
    private void QuitChatRoomByAccident(String userName, String roomName) {
        // 连接异常
        if (SQLServer.getCon() == null) {
            System.out.println("server -- warning: " + "用户非自然退出时发生了" + CommonUtil.DATABASE_ERROR);
        }
        // 执行退出
        else if (SQLServer.quitChatRoom(roomName, userName)) {
            if (SQLServer.isMaster(roomName, userName)) {
                // 删除关联数据库
                SQLServer.deleteRoom(roomName);
                // 删除服务器端缓存信息
                ChatRoom hostRoom = MainServer.getRooms().get(roomName);
                for (String user : hostRoom.getUsersInRoom()) {
                    if (!user.equals(userName)) {
                        MainServer.getOnlineUsers().get(user).setRoomIn("");
                        try {
                            MainServer.getOnlineUsers().get(user).getOutputStream().writeUTF(CommonUtil.ROOM_CLOSED);
                        } catch (IOException e) {
                            System.out.println("server -- warning: " + "房主非自然退出时发送消息异常");
                            e.printStackTrace();
                        }
                    }
                }
                MainServer.getRooms().remove(roomName);     // 清空服务器缓存的房间信息
            }
            else {
                // 从该房间房间成员列表移出
                MainServer.getRooms().get(roomName).getUsersInRoom().remove(userName);
                SQLServer.minusMemberNum(roomName);     // 减少房间人数
            }
            MainServer.getOnlineUsers().remove(userName);    // 清空服务器缓存的用户信息
            System.out.println("server -- quit chatting" + "CommonUtil.ACTION_SUCCESS");      // 输出调试信息
        }
    }

    /**
     * 修改密码请求的处理方法.
     * @see CommonUtil
     * @see SQLServer#updatePwd(String, String)
     */
    public void UpdateUserPwd(String[] msg) {
        try {
            String userName = user.getName();
            String userPwd = msg[1];
            // 检测是否连接
            if (SQLServer.getCon()==null){
                out.writeUTF(CommonUtil.DATABASE_ERROR);
            }
            // 成功更新密码检测
            else if (SQLServer.updatePwd(userName, userPwd)){
                out.writeUTF(CommonUtil.ACTION_SUCCESS);
            }
            // 返回错误信息
            else {
                out.writeUTF(CommonUtil.UPDATE_INFO_ERROR);
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }
}

