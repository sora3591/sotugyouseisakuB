package Dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bean.Task;

/**
 * TASKS テーブルへのアクセス。
 * 全メソッドで USER_ID を条件に含め、他ユーザーの課題を操作できないようにしています。
 */
public class TaskDao {

    private static final String COLUMNS =
            "TASK_ID, TITLE, COURSE, DUE_DATE, DESCRIPTION, ESTIMATE, STATUS, PRIORITY";

    /** 期限の近い順（期限なしは最後）に全件取得 */
    public List<Task> findAll(String userId) {
        return search(userId, null);
    }

    /** 課題名・授業名・説明のキーワード検索（q が空なら全件） */
    public List<Task> search(String userId, String q) {
        boolean hasQ = q != null && !q.isEmpty();
        String sql = "SELECT " + COLUMNS + " FROM TASKS WHERE USER_ID = ?"
                + (hasQ ? " AND (TITLE LIKE ? OR COURSE LIKE ? OR DESCRIPTION LIKE ?)" : "")
                + " ORDER BY DUE_DATE NULLS LAST, TASK_ID";
        List<Task> list = new ArrayList<Task>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            if (hasQ) {
                String like = "%" + q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("課題一覧の取得に失敗しました", e);
        }
        return list;
    }

    public Task findById(String userId, int taskId) {
        String sql = "SELECT " + COLUMNS + " FROM TASKS WHERE USER_ID = ? AND TASK_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, taskId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("課題の取得に失敗しました", e);
        }
    }

    /** 追加。採番された TASK_ID を task にセットして返します（新規は id=0 で渡してOK）。 */
    public Task insert(String userId, Task task) {
        String sql = "INSERT INTO TASKS (USER_ID, TITLE, COURSE, DUE_DATE, DESCRIPTION, ESTIMATE, STATUS, PRIORITY) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, userId);
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getCourse());
            setDate(ps, 4, task.getDue());
            ps.setString(5, task.getDescription());
            ps.setInt(6, task.getEstimate());
            ps.setString(7, task.getStatus());
            ps.setString(8, task.getPriority());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) task.setId(keys.getInt(1));
            }
            return task;
        } catch (SQLException e) {
            throw new DaoException("課題の追加に失敗しました", e);
        }
    }

    /** 更新（STATUS は toggleDone / updateStatus で変更） */
    public boolean update(String userId, Task task) {
        String sql = "UPDATE TASKS SET TITLE = ?, COURSE = ?, DUE_DATE = ?, DESCRIPTION = ?, "
                + "ESTIMATE = ?, PRIORITY = ?, UPDATED_AT = CURRENT_TIMESTAMP "
                + "WHERE TASK_ID = ? AND USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getCourse());
            setDate(ps, 3, task.getDue());
            ps.setString(4, task.getDescription());
            ps.setInt(5, task.getEstimate());
            ps.setString(6, task.getPriority());
            ps.setInt(7, task.getId());
            ps.setString(8, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("課題の更新に失敗しました", e);
        }
    }

    /** 状態を直接変更（todo / doing / done） */
    public boolean updateStatus(String userId, int taskId, String status) {
        String sql = "UPDATE TASKS SET STATUS = ?, UPDATED_AT = CURRENT_TIMESTAMP WHERE TASK_ID = ? AND USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, taskId);
            ps.setString(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("課題の状態変更に失敗しました", e);
        }
    }

    /** done ⇔ todo の切り替え（TaskServlet の toggle と同じ挙動） */
    public boolean toggleDone(String userId, int taskId) {
        String sql = "UPDATE TASKS SET STATUS = CASE WHEN STATUS = 'done' THEN 'todo' ELSE 'done' END, "
                + "UPDATED_AT = CURRENT_TIMESTAMP WHERE TASK_ID = ? AND USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("課題の状態変更に失敗しました", e);
        }
    }

    /** 削除（NOTIFICATIONS.TASK_ID は ON DELETE SET NULL で自動的に null になります） */
    public boolean delete(String userId, int taskId) {
        String sql = "DELETE FROM TASKS WHERE TASK_ID = ? AND USER_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("課題の削除に失敗しました", e);
        }
    }

    /** 未完了のうち最も期限が近い課題（AI提案用）。なければ null */
    public Task findNextDue(String userId) {
        String sql = "SELECT " + COLUMNS + " FROM TASKS WHERE USER_ID = ? AND STATUS <> 'done' "
                + "ORDER BY DUE_DATE NULLS LAST, TASK_ID LIMIT 1";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("次の課題の取得に失敗しました", e);
        }
    }

    /**
     * リマインド対象（未完了で期限が from〜to の課題）。
     * 通知設定 BEFORE_DUE / ON_DUE に基づく通知生成バッチ向け。
     */
    public List<Task> findDueBetween(String userId, LocalDate from, LocalDate to) {
        String sql = "SELECT " + COLUMNS + " FROM TASKS WHERE USER_ID = ? AND STATUS <> 'done' "
                + "AND DUE_DATE BETWEEN ? AND ? ORDER BY DUE_DATE, TASK_ID";
        List<Task> list = new ArrayList<Task>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("期限間近の課題取得に失敗しました", e);
        }
        return list;
    }

    // ---- 内部処理 ----

    private static void setDate(PreparedStatement ps, int index, LocalDate d) throws SQLException {
        if (d == null) ps.setNull(index, Types.DATE);
        else ps.setDate(index, Date.valueOf(d));
    }

    private Task map(ResultSet rs) throws SQLException {
        Date due = rs.getDate("DUE_DATE");
        return new Task(
                rs.getInt("TASK_ID"),
                rs.getString("TITLE"),
                rs.getString("COURSE"),
                due == null ? null : due.toLocalDate(),
                rs.getString("DESCRIPTION"),
                rs.getInt("ESTIMATE"),
                rs.getString("STATUS"),
                rs.getString("PRIORITY"));
    }
}