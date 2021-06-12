package client.Stage;

import client.Main;
import client.Tools.PreProcessingTools;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static client.Tools.CommonUtil.*;
import static client.Tools.CommonUtil.DATABASE_ERROR;

/**
 * 修改密码弹窗类.
 */
public class PWDAlert {
    private static boolean res=false;
    private static Stage stage;
    public static boolean display() throws IOException {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(new FXMLLoader(PWDAlert.class.getResource("/FXML/PWDAlert.fxml")).load(),400,350);
        scene.getStylesheets().add(PWDAlert.class.getResource("/CSS/PWD.css").toExternalForm());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.showAndWait();
        return res;
    }
    @FXML
    private PasswordField pwdModify1;
    @FXML
    private PasswordField pwdModify2;
    @FXML
    private Label pwd1ModifyError;
    @FXML
    private Label pwd2ModifyError;

    /**
     * “修改密码”的事件处理.
     * @see MainStage#changePWD()
     */
    public boolean modifyPwd() {
        // 获取输入框数据
        String pwd1 = pwdModify1.getText();
        String pwd2 = pwdModify2.getText();
        System.out.println("client -- want: modify pwd");
        // 数据预处理
        boolean isLegal = true;
        if (PreProcessingTools.isPwdLengthWrong(pwd1)) {
            pwd1ModifyError.setText("密码长度应在8~16位之间");
            pwd1ModifyError.setVisible(true);
            isLegal = false;
        } else if (PreProcessingTools.isPwdFormatWrong(pwd1)) {
            pwd1ModifyError.setText("密码只能是数字、字母、下划线的组合");
            pwd1ModifyError.setVisible(true);
            isLegal = false;
        } else {
            pwd1ModifyError.setVisible(false);
        }
        if (!pwd1.equals(pwd2)) {
            pwd2ModifyError.setText("两次输入的密码不一致");
            pwd2ModifyError.setVisible(true);
            isLegal = false;
        } else {
            pwd2ModifyError.setVisible(false);
        }
        if (!isLegal) {
            return false;
        }
        // 发送至客户端
        DataInputStream in = Main.getIn();
        DataOutputStream out = Main.getOut();
        try {
            // 获得服务器信息
            out.writeUTF(SIGNAL_UPDATE_PWD + "#" + pwd1 + "#" + SIGNAL_END);
            String state = in.readUTF();
            System.out.println("client -- get update_pwd reply: " + state);
            if (state.equals(ACTION_SUCCESS)) {
                System.out.println("client -- update password success");
            } else if (state.equals(DATABASE_ERROR)) {
                pwd1ModifyError.setText("数据库连接错误");
                pwd1ModifyError.setVisible(true);
            } else {
                pwd1ModifyError.setText("修改密码时发生未知错误");
                pwd1ModifyError.setVisible(true);
            }
        } catch (IOException e) {
            System.out.println("client -- error: update password error");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * “取消操作”的事件处理.
     */
    @FXML
    public void toCancel(){
        stage.close();
    }

    /**
     * “确认操作”的事件处理.
     */
    @FXML
    public void toConfirm(){
        if (modifyPwd()) stage.close();
    }
}
