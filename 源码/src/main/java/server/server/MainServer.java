package server.server;

import server.common.CommonUtil;
import server.models.ChatRoom;
import server.models.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * 服务器端入口.
 * 服务器的属性包括了在线用户表、在线房间表
 * @see User
 * @see ChatRoom
 * @see ServerThread
 * @see CommonUtil
 */
public class MainServer {
    private static HashMap<String, User> onlineUsers;       // 在线用户的HashMap
    private static HashMap<String, ChatRoom> rooms;         // 建立房间的HashMap

    public MainServer() {
        onlineUsers = new HashMap<>();
        rooms = new HashMap<>();
    }

    /**
     * 入口方法.
     */
    public static void main(String[] args) {
        MainServer mainServer = new MainServer();
        mainServer.start();
    }

    /**
     * 服务器启动方法
     */
    public void start() {
        // 设置套接字
        ServerSocket sc = null;
        Socket userSocket = null;
        while (true) {
            try {
                sc = new ServerSocket(CommonUtil.PORT);
            } catch (IOException e1) {
                System.out.println("=======CloudChat 服务终端=======");
            }
            try {
                System.out.println("server -- 等待用户连接");
                if (sc != null) {
                    // 获得用户套接字
                    userSocket = sc.accept();
                } else {
                    System.out.println("server -- error: PORT_ERROR");
                    break;
                }
            } catch (IOException e2) {
                System.out.println("ServerSocket等待连接");
            }
            if (userSocket != null) {
                System.out.println("用户" + userSocket.toString() + "连接了服务器端");
                // 为此用户启动一个服务线程
                new ServerThread(userSocket).start();
            }
        }
    }

    public static HashMap<String, User> getOnlineUsers() {
        return onlineUsers;
    }

    public static HashMap<String, ChatRoom> getRooms() {
        return rooms;
    }
}