package client.Tools;

/**
 * 预处理工具类.
 * 主要包含了一些预处理用户输入的静态判断方法
 */
public class PreProcessingTools {
    public static boolean isNameLengthLegal(String name) {
        return name.length() >= 4 && name.length() <= 16;
    }

    public static boolean isNameFormatLegal(String name) {
        for (char c: name.toCharArray()) {
            if (!(c<='9'&&c>='0' || c<='Z'&&c>='A' || c<='z'&&c>='a' || c=='_')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPwdLengthWrong(String pwd) {
        return pwd.length() < 8 || pwd.length() > 16;
    }

    public static boolean isPwdFormatWrong(String pwd) {
        for (char c: pwd.toCharArray()) {
            if (!(c<='9'&&c>='0' || c<='Z'&&c>='A' || c<='z'&&c>='a')) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMailLegal(String mail) {
        return mail.matches("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*$");
    }

    public static boolean isRoomNameLegal(String name) {
        return name.length() >= 4 && name.length() <= 16;
    }

    public static boolean isRoomPwdLegal(String pwd) {
        if (pwd.length()>8 || pwd.length()<4) {
            return false;
        } else {
            for (char c: pwd.toCharArray()) {
                if (!(c<='9' && c>='0')) {
                    return false;
                }
            }
        }
        return true;
    }
}
