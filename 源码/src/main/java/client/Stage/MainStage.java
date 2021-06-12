package client.Stage;
import client.Main;
import client.Stage.PWDAlert;
import client.cache.RoomIn;
import client.Tools.PreProcessingTools;
import client.Tools.ReadRoomMsg;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static client.Tools.CommonUtil.*;

/**
 * 主界面类.
 * 主要工作如下 <br>
 * 1 加载用户界面 <br>
 * 2 处理用户输入 <br>
 * 3 与服务器端进行传输 <br>
 * 4 根据收到的数据渲染页面
 */
public class MainStage implements Initializable {
    @FXML
    private ImageView headImg = new ImageView();
    @FXML
    private ImageView chatIcon = new ImageView();
    @FXML
    private ImageView searchIcon = new ImageView();
    @FXML
    private ImageView addIcon = new ImageView();
    @FXML
    private Label roomTitle;
    @FXML
    private TextField roomAddPwd;
    @FXML
    private TextField roomAddName;
    @FXML
    private TextArea roomAddDescription;
    @FXML
    private Label roomNameErrorMsg;
    @FXML
    private Label roomPwdErrorMsg;
    @FXML
    private FlowPane chatMsgArea;
    @FXML
    private TextArea inputMsgArea;
    @FXML
    private Label sendMsgErrorLabel;
    @FXML
    private FlowPane chatRoomList;
    @FXML
    private TabPane navBar;
    @FXML
    private Tab chatList;
    @FXML
    private TextField searchNameInput;
    @FXML
    private TextField searchPwdInput;
    @FXML
    private Label searchNameError;
    @FXML
    private Label searchPwdError;
    @FXML
    private AnchorPane chatPane1;
    @FXML
    private AnchorPane chatPane2;
    @FXML
    private Tab search;
    @FXML
    private Tab create;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Tab homePage;
    @FXML
    private AnchorPane quitTip;
    @FXML
    private ScrollPane scroll;
    @FXML
    private ImageView homeBG;
    @FXML
    private ImageView chattingBG;
    @FXML
    private ImageView chatBG;
    @FXML
    private ImageView createBG;
    @FXML
    private ImageView searchBG;
    @FXML
    private Label roomListEmptyLabel;
    //加载图片
    Image homeBg=new Image("IMG/bg.png",939,600,false,false);
    Image chattingBg=new Image("IMG/bg3.png");
    Image chatBg=new Image("IMG/bg1.png");
    Image createBg=new Image("IMG/bg2.png");
    Image searchBg=new Image("IMG/bg2.png");
    Image chat1=new Image("IMG/chat1.png");
    Image chat2=new Image("IMG/chat2.png");
    Image sea1=new Image("IMG/search1.png");
    Image sea2=new Image("IMG/search2.png");
    Image add1=new Image("IMG/add1.png");
    Image add2=new Image("IMG/add2.png");
    Image user1=new Image("IMG/user1.png");
    Image user2=new Image("IMG/user2.png");


    /**
     * 初始化.
     */
    public void initialize(URL location, ResourceBundle resources){
        scroll.setVvalue(1);
        homeBG.setImage(homeBg);
        chatBG.setImage(chatBg);
        chattingBG.setImage(chattingBg);
        createBG.setImage(createBg);
        searchBG.setImage(searchBg);
        inputMsgArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    submitChatMsg();
                }
            }
        });
        //初始化各个图标
        addIcon.setImage(add1);
        headImg.setImage(user2);
        searchIcon.setImage(sea1);
        chatIcon.setImage(chat1);
        welcomeLabel.setText("亲爱的 " + Main.getClient().getName() + "，欢迎进入云端聊天室！");
    }

    /**
     * “新建房间”的事件处理.
     * @see ReadRoomMsg
     * @see #clearCreateTable()
     */
    @FXML
    public void submitRoomInfo() {
        // 获取输入框数据
        String name = roomAddName.getText();
        String pwd = roomAddPwd.getText();
        String description = roomAddDescription.getText();
        System.out.println("client -- create room: " + name + " " + pwd);

        // 数据预处理
        boolean isLegal = true;
        if (name.equals("")) {
            roomNameErrorMsg.setText("聊天室名不能为空");
            roomNameErrorMsg.setVisible(true);
            isLegal = false;
        } else if (!PreProcessingTools.isRoomNameLegal(name)) {
            roomNameErrorMsg.setText("聊天室名必须在4~16位之间");
            roomNameErrorMsg.setVisible(true);
            isLegal = false;
        } else {
            roomNameErrorMsg.setVisible(false);
        }
        if (pwd.equals("")) {
            roomPwdErrorMsg.setText("聊天室密码不能为空");
            roomPwdErrorMsg.setVisible(true);
            isLegal = false;
        } else if (!PreProcessingTools.isRoomPwdLegal(pwd)) {
            roomPwdErrorMsg.setText("聊天室密码必须是4~8位数字");
            roomPwdErrorMsg.setVisible(true);
            isLegal = false;
        } else {
            roomPwdErrorMsg.setVisible(false);
        }
        if (!isLegal) {
            return;
        }

        // 发送至客户端
        DataInputStream in = Main.getIn();
        DataOutputStream out = Main.getOut();
        try {
            // 获得服务器信息
            out.writeUTF(SIGNAL_CREATEROOM + SIGNAL_SPLIT
                    + name + SIGNAL_SPLIT
                    + pwd + SIGNAL_SPLIT
                    + description + SIGNAL_SPLIT
                    + SIGNAL_END);
            String state = in.readUTF();
            System.out.println(state);
            // 服务器信息响应
            if (state.equals(ACTION_SUCCESS)) {
                // 记录房间信息
                Main.setRoomInfo(new RoomIn(name, description));
                ReadRoomMsg read = new ReadRoomMsg();
                read.setIn(Main.getIn());
                new Thread(read).start();       // 聊天线程开始
                // 此处跳转并且清空信息表
                roomTitle.setText(Main.getRoomIn().getName());
                clearCreateTable();
                inputMsgArea.setText("");
                chatMsgArea.getChildren().clear();
                Main.getClient().setChatting(true);
                navBar.getSelectionModel().select(chatList);
            } else if (state.equals(ROOM_NAME_REPEAT)) {
                roomNameErrorMsg.setText("该房间名已被使用");
                roomNameErrorMsg.setVisible(true);
            } else if (state.equals(DATABASE_ERROR)) {
                roomNameErrorMsg.setText("数据库异常，无法创建");
                roomNameErrorMsg.setVisible(true);
            } else {
                roomNameErrorMsg.setText("创建房间时发生未知错误");
                roomNameErrorMsg.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * “搜索加入房间”的事件处理.
     * @see ReadRoomMsg
     * @see #clearCreateTable()
     */
    @FXML
    public void searchRoom(){
        // 获取输入框数据
        String roomName = searchNameInput.getText();
        String roomPwd = searchPwdInput.getText();
        System.out.println("client -- want search room: " + roomName);

        // 数据预处理
        boolean isLegal = true;
        if (roomName.equals("")) {
            searchNameError.setText("房间名不能为空");
            searchNameError.setVisible(true);
            isLegal = false;
        }
        else {
            searchNameError.setText("");
            searchNameError.setVisible(false);
        }
        if (roomPwd.equals("")) {
            searchPwdError.setText("密码不能为空");
            searchPwdError.setVisible(true);
            isLegal = false;
        } else {
            searchPwdError.setText("");
            searchPwdError.setVisible(false);
        }
        if (!isLegal) {
            return;
        }

        // 发送至客户端
        DataInputStream in = Main.getIn();
        DataOutputStream out = Main.getOut();
        try {
            // 获得服务器信息
            out.writeUTF( SIGNAL_JOIN_ROOM + SIGNAL_SPLIT + roomName + SIGNAL_SPLIT + roomPwd + SIGNAL_SPLIT + SIGNAL_END);
            String state = in.readUTF();
            System.out.println("client -- get search reply: " + state);
            if (state.equals(ACTION_SUCCESS)) {
                searchNameError.setVisible(false);
                ReadRoomMsg read = new ReadRoomMsg();
                read.setIn(Main.getIn());
                new Thread(read).start();
                // 此处跳转并清空表
                clearSearchTable();
                inputMsgArea.setText("");
                chatMsgArea.getChildren().clear();
                Main.getClient().setChatting(true);
                Main.getRoomIn().setName(roomName);
                roomTitle.setText(roomName);
                navBar.getSelectionModel().select(chatList);
            }
            else if(state.equals(DATABASE_ERROR)) {
                searchNameError.setText("数据库连接错误");
                searchNameError.setVisible(true);
            } else if (state.equals(ROOM_INFO_ERROR)) {
                searchNameError.setText("房间名或密码错误");
                searchNameError.setVisible(true);
            } else {
                searchNameError.setText("发生未知错误，无法加入房间");
                searchNameError.setVisible(true);
            }
        } catch (IOException e) {
            System.out.println("client -- get error: 发送消息失败");
        }
    }

    /**
     * “发送消息”的事件处理.
     */
    @FXML
    public void submitChatMsg() {
        // 获取输入框数据
        String content = inputMsgArea.getText();
        inputMsgArea.clear();
        System.out.println("client -- want send msg: " + content);

        // 数据预处理
        boolean isLegal = true;
        content = content.replace("\n", "");
        if (content.equals("")) {
            sendMsgErrorLabel.setText("发送内容不能为空");
            sendMsgErrorLabel.setVisible(true);
            isLegal = false;
        } else if (content.length() > 500) {
            sendMsgErrorLabel.setText("发送消息过长，请分条发送");
            sendMsgErrorLabel.setVisible(true);
            isLegal = false;
        } else {
            inputMsgArea.setText("");
            sendMsgErrorLabel.setVisible(false);
        }
        if (!isLegal) {
            return;
        }

        // 发送至客户端
        DataOutputStream out = Main.getOut();
        try {
            out.writeUTF(SIGNAL_CHAT_IN_ROOM + SIGNAL_SPLIT + content + SIGNAL_SPLIT + SIGNAL_END);
        } catch (IOException e) {
            System.out.println("client -- error: 发送消息失败");
        }
    }

    /**
     * “退出房间”的事件处理.
     * @see #enable()
     */
    @FXML
    public void quitRoom(){
        // 面板处理
        chatMsgArea.getChildren().clear();
        enable();       // 设置能选中其他的导航按钮
        // 数据预处理
        String userName = Main.getClient().getName();
        String roomName = Main.getRoomIn().getName();
        String msg = SIGNAL_QUIT_ROOM + SIGNAL_SPLIT
                + roomName  + SIGNAL_SPLIT
                + userName  + SIGNAL_SPLIT
                + SIGNAL_END;
        System.out.println("client -- want send message: " + msg);
        // 发送至客户端
        DataOutputStream out = Main.getOut();
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("client -- error: 发送消息失败");
        }
    }

    /**
     * “获取房间列表”的事件处理.
     * @see #copyName(String)
     */
    @FXML
    public void getRoomList() {
        // 发送请求
        DataInputStream in = Main.getIn();
        DataOutputStream out = Main.getOut();
        try {
            // 获得服务器信息
            out.writeUTF( SIGNAL_ENTER_SEARCH + SIGNAL_SPLIT + SIGNAL_END);
            String state = in.readUTF();
            System.out.println("client -- get room_list reply: " + state);
            if (state.equals(SIGNAL_LIST_START)) {
                // 房间表单元素内部类
                class RoomItem {
                    final String name;
                    final String description;
                    final String master;
                    final String cnt;
                    final String time;

                    public RoomItem(String name, String description, String master, String cnt, String time) {
                        this.name = name;
                        this.description = description;
                        this.master = master;
                        this.cnt = cnt;
                        this.time = time;
                    }
                }
                ArrayList<RoomItem> roomList = new ArrayList<>();
                // 循环读入
                String msg;
                while (true) {
                    msg = in.readUTF();
                    if (msg.equals(SIGNAL_LIST_END)) {
                        break;
                    }
                    // 消息打包
                    String[] items = msg.split(SIGNAL_SPLIT);
                    String nameRoom = items[1];
                    String descriptionRoom = items[2];
                    String masterRoom = items[3];
                    String cntRoom = items[4];
                    String timeRoom =items[5].substring(0,19);
                    // 调试
                    System.out.println(
                            "client -- get message: "
                            + nameRoom + " "
                            + descriptionRoom + " "
                            + masterRoom + " "
                            + cntRoom + " "
                            + timeRoom + " "
                    );
                    roomList.add(new RoomItem(nameRoom, descriptionRoom, masterRoom, cntRoom, timeRoom));
                }
                // 界面渲染房间列表
                chatRoomList.getChildren().clear();
                if (roomList.size()==0){
                    roomListEmptyLabel.setVisible(true);
                }else {
                    for (RoomItem roomItem:roomList){
                        roomListEmptyLabel.setVisible(false);
                        HBox room=new HBox();
                        Label label1 = new Label("房间名："+roomItem.name);
                        label1.setPrefWidth(250);
                        label1.setMinWidth(250);
                        label1.setMaxWidth(250);
                        if (roomItem.description.equals("")) {
                            label1.setTooltip(new Tooltip("该房间无描述"));
                        }else label1.setTooltip(new Tooltip(roomItem.description));
                        Label label2 = new Label("房主："+roomItem.master);
                        label2.setPrefWidth(250);
                        label2.setMinWidth(250);
                        label2.setMaxWidth(250);
                        if (roomItem.description.equals("")) {
                            label2.setTooltip(new Tooltip("该房间无描述"));
                        }else label2.setTooltip(new Tooltip(roomItem.description));
                        Label label3 = new Label("房间人数："+roomItem.cnt);
                        label3.setPrefWidth(150);
                        label3.setMinWidth(150);
                        label3.setMaxWidth(150);
                        if (roomItem.description.equals("")) {
                            label3.setTooltip(new Tooltip("该房间无描述"));
                        }else label3.setTooltip(new Tooltip(roomItem.description));

                        Label label4=new Label("点击将房间名复制到输入框");
                        label4.setId("copyLink");
                        label4.setOnMouseClicked(new EventHandler<MouseEvent>(){
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                copyName(roomItem.name);
                            }
                        });
                        room.setPadding(new Insets(3));
                        room.getChildren().addAll(label1,label2,label3,label4);
                        chatRoomList.getChildren().add(room);
                    }
                }

            }
        } catch (IOException e) {
            System.out.println("client -- error: 获得房间列表失败");
        }
    }

    /**
     * “退出系统”的事件处理.
     */
    @FXML
    public void quitSystem(){
        System.exit(0);
    }

    /**
     * “修改密码”的事件处理.
     * @see PWDAlert#modifyPwd()
     */
    @FXML
    public void changePWD() throws IOException {
        PWDAlert.display();
    }

    /**
     * “进入主页”的事件处理.
     */
    @FXML
    public void enterHome(){
        headImg.setImage(user2);
        searchIcon.setImage(sea1);
        chatIcon.setImage(chat1);
        addIcon.setImage(add1);
    }

    /**
     * “进入聊天界面”的事件处理.
     * @see #disable()
     */
    @FXML
    public void enterChat(){
        quitTip.setVisible(false);
        chatPane1.setDisable(false);
        chatIcon.setImage(chat2);
        searchIcon.setImage(sea1);
        addIcon.setImage(add1);
        headImg.setImage(user1);
        // 如果在聊，就进去聊
        if (Main.getClient().getChatting()){
            //设置不能选中其他的东西
            disable();
            chatPane1.setVisible(true);
            chatPane2.setVisible(false);
        }
        // 没在聊，就显示按钮
        else{
            chatPane1.setVisible(false);
            chatPane2.setVisible(true);
        }
    }

    /**
     * “进入搜索界面”的事件处理.
     */
    @FXML
    public void enterSearch(){
        searchIcon.setImage(sea2);
        chatIcon.setImage(chat1);
        addIcon.setImage(add1);
        headImg.setImage(user1);
    }

    /**
     * “进入创建房间界面”的事件处理.
     */
    @FXML
    public void enterAdd(){
        addIcon.setImage(add2);
        searchIcon.setImage(sea1);
        chatIcon.setImage(chat1);
        headImg.setImage(user1);
    }

    /**
     * “清空按钮”的事件处理.
     */
    @FXML
    public void clearInput() {
        inputMsgArea.setText("");
    }

    /**
     * 导航.
     * @see #enterSearch()
     */
    @FXML
    public void toSearchRoom(){
        navBar.getSelectionModel().select(search);
    }

    /**
     * 导航.
     * @see #enterChat()
     */
    @FXML
    public void toChat(){
        navBar.getSelectionModel().select(chatList);
    }

    /**
     * 导航.
     * @see #enterAdd()
     */
    @FXML
    public void toCreateRoom(){
        navBar.getSelectionModel().select(create);
    }

    // 上述函数引用的方法
    public void disable(){
        homePage.setDisable(true);
        search.setDisable(true);
        create.setDisable(true);
        headImg.setVisible(false);
        searchIcon.setVisible(false);
        addIcon.setVisible(false);
    }
    public void enable(){
        homePage.setDisable(false);
        search.setDisable(false);
        create.setDisable(false);
        headImg.setVisible(true);
        searchIcon.setVisible(true);
        addIcon.setVisible(true);
    }
    public void clearCreateTable(){
        roomAddDescription.setText("");
        roomAddName.setText("");
        roomAddPwd.setText("");
    }
    public void copyName(String name){
        searchNameInput.setText(name);
    }
    public void clearSearchTable(){
        searchNameInput.setText("");
        searchPwdInput.setText("");
    }

    // getter
    public FlowPane getChatMsgArea() {
        return chatMsgArea;
    }

    public AnchorPane getQuitTip() {
        return quitTip;
    }

    public TabPane getNavBar() {
        return navBar;
    }

    public Tab getChatList() {
        return chatList;
    }

    public Tab getCreate() {
        return create;
    }

    public AnchorPane getChatPane1() {
        return chatPane1;
    }

    public ScrollPane getScroll() {
        return scroll;
    }
}
