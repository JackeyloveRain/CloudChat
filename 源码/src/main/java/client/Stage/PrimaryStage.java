package client.Stage;

import client.cache.Client;
import client.Main;
import client.Tools.PreProcessingTools;
import javafx.animation.PathTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static client.Tools.CommonUtil.*;

/**
 * 起始界面类.
 * 主要工作如下 <br>
 * 1 加载用户界面 <br>
 * 2 处理用户输入 <br>
 * 3 与服务器端进行传输 <br>
 * 4 根据收到的数据渲染页面
 */
public class PrimaryStage implements Initializable {
    public static FXMLLoader mainFxmlLoader;
    private static Stage mainStage;
    private static Stage rootStage;
    private boolean isInLogin =true;
    @FXML
    private AnchorPane moveBlock;
    @FXML
    private Text registerSuccessMsg;
    @FXML
    private TextField usernameLogin;
    @FXML
    private TextField passwordLogin;
    @FXML
    private Label pwd2ErrorMsg;
    @FXML
    private Label pwd1ErrorMsg;
    @FXML
    private Label emailErrorMsg;
    @FXML
    private Label usernameErrorMsg;
    @FXML
    private Label usernameErrorLogin;
    @FXML
    private Label pwdErrorLogin;
    @FXML
    private TextField username;
    @FXML
    private TextField password1;
    @FXML
    private TextField password2;
    @FXML
    private TextField email;
    @FXML
    private Button toLoginButton;
    @FXML
    private AnchorPane registerMsg;
    @FXML
    private ImageView registerBg;
    @FXML
    private ImageView loginBg;
    @FXML
    private ImageView moveBg;

    /**
     * 初始化.
     */
    public void initialize(URL location, ResourceBundle resources){
        //设置背景图
        registerBg.setImage(new Image("IMG/prbg1.jpg",300,400,false,false));
        moveBg.setImage(new Image("IMG/prbg2.jpg",300,400,false,false));
        loginBg.setImage(new Image("IMG/prbg3.jpg",300,400,false,false));
        //设置回车事件
        usernameLogin.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    login();
                }
            }
        });
        passwordLogin.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){
                    login();
                }
            }
        });
    }

    /**
     * “进入登录界面”的事件处理.
     */
    @FXML
    void moveToLogin() {
        //创建路径
        Path path=new javafx.scene.shape.Path();
        if (!isInLogin){
            //设置路径
            path.getElements().add(new MoveTo(450, 200));
            path.getElements().add(new LineTo(150, 200));
            path.getElements().add(new LineTo(170, 200));
            path.getElements().add(new LineTo(150, 200));
            isInLogin =true;
            //创建动画
            PathTransition pathTransition=new PathTransition();
            //设置持续时间1秒
            pathTransition.setDuration(Duration.seconds(1));
            //循环一遍
            pathTransition.setCycleCount(1);
            //设置路径
            pathTransition.setPath(path);
            //设置动画物体
            pathTransition.setNode(moveBlock);
            //启动动画
            pathTransition.play();
        }
    }

    /**
     * “登录”的事件处理.
     */
    @FXML
    void login(){
        // 获取输入框数据
        String name=usernameLogin.getText();
        String pwd=passwordLogin.getText();
        System.out.println("client -- want send login message: " + name + " " + pwd);

        // 数据预处理
        boolean isLegal = true;
        if (name.equals("")) {
            isLegal = false;
            usernameErrorLogin.setText("用户名不能为空");
            usernameErrorLogin.setVisible(true);
        } else {
            usernameErrorLogin.setVisible(false);
        }
        if (pwd.equals("")) {
            isLegal = false;
            pwdErrorLogin.setText("密码不能为空");
            pwdErrorLogin.setVisible(true);
        } else {
            pwdErrorLogin.setVisible(false);
        }
        if (!isLegal) {
            return;
        }

        // 发送至客户端
        DataInputStream in = Main.getIn();
        DataOutputStream out = Main.getOut();
        try {
            // 获得服务器信息
            out.writeUTF(SIGNAL_LOGIN + SIGNAL_SPLIT
                    + name + SIGNAL_SPLIT
                    + pwd + SIGNAL_SPLIT
                    + SIGNAL_END);
            String state = in.readUTF();
            System.out.println(state);
            // 服务器信息响应
            String str = "";
            if (state.equals(ACTION_SUCCESS)) {
                // 记录用户信息
                Main.setClient(new Client(name));
                loginSuccess(name);
            }
            else {
                if (state.equals(LOGIN_REPEAT)) {
                    str = name + "已经登录，不可重复登陆";
                }
                else if (state.equals(LOGIN_ERROR)) {
                    str = "用户名或密码错误";
                } else if (state.equals(USER_BANNED)) {
                    str = "您已被封禁";
                }
                usernameErrorLogin.setText(str);
                usernameErrorLogin.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * “登录成功”的事件处理.
     */
    void loginSuccess(String name) throws IOException {
        if(name.equals("root")){
            //创建mainStage
            rootStage=new Stage();
            //创建scene
            Scene scene=new Scene(FXMLLoader.load(getClass().getResource("/FXML/rootStage.fxml")),600,400);
            //为scene添加css样式
            scene.getStylesheets().add(getClass().getResource("/CSS/RootStageTitle.css").toExternalForm());
            //设置窗口标题
            rootStage.setTitle("Root");
            rootStage.setScene(scene);
            //设置logo
            rootStage.getIcons().add(new Image("IMG/logo.png"));
            //不显示标题栏
            rootStage.initStyle(StageStyle.UNDECORATED);
            //关闭primaryStage
            Main.getPrimaryStage().close();
            //显示
            rootStage.show();
        }
        else{
            //创建mainStage
            mainStage=new Stage();
            //创建scene
            mainFxmlLoader=new FXMLLoader(getClass().getResource("/FXML/Mainstage.fxml"));
            Scene scene=new Scene(mainFxmlLoader.load(),1000,600);
            //为scene添加css样式
            scene.getStylesheets().add(getClass().getResource("/CSS/Mainstage.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/CSS/MainStageTitle.css").toExternalForm());
            //设置窗口标题
            mainStage.setTitle("Main");
            mainStage.setScene(scene);
            //设置logo
            mainStage.getIcons().add(new Image("IMG/logo.png"));
            //不显示标题栏
            mainStage.initStyle(StageStyle.UNDECORATED);
            //关闭primaryStage
            Main.getPrimaryStage().close();
            //显示
            mainStage.show();
        }

    }

    /**
     * “进入注册界面”的事件处理.
     */
    @FXML
    void moveToRegister() {
        registerSuccessMsg.setVisible(false);
        toLoginButton.setVisible(false);
        registerMsg.setVisible(true);
        //创建路径
        Path path=new javafx.scene.shape.Path();
        if (isInLogin){
            //设置路径
            path.getElements().add(new MoveTo(150, 200));
            path.getElements().add(new LineTo(450, 200));
            path.getElements().add(new LineTo(430, 200));
            path.getElements().add(new LineTo(450, 200));
            isInLogin =false;
            //创建动画
            PathTransition pathTransition=new PathTransition();
            //设置持续时间1秒
            pathTransition.setDuration(Duration.seconds(1));
            //循环一遍
            pathTransition.setCycleCount(1);
            //设置路径
            pathTransition.setPath(path);
            //设置动画物体
            pathTransition.setNode(moveBlock);
            //启动动画
            pathTransition.play();
        }
    }

    /**
     * “注册”的事件处理.
     */
    @FXML
    void register(){
        // 获得用户输入
        String name = username.getText();
        String pwd1 = password1.getText();
        String pwd2 = password2.getText();
        String mail = email.getText();
        System.out.println("client -- want send login message: " + name + " " + pwd1 + " " + pwd2 + " " + mail);

        // 数据预处理
        boolean isLegal = true;
        if (!PreProcessingTools.isNameLengthLegal(name)) {
            usernameErrorMsg.setText("用户名长度应在4~16位之间");
            usernameErrorMsg.setVisible(true);
        } else if (!PreProcessingTools.isNameFormatLegal(name)) {
            usernameErrorMsg.setText("用户名只能是数字、字母、下划线的组合");
            usernameErrorMsg.setVisible(true);
            isLegal = false;
        } else {
            usernameErrorMsg.setVisible(false);
        }
        if (PreProcessingTools.isPwdLengthWrong(pwd1)) {
            pwd1ErrorMsg.setText("密码长度应在8~16位之间");
            pwd1ErrorMsg.setVisible(true);
            isLegal = false;
        } else if (PreProcessingTools.isPwdFormatWrong(pwd1)) {
            pwd1ErrorMsg.setText("密码只能是数字、字母、下划线的组合");
            pwd1ErrorMsg.setVisible(true);
            isLegal = false;
        } else {
            pwd1ErrorMsg.setVisible(false);
        }
        if (!pwd1.equals(pwd2)) {
            pwd2ErrorMsg.setText("两次输入的密码不一致");
            pwd2ErrorMsg.setVisible(true);
            isLegal = false;
        } else {
            pwd2ErrorMsg.setVisible(false);
        }
        if (!PreProcessingTools.isMailLegal(mail)) {
            emailErrorMsg.setText("邮箱格式错误");
            emailErrorMsg.setVisible(true);
            isLegal = false;
        } else {
            emailErrorMsg.setVisible(false);
        }
        if (!isLegal) {
            return;
        }

        // 向服务器发送注册信息
        DataInputStream in = Main.getIn();
        DataOutputStream out =Main.getOut();
        try {
            // 获得服务器信息
            out.writeUTF(SIGNAL_REGISTER + SIGNAL_SPLIT
                    + name + SIGNAL_SPLIT
                    + pwd1 + SIGNAL_SPLIT
                    + mail + SIGNAL_SPLIT
                    + SIGNAL_END);
            String state = in.readUTF();
            System.out.println(state);
            // 服务器信息响应
            if (state.equals(ACTION_SUCCESS)) {
                registerSuccess();
            }
            else if(state.equals(REGISTER_USER_ERROR)) {
                usernameErrorMsg.setText("该用户名已被注册");
                usernameErrorMsg.setVisible(true);
            } else if(state.equals(REGISTER_MAIL_ERROR)) {
                emailErrorMsg.setText("该邮箱已被注册");
                emailErrorMsg.setVisible(true);
            } else {
                System.out.println("注册时发生未知错误");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * “注册成功”的事件处理.
     */
    void registerSuccess(){
        registerMsg.setVisible(false);
        toLoginButton.setVisible(true);
        registerSuccessMsg.setVisible(true);
    }

    // getter
    public static Stage getMainStage(){
        return mainStage;
    }

    public static Stage getRootStage(){
        return rootStage;
    }

    public static FXMLLoader getMainFxmlLoader() {
        return mainFxmlLoader;
    }
}
