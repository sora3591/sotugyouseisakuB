package Dao;

/** SQLException を包む非検査例外 */
public class DaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DaoException(String message, Throwable cause) {
        super(message + "（原因: " + describe(cause) + "）", cause);
    }

    private static String describe(Throwable t) {
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        while (t != null && depth++ < 5) {
            if (sb.length() > 0) sb.append(" <- ");
            sb.append(t.getClass().getName()).append(": ").append(t.getMessage());
            t = t.getCause();
        }
        return sb.toString();
    }
}
