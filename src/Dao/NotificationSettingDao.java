package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bean.NotificationSetting;

/** NOTIFICATION_SETTINGS テーブル（ユーザーごとに1行） */
public class NotificationSettingDao {

    /** 設定行がなければ既定値（両方ON）を返します */
    public NotificationSetting find(String userId) {
        String sql = "SELECT BEFORE_DUE, ON_DUE FROM NOTIFICATION_SETTINGS WHERE USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new NotificationSetting(rs.getBoolean("BEFORE_DUE"), rs.getBoolean("ON_DUE"))
                                 : new NotificationSetting(true, true);
            }
        } catch (SQLException e) {
            throw new DaoException("通知設定の取得に失敗しました", e);
        }
    }

    /** 保存（行がなければ作成、あれば更新） */
    public void save(String userId, boolean beforeDue, boolean onDue) {
        String sql = "MERGE INTO NOTIFICATION_SETTINGS (USER_ID, BEFORE_DUE, ON_DUE) KEY (USER_ID) VALUES (?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setBoolean(2, beforeDue);
            ps.setBoolean(3, onDue);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("通知設定の保存に失敗しました", e);
        }
    }
}
