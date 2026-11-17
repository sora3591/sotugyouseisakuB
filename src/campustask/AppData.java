package campustask;

import bean.Task;
import bean.User;

import java.util.List;

/**
 * 画面表示用のデータ（リクエストごとに DAO から作成する読み取り専用のまとめ役）。
 * 以前のセッション保存の仮データに代わるもので、home.jsp からは従来どおり
 * getTasks() / getNotifications() / getUserName() / getUserEmail() で参照できます。
 */
public class AppData {
    private final User user;
    private final List<Task> tasks;
    private final List<String> notifications;

    public AppData(User user, List<Task> tasks, List<String> notifications) {
        this.user = user;
        this.tasks = tasks;
        this.notifications = notifications;
    }

    public List<Task> getTasks() { return tasks; }
    public List<String> getNotifications() { return notifications; }
    public String getUserName() { return user.getName(); }
    public String getUserEmail() { return user.getEmail(); }
}
