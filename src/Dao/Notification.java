package Dao;

import java.io.Serializable;
import java.time.LocalDateTime;

/** NOTIFICATIONS テーブル1行分 */
public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String userId;
    private Integer taskId;          // 課題に紐づかない通知は null
    private String message;
    private String type;             // reminder / ai / system
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int id, String userId, Integer taskId, String message,
                        String type, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getUserId() { return userId; }
    public Integer getTaskId() { return taskId; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
