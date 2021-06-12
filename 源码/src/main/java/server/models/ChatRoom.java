package server.models;

import java.sql.Timestamp;
import java.util.ArrayList;


/**
 * 聊天室类.
 * 用于服务器端构建房间信息缓存 <br>
 * 可以快速获取房间信息而不用与服务器端交互
 */
public class ChatRoom {
    private String RoomName;
    private String PWD;
    private String Master;
    private Integer Num;
    private Timestamp time;
    private String description;
    private final ArrayList<String> usersInRoom;        // 存放此房间内的用户

    public ChatRoom() {
        usersInRoom = new ArrayList<>();
    }

    public ChatRoom(String RoomName,String PWD,String Master,String description){
        this.RoomName=RoomName;
        this.PWD=PWD;
        this.Master=Master;
        this.description=description;
        usersInRoom = new ArrayList<>();
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }

    public String getMaster() {
        return Master;
    }

    public void setMaster(String master) {
        Master = master;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getUsersInRoom() {
        return usersInRoom;
    }

    public Integer getNum() {
        return Num;
    }

    public void setNum(Integer num) {
        Num = num;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
