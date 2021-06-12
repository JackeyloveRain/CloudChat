package client.components.MainStageTitle;

import client.components.DragWindowHandler;
import client.Stage.PrimaryStage;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 主面板的标题栏.
 */
public class MainStageTitle implements Initializable {
    private Stage mainStage;

    /* 自定义窗口 */
    public VBox window;
    public Button minWindow;
    public Button closeWindow;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainStage = PrimaryStage.getMainStage();
        DragWindowHandler handler = new DragWindowHandler(mainStage);
        window.setOnMousePressed(handler);      // 鼠标按下
        window.setOnMouseDragged(handler);      // 鼠标拖动
        minWindow.setOnAction(event -> mainStage.setIconified(true));       // 最小化
        closeWindow.setOnAction((event)->System.exit(0));       // 关闭程序
    }
}
