package bean;

import java.io.Serializable;

/** NOTIFICATION_SETTINGS テーブル1行分 */
public class NotificationSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean beforeDue = true;
    private boolean onDue = true;

    public NotificationSetting() {}

    public NotificationSetting(boolean beforeDue, boolean onDue) {
        this.beforeDue = beforeDue;
        this.onDue = onDue;
    }

    public boolean isBeforeDue() { return beforeDue; }
    public void setBeforeDue(boolean beforeDue) { this.beforeDue = beforeDue; }
    public boolean isOnDue() { return onDue; }
    public void setOnDue(boolean onDue) { this.onDue = onDue; }
}
