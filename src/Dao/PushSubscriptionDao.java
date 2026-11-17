package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import bean.PushSubscription;

/** PUSH_SUBSCRIPTIONS テーブル（Web Push の購読情報） */
public class PushSubscriptionDao {

    public void add(String userId, String endpoint, String publicKey, String authKey) {
        String sql = "INSERT INTO PUSH_SUBSCRIPTIONS (USER_ID, ENDPOINT, PUBLIC_KEY, AUTH_KEY) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, endpoint);
            ps.setString(3, publicKey);
            ps.setString(4, authKey);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("購読情報の保存に失敗しました", e);
        }
    }

    public List<PushSubscription> findByUser(String userId) {
        String sql = "SELECT SUBSCRIPTION_ID, USER_ID, ENDPOINT, PUBLIC_KEY, AUTH_KEY "
                + "FROM PUSH_SUBSCRIPTIONS WHERE USER_ID = ? ORDER BY SUBSCRIPTION_ID";
        List<PushSubscription> list = new ArrayList<PushSubscription>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PushSubscription(rs.getInt("SUBSCRIPTION_ID"), rs.getString("USER_ID"),
                            rs.getString("ENDPOINT"), rs.getString("PUBLIC_KEY"), rs.getString("AUTH_KEY")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("購読情報の取得に失敗しました", e);
        }
        return list;
    }

    /** 期限切れ購読の掃除用（ENDPOINT は CLOB なので ID で削除します） */
    public boolean delete(int subscriptionId) {
        String sql = "DELETE FROM PUSH_SUBSCRIPTIONS WHERE SUBSCRIPTION_ID = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, subscriptionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("購読情報の削除に失敗しました", e);
        }
    }
}
