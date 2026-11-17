package Dao;

/** SQLException を包む非検査例外 */
public class DaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
