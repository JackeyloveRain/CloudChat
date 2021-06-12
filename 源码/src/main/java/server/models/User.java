package server.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 用户类.
 * 用于服务器端构建用户信息缓存 <br>
 * 可以快速获取用户信息而不用与服务器端交互
 */
public class User {
    private String name;
    private String pwd;
    private Boolean isBan = false;
    private Boolean isOnline = false;
    private String roomIn;      // 用户所在的房间
    private Socket userSocket;      // 用户的套接字
    private DataInputStream inputStream;        // 用户的输入流
    private DataOutputStream outputStream;      // 用户的输出流

    public User(String name, String pwd, Socket userSocket, DataInputStream inputStream, DataOutputStream outputStream) {
        this.pwd = pwd;
        this.name = name;
        this.roomIn = "";
        this.userSocket = userSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public User() {
        this.roomIn = "";
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBan() {
        return isBan;
    }

    public void setBan(Boolean ban) {
        isBan = ban;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public void setUserSocket(Socket userSocket) {
        this.userSocket = userSocket;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public String getRoomIn() {
        return roomIn;
    }

    public void setRoomIn(String roomIn) {
        this.roomIn = roomIn;
    }
}
