package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** NOTIFICATIONS テーブルへのアクセス。新しい通知が先頭になる降順で返します。 */
public class NotificationDao {

    public static final String TYPE_REMINDER = "reminder";
    public static final String TYPE_AI = "ai";
    public static final String TYPE_SYSTEM = "system";

    public List<Notification> findAll(String userId) {
        String sql = "SELECT NOTIFICATION_ID, USER_ID, TASK_ID, MESSAGE, NOTIFICATION_TYPE, IS_READ, CREATED_AT "
                + "FROM NOTIFICATIONS WHERE USER_ID = ? ORDER BY CREATED_AT DESC, NOTIFICATION_ID DESC";
        List<Notification> list = new ArrayList<Notification>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int tid = rs.getInt("TASK_ID");
                    Integer taskId = rs.wasNull() ? null : Integer.valueOf(tid);
                    list.add(new Notification(
                            rs.getInt("NOTIFICATION_ID"), rs.getString("USER_ID"), taskId,
                            rs.getString("MESSAGE"), rs.getString("NOTIFICATION_TYPE"),
                            rs.getBoolean("IS_READ"), rs.getTimestamp("CREATED_AT").toLocalDateTime()));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("通知の取得に失敗しました", e);
        }
        return list;
    }

    /** 既存JSPが List<String> を期待している場合用 */
    public List<String> findMessages(String userId) {
        List<String> messages = new ArrayList<String>();
        for (Notification n : findAll(userId)) messages.add(n.getMessage());
        return messages;
    }

    public int countUnread(String userId) {
        String sql = "SELECT COUNT(*) FROM NOTIFICATIONS WHERE USER_ID = ? AND IS_READ = FALSE";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DaoException("未読件数の取得に失敗しました", e);
        }
    }

    /** 通知追加。taskId が不要なら null */
    public void add(String userId, Integer taskId, String message, String type) {
        String sql = "INSERT INTO NOTIFICATIONS (USER_ID, TASK_ID, MESSAGE, NOTIFICATION_TYPE) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            if (taskId == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, taskId);
            ps.setString(3, message);
            ps.setString(4, type);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("通知の追加に失敗しました", e);
        }
    }

    public void markAllRead(String userId) {
        String sql = "UPDATE NOTIFICATIONS SET IS_READ = TRUE WHERE USER_ID = ? AND IS_READ = FALSE";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("通知の既読化に失敗しました", e);
        }
    }

    /** 同じ課題・同じ種類の通知を今日すでに作成済みか（リマインドの重複防止用） */
    public boolean existsToday(String userId, int taskId, String type) {
        String sql = "SELECT COUNT(*) FROM NOTIFICATIONS WHERE USER_ID = ? AND TASK_ID = ? "
                + "AND NOTIFICATION_TYPE = ? AND CAST(CREATED_AT AS DATE) = CURRENT_DATE";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, taskId);
            ps.setString(3, type);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DaoException("通知の重複確認に失敗しました", e);
        }
    }
}
