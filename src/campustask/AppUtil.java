package campustask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class AppUtil {
    /** ログイン中ユーザーの USER_ID を入れるセッション属性名 */
    public static final String SESSION_USER_ID = "userId";

    private AppUtil() {}

    /** トリム済みのパラメータ値（未指定なら fallback）。パスワードには使わないこと。 */
    public static String value(HttpServletRequest request, String name, String fallback) {
        String value = request.getParameter(name);
        return value == null ? fallback : value.trim();
    }

    /** ログイン中の USER_ID。未ログインなら null（セッションは新規作成しない） */
    public static String currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (String) session.getAttribute(SESSION_USER_ID);
    }

    /** JSP に値を出力するときの HTML エスケープ */
    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
