package client.components.PrimaryStageTitle;

import client.components.DragWindowHandler;
import client.Main;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 登录注册面板的标题栏.
 */
public class PrimaryStageTitle implements Initializable {

    private Stage primaryStage;

    /* 自定义窗口 */
    public VBox window;
    public Button minWindow;
    public Button closeWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        primaryStage = Main.getPrimaryStage();      // primaryStage为start方法头中的Stage
        DragWindowHandler handler = new DragWindowHandler(primaryStage);
        window.setOnMousePressed(handler);      // 鼠标按下
        window.setOnMouseDragged(handler);      // 鼠标拖动
        minWindow.setOnAction(event -> primaryStage.setIconified(true));    // 最小化
        closeWindow.setOnAction((event)->System.exit(0));       // 关闭程序
    }
}
