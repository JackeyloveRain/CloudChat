package server.common;

/**
 * 标志符号类.
 * 这个类中用于存放系统需要的各类标志
 */
public class CommonUtil {
    // 设置操作和信息标识符
    public static int PORT = 40477;     // 设置端口
    public static String BAN_USER_ERROR = "封禁用户时发生错误";
    public static String UNLOCK_USER_ERROR = "解禁用户失败";
    public static String USER_BANNED = "用户被封禁";
    public static String ACTION_SUCCESS = "操作成功";
    public static String DATABASE_ERROR = "读取数据库出错";
    public static String LOGIN_ERROR = "登录失败";
    public static String LOGIN_REPEAT = "重复登录";
    public static String REGISTER_USER_ERROR = "该用户名已被注册";
    public static String REGISTER_MAIL_ERROR = "该邮箱已被注册";
    public static String REGISTER_ERROR = "注册失败";
    public static String CREATEROOM_ERROR = "创建房间失败";
    public static String JOIN_ROOM_ERROR = "加入房间失败";
    public static String QUIT_ROOM_ERROR = "退出房间失败";
    public static String UPDATE_INFO_ERROR = "更新资料失败";
    public static String ROOM_NAME_REPEAT = "房间名已存在";
    public static String ROOM_INFO_ERROR = "房间号或密码错误";
    public static String ROOM_CLOSED = "房间已关闭";
    public static String QUIT_ROOM_NORMAL = "自然退出";
    public static String ROOM_LIST_EMPTY = "房间列表为空";

    // 设置数据流信息标识符
    public static String SIGNAL_SPLIT = "#";
    public static String SIGNAL_END = "END";
    public static String SIGNAL_LOGIN = "LOGIN";
    public static String SIGNAL_REGISTER = "REGISTER";
    public static String SIGNAL_CREATEROOM = "CREATEROOM";
    public static String SIGNAL_JOIN_ROOM = "JOIN_ROOM";
    public static String SIGNAL_QUIT_ROOM = "QUIT_ROOM";
    public static String SIGNAL_CHAT_IN_ROOM = "CHAT_IN_ROOM";
    public static String SIGNAL_UPDATE_PWD = "UPDATE_PWD";
    public static String SIGNAL_ROOM_LIST = "ROOM_LIST";
    public static String SIGNAL_LIST_START = "LIST_START";
    public static String SIGNAL_LIST_END = "LIST_END";
    public static String SIGNAL_ENTER_SEARCH = "ENTER_SEARCH";
}
