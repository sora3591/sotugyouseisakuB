package com.campus.task;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//import org.eclipse.jdt.internal.compiler.ast.Assignment;

@WebServlet("/tasks")
public class TaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // アプリケーションスコープまたは簡易的にstaticリストでタスクを保持
    private static List<Assignment> tasks = new ArrayList<>();
    private static int idCounter = 5;

    static {
        tasks.add(new Assignment(1, "データベース設計レポート", "データベース論", LocalDate.now().plusDays(2), "正規化を行い、ER図とテーブル定義をまとめる。", 180, "todo", "高"));
        tasks.add(new Assignment(2, "Flutter UI演習", "モバイルアプリ開発", LocalDate.now().plusDays(4), "授業で学んだWidgetを使って画面を作成する。", 120, "doing", "普通"));
        tasks.add(new Assignment(3, "ネットワーク小テスト対策", "ネットワーク基礎", LocalDate.now().plusDays(1), "第4〜6回の講義資料を復習する。", 60, "todo", "高"));
        tasks.add(new Assignment(4, "情報倫理リアクションペーパー", "情報倫理", LocalDate.now().plusDays(7), "講義内容への意見を800字程度で記述する。", 45, "done", "普通"));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // ログイン簡易チェック
        if (session.getAttribute("userName") == null) {
            session.setAttribute("userName", "山田 太郎");
            session.setAttribute("userEmail", "student@example.com");
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            tasks.removeIf(t -> t.getId() == id);
            response.sendRedirect("tasks");
            return;
        }

        request.setAttribute("tasks", tasks);
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String title = request.getParameter("title");
            String course = request.getParameter("course");
            LocalDate due = LocalDate.parse(request.getParameter("due"));
            String description = request.getParameter("description");
            int estimate = Integer.parseInt(request.getParameter("estimate"));
            String priority = request.getParameter("priority");

            tasks.add(new Assignment(idCounter++, title, course, due, description, estimate, "todo", priority));
        } else if ("toggle".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            for (Assignment t : tasks) {
                if (t.getId() == id) {
                    t.setStatus("done".equals(t.getStatus()) ? "todo" : "done");
                    break;
                }
            }
        }

        response.sendRedirect("tasks");
    }
}