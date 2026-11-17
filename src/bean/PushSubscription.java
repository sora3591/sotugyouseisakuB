package bean;

import java.io.Serializable;

/** PUSH_SUBSCRIPTIONS テーブル1行分 */
public class PushSubscription implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String userId;
    private String endpoint;
    private String publicKey;
    private String authKey;

    public PushSubscription() {}

    public PushSubscription(int id, String userId, String endpoint, String publicKey, String authKey) {
        this.id = id;
        this.userId = userId;
        this.endpoint = endpoint;
        this.publicKey = publicKey;
        this.authKey = authKey;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    public String getAuthKey() { return authKey; }
    public void setAuthKey(String authKey) { this.authKey = authKey; }
}
