package campustask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class AppUtil {
    private AppUtil() {}

    public static AppData data(HttpServletRequest request) {
        HttpSession session = request.getSession();
        AppData data = (AppData) session.getAttribute("appData");
        if (data == null) {
            data = new AppData();
            session.setAttribute("appData", data);
        }
        return data;
    }

    public static String value(HttpServletRequest request, String name, String fallback) {
        String value = request.getParameter(name);
        return value == null ? fallback : value.trim();
    }
}
