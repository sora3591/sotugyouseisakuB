package campustask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DB未接続の試作用データ。
 * セッションごとに初期データを作成します。
 */
public class AppData {
    private final List<Task> tasks = new ArrayList<Task>();
    private final List<String> notifications = new ArrayList<String>();
    private String userName = "山田 太郎";
    private String userEmail = "student@example.com";

    public AppData() {
        LocalDate today = LocalDate.now();
        tasks.add(new Task(1, "データベース設計レポート", "データベース論",
                today.plusDays(2), "正規化を行い、ER図とテーブル定義をまとめる。",
                180, "todo", "高"));
        tasks.add(new Task(2, "Flutter UI演習", "モバイルアプリ開発",
                today.plusDays(4), "授業で学んだWidgetを使って画面を作成する。",
                120, "doing", "普通"));
        tasks.add(new Task(3, "ネットワーク小テスト対策", "ネットワーク基礎",
                today.plusDays(1), "第4〜6回の講義資料を復習する。",
                60, "todo", "高"));
        tasks.add(new Task(4, "情報倫理リアクションペーパー", "情報倫理",
                today.plusDays(7), "講義内容への意見を800字程度で記述する。",
                45, "done", "普通"));

        notifications.add("「ネットワーク小テスト対策」の期限が近づいています。");
        notifications.add("「データベース設計レポート」は計画的に進めましょう。");
    }

    public List<Task> getTasks() { return tasks; }
    public List<String> getNotifications() { return notifications; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int nextId() {
        int max = 0;
        for (Task task : tasks) if (task.getId() > max) max = task.getId();
        return max + 1;
    }

    public Task findTask(int id) {
        for (Task task : tasks) if (task.getId() == id) return task;
        return null;
    }
}
