package client.components.RootStageTitle;

import client.components.DragWindowHandler;
import client.Stage.PrimaryStage;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 管理员面板的标题栏.
 */
public class RootStageTitle implements Initializable {
    private Stage rootStage;

    /* 自定义窗口 */
    public VBox window;
    public Button minWindow;
    public Button closeWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootStage = PrimaryStage.getRootStage();
        DragWindowHandler handler = new DragWindowHandler(rootStage);
        window.setOnMousePressed(handler);      // 鼠标按下
        window.setOnMouseDragged(handler);      // 鼠标拖动
        minWindow.setOnAction(event -> rootStage.setIconified(true));   // 最小化
        closeWindow.setOnAction((event)->System.exit(0));   // 关闭程序
    }
}
