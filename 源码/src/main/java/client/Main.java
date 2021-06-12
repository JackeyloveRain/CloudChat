package client;
import client.cache.Client;
import client.cache.RoomIn;
import client.Tools.CommonUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 客户端入口.
 */
public class Main extends Application {
    private static Stage stage;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Client client;       // 客户端的用户信息缓存
    private static RoomIn roomIn;       // 客户端的用户所在房间信息缓存



    @Override
    public void start(Stage primaryStage) throws IOException {
        // 建立此客户端的套接字
        try {
            Socket clientSocket = new Socket("10.136.51.133", CommonUtil.PORT);
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            client = new Client();
            roomIn = new RoomIn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //为了可以在其他类里获得primary stage，先暂时使用这种方法
        stage = primaryStage;
        //引入fxml
        Parent root = FXMLLoader.load(getClass().getResource("/FXML/PrimaryStage.fxml"));
        //创建scene
        Scene scene=new Scene(root, 600, 400);
        //为scene添加css样式
        scene.getStylesheets().add(getClass().getResource("/CSS/PrimaryStageTitle.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/CSS/PrimaryStage.css").toExternalForm());
        //设置窗口标题
        primaryStage.setTitle("Primary");
        //为舞台设置scene
        primaryStage.setScene(scene);
        //设置logo
        primaryStage.getIcons().add(new Image("IMG/logo.png"));
        //不显示标题栏
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //使窗口大小固定
        primaryStage.setResizable(false);
        //显示
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return stage;
    }       //获得primary stage

    public static void main(String[] args) {
        launch(args);
    }

    public static DataInputStream getIn() {
        return in;
    }

    public static DataOutputStream getOut() {
        return out;
    }

    public static void setClient(Client client) {
        Main.client = client;
    }

    public static Client getClient() {
        return client;
    }

    public static RoomIn getRoomIn() {
        return roomIn;
    }

    public static void setRoomInfo(RoomIn roomIn) {
        Main.roomIn = roomIn;
    }

}
