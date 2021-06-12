package client.Tools;

import client.Main;
import client.Stage.MainStage;
import client.Stage.PrimaryStage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.Timestamp;

import static client.Tools.CommonUtil.*;

/**
 * 读聊天信息的线程接口.
 */
public class ReadRoomMsg implements Runnable {
    DataInputStream in;
    public void run() {
        System.out.println("client -- 用户聊天线程开始");
        String message;
        String fromName;
        String msgContent;
        Timestamp time;
        while(true) {
            try {
                message = in.readUTF();
                System.out.println("client -- chat thread -- get message: " + message);
                if (message.equals(QUIT_ROOM_NORMAL)) {
                    Main.getClient().setChatting(false);
                    // 更新页面
                    MainStage mainStage = PrimaryStage.getMainFxmlLoader().getController();
                    mainStage.getNavBar().getSelectionModel().select(mainStage.getCreate());
                    mainStage.getNavBar().getSelectionModel().select(mainStage.getChatList());
                    System.out.println("client -- 用户聊天线程终止");
                    break;
                } else if (message.equals(ROOM_CLOSED)) {
                    Main.getClient().setChatting(false);
                    // 更新页面
                    MainStage mainStage = PrimaryStage.getMainFxmlLoader().getController();
                    Label label =new Label("房主已经解散该房间，请创建或加入新的房间。");
                    label.setTextFill(Color.rgb(255,0,0));
                    Platform.runLater(()->{
                        mainStage.getChatPane1().setDisable(true);
                        mainStage.getQuitTip().setVisible(true);
                        mainStage.enable();
                    });
                    System.out.println("client -- 用户聊天线程终止");
                    break;
                }
                else {
                    // 预处理服务器端信息
                    String[] msgInfo = message.split(SIGNAL_SPLIT);
                    fromName = msgInfo[1];
                    msgContent = msgInfo[2];
                    time = Timestamp.valueOf(msgInfo[3]);
                    // 渲染单条消息
                    MainStage mainStage=PrimaryStage.getMainFxmlLoader().getController();
                    // 一个消息的垂直框
                    VBox msg=new VBox();
                    HBox msgHead=new HBox();
                    // 时间
                    Label toSentTime=new Label("[ "+time.toString().substring(0,19)+" ] ");
                    toSentTime.setTextFill(Color.rgb(145,208,229));
                    toSentTime.setPadding(new Insets(3));
                    // 用户名
                    Label toSentName=new Label(fromName+" ");
                    toSentName.setTextFill(Color.rgb(0,168,222));
                    toSentName.setPadding(new Insets(3));
                    msgHead.getChildren().addAll(toSentName,toSentTime);
                    // 消息主体
                    Label toSent =new Label(msgContent);
                    toSent.setPadding(new Insets(3));
                    toSent.setPrefWidth(870);
                    toSent.setMaxWidth(870);
                    toSent.setMinWidth(870);
                    toSent.setWrapText(true);
                    msg.getChildren().addAll(msgHead,toSent);
                    msg.setPrefWidth(870);
                    Platform.runLater(()->{
                        mainStage.getScroll().setVvalue(2);
                        mainStage.getChatMsgArea().getChildren().add(msg);
                        mainStage.getScroll().setVvalue(2);
                    });
                }
            } catch (IOException e) {
                System.out.println("与服务器断开连接");
                break;
            }
        }
    }

    public void setIn(DataInputStream in) {
        this.in = in;
    }
}
