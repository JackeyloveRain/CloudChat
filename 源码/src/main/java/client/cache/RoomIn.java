package client.cache;

/**
 * 房间实体类.
 * 用于客户端构建用户所在房间信息的缓存
 * 可以快速获取当前用户所在房间信息而不用与服务器端交互
 */
public class RoomIn {
    private String name;
    private String description;

    public RoomIn() {}
    public RoomIn(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
