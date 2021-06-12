package client.cache;

/**
 * 用户实体类.
 * 用于客户端构建用户信息缓存
 * 可以快速获取当前用户信息而不用与服务器端交互
 */
public class Client {
    private String name;
    private Boolean isChatting;

    public Client() {}

    public Client (String name) {
        this.name = name;
        this.isChatting = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChatting() {
        return isChatting;
    }

    public void setChatting(Boolean chatting) {
        isChatting = chatting;
    }
}
