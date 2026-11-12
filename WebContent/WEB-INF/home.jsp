
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="campustask.*,java.util.*,java.time.*" %>

<%
AppData data = (AppData)request.getAttribute("data");
String pageName = (String)request.getAttribute("page");
String filter = (String)request.getAttribute("filter");
String q = (String)request.getAttribute("q");

List<Task> tasks = data.getTasks();

int done = 0;
for (Task t : tasks) {
    if ("done".equals(t.getStatus())) done++;
}

List<Task> shown = new ArrayList<Task>();

for (Task t : tasks) {
    boolean status = "すべて".equals(filter) ||
        ("未完了".equals(filter) && !"done".equals(t.getStatus())) ||
        ("完了".equals(filter) && "done".equals(t.getStatus()));

    if (status && (t.getTitle().contains(q) || t.getCourse().contains(q))) {
        shown.add(t);
    }
}

Collections.sort(shown, new Comparator<Task>() {
    public int compare(Task a, Task b) {
        return a.getDue().compareTo(b.getDue());
    }
});

String ctx = request.getContextPath();

String message = (String)session.getAttribute("message");
session.removeAttribute("message");
%>

<!DOCTYPE html>
<html lang="ja">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>Campus Task</title>

<link rel="stylesheet" href="<%=ctx%>/css/style.css">

<style>

/* ================================= */
/* 課題追加ポップアップ */
/* ================================= */

.add-dialog {
    width: 90%;
    max-width: 550px;
    max-height: 90vh;
    overflow-y: auto;

    padding: 0;
    border: none;
    border-radius: 16px;

    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

/* 背景 */
.add-dialog::backdrop {
    background: rgba(0, 0, 0, 0.5);
}

/* ヘッダー */
.dialog-header {
    padding: 20px 25px;

    border-bottom: 1px solid #eee;

    display: flex;
    justify-content: space-between;
    align-items: center;
}

.dialog-header h2 {
    margin: 0;
    font-size: 20px;
}

/* 閉じるボタン */
.dialog-close {
    border: none;
    background: none;

    font-size: 26px;
    cursor: pointer;

    color: #666;
}

/* 入力フォーム */
.dialog-body {
    padding: 25px;
}

.dialog-body label {
    display: block;
    margin-bottom: 16px;
    font-weight: bold;
}

.dialog-body input,
.dialog-body select,
.dialog-body textarea {
    display: block;

    width: 100%;
    margin-top: 7px;
    padding: 10px;

    border: 1px solid #ddd;
    border-radius: 8px;

    box-sizing: border-box;
    font: inherit;
}

/* フッター */
.dialog-footer {
    padding: 15px 25px;

    border-top: 1px solid #eee;

    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* 追加ボタン */
.add-button {
    cursor: pointer;
    font: inherit;
}

/* ポップアップ内のボタン */
.dialog-footer button {
    cursor: pointer;
}

</style>

</head>

<body>

<div class="layout">

<!-- ================================= -->
<!-- サイドバー -->
<!-- ================================= -->

<aside class="sidebar">

    <div class="logo">
        <span>🎓</span>
        <div>
            <b>Campus Task</b>
            <small>課題管理アプリ</small>
        </div>
    </div>

    <p class="menu-label">MENU</p>

    <a class="<%="dashboard".equals(pageName)?"active":""%>"
       href="<%=ctx%>/home?page=dashboard">
        ▦　ホーム
    </a>

    <a class="<%="tasks".equals(pageName)?"active":""%>"
       href="<%=ctx%>/home?page=tasks">
        ☷　課題一覧
    </a>

    <a class="<%="reminders".equals(pageName)?"active":""%>"
       href="<%=ctx%>/home?page=reminders">
        ♧　リマインド・通知
    </a>

    <a class="<%="profile".equals(pageName)?"active":""%>"
       href="<%=ctx%>/home?page=profile">
        ♙　マイページ
    </a>

    <div class="sidebar-user">
        <b><%=data.getUserName()%></b>
        <small>学生アカウント</small>
    </div>

</aside>


<!-- ================================= -->
<!-- メインコンテンツ -->
<!-- ================================= -->

<main class="main">

    <header class="topbar">
        <span></span>
        <a href="<%=ctx%>/home?page=reminders">♧</a>
        <b><%=data.getUserName()%></b>
    </header>

    <div class="content">

    <% if (message != null) { %>

        <div class="notice"><%=message%></div>

    <% } %>


    <!-- ================================= -->
    <!-- ダッシュボード -->
    <!-- ================================= -->

    <% if ("dashboard".equals(pageName)) { %>

        <div class="heading-row">

            <div>
                <h1>おかえりなさい！ 👋</h1>
                <p>
                    課題の進捗を確認して、今日やることを整理しましょう。
                </p>
            </div>

            <a class="primary button-link"
               href="<%=ctx%>/home?page=tasks#openAdd">
                ＋ 課題を追加
            </a>

        </div>


        <!-- 統計カード -->

        <div class="stats">

            <div class="stat">
                <small>登録課題</small>
                <strong><%=tasks.size()%></strong> 件
            </div>

            <div class="stat">
                <small>未完了</small>
                <strong><%=tasks.size()-done%></strong> 件
            </div>

            <div class="stat">
                <small>完了</small>
                <strong><%=done%></strong> 件
            </div>

        </div>


        <!-- 今日の学習プラン -->

        <div class="hero">
            <small>今日の学習プラン</small>

            <h2>
                少しずつ進めて、提出忘れを防ごう。
            </h2>

            <p>
                未完了の課題 <%=tasks.size()-done%> 件を確認しましょう。
            </p>
        </div>


        <!-- 期限が近い課題 -->

        <h2 class="section-title">
            期限が近い課題

            <a href="<%=ctx%>/home?page=tasks">
                すべて見る →
            </a>
        </h2>

        <div class="task-list">

        <%
        int count = 0;

        for (Task t : shown) {
            if (!"done".equals(t.getStatus()) && count < 3) {
                count++;
        %>

            <div class="task-card">

                <div>
                    <b><%=t.getTitle()%></b>

                    <small>
                        <%=t.getCourse()%> ・
                        <%=t.getEstimate()%>分
                    </small>
                </div>

                <span>期限 <%=t.getDue()%></span>

                <span class="chip">
                    <%=t.getStatus()%>
                </span>

            </div>

        <%
            }
        }
        %>

        </div>


    <!-- ================================= -->
    <!-- 課題一覧 -->
    <!-- ================================= -->

    <% } else if ("tasks".equals(pageName)) { %>


        <!-- タイトルと追加ボタン -->

        <div class="heading-row">

            <div>
                <h1>課題一覧</h1>

                <p>
                    課題の追加・確認・編集・削除ができます。
                </p>
            </div>

            <!-- 課題追加ボタン -->

            <button type="button"
                    class="primary add-button"
                    onclick="document.getElementById('addDialog').showModal()">

                ＋ 課題を追加

            </button>

        </div>


        <!-- 検索フォーム -->

        <form class="search-row"
              method="get"
              action="<%=ctx%>/home">

            <input type="hidden" name="page" value="tasks">

            <input name="q"
                   placeholder="課題名・授業名で検索"
                   value="<%=q%>">

            <button class="secondary">検索</button>

        </form>


        <!-- 絞り込み -->

        <div class="filters">

        <%
        for (String f : new String[]{"すべて","未完了","完了"}) {
        %>

            <a class="<%=filter.equals(f)?"selected":""%>"
               href="<%=ctx%>/home?page=tasks&filter=<%=f%>&q=<%=java.net.URLEncoder.encode(q,"UTF-8")%>">

                <%=f%>

            </a>

        <%
        }
        %>

        </div>


        <!-- 課題リスト -->

        <div class="task-list">

        <%
        for (Task t : shown) {
        %>

            <div class="task-card task-expanded">

                <div class="task-main">

                    <b><%=t.getTitle()%></b>

                    <small>
                        <%=t.getCourse()%> ・
                        <%=t.getEstimate()%>分 ・
                        優先度：<%=t.getPriority()%>
                    </small>

                    <p><%=t.getDescription()%></p>

                </div>


                <div class="task-meta">

                    <span>期限 <%=t.getDue()%></span>

                    <span class="chip">
                        <%= "done".equals(t.getStatus())
                            ? "完了"
                            : ("doing".equals(t.getStatus())
                                ? "着手中"
                                : "未着手") %>
                    </span>


                    <!-- 完了切り替え -->

                    <form method="post" action="<%=ctx%>/task">

                        <input type="hidden"
                               name="action"
                               value="toggle">

                        <input type="hidden"
                               name="id"
                               value="<%=t.getId()%>">

                        <button class="tiny">
                            完了切替
                        </button>

                    </form>


                    <!-- 削除 -->

                    <form method="post"
                          action="<%=ctx%>/task"
                          onsubmit="return confirm('この課題を削除しますか？');">

                        <input type="hidden"
                               name="action"
                               value="delete">

                        <input type="hidden"
                               name="id"
                               value="<%=t.getId()%>">

                        <button class="tiny danger">
                            削除
                        </button>

                    </form>

                </div>

            </div>

        <%
        }

        if (shown.isEmpty()) {
        %>

            <p class="empty">
                条件に一致する課題がありません。
            </p>

        <%
        }
        %>

        </div>


    <!-- ================================= -->
    <!-- リマインド・通知 -->
    <!-- ================================= -->

    <% } else if ("reminders".equals(pageName)) { %>

        <h1>リマインド・通知</h1>

        <p>
            通知タイミングの確認と、AIリマインドのデモを試せます。
        </p>


        <div class="panel">

            <h2>✨ AI逆算リマインド</h2>

            <p>
                期限と予想作業時間から、取り組む順番を提案
            </p>

            <p class="muted small">
                ※ プロトタイプです。
                AIによる実際の推論・プッシュ通知送信は未接続です。
            </p>

            <form method="post" action="<%=ctx%>/task">

                <input type="hidden" name="action" value="ai">

                <button class="primary">
                    AIリマインドを試す
                </button>

            </form>

        </div>


        <h2 class="section-title">通知履歴</h2>

        <% for (String n : data.getNotifications()) { %>

            <div class="notification">
                ♧　<%=n%>
                <small>デモ</small>
            </div>

        <% } %>


        <h2 class="section-title">
            通常通知の設定（画面モック）
        </h2>

        <div class="panel">

            ◷　期限の前日

            <small>
                提出期限の1日前に通知
            </small>

            <hr>

            ◷　期限当日

            <small>
                提出期限当日の朝に通知
            </small>

        </div>


    <!-- ================================= -->
    <!-- マイページ -->
    <!-- ================================= -->

    <% } else if ("profile".equals(pageName)) { %>

        <h1>マイページ</h1>

        <p>
            登録情報を確認・変更できます。
        </p>


        <form class="panel profile-form"
              method="post"
              action="<%=ctx%>/profile">

            <div class="avatar">♙</div>

            <label>
                氏名

                <input name="name"
                       value="<%=data.getUserName()%>"
                       required>
            </label>

            <label>
                メールアドレス

                <input type="email"
                       name="email"
                       value="<%=data.getUserEmail()%>"
                       required>
            </label>

            <button class="primary">
                変更を保存
            </button>

        </form>


        <div class="panel">

            <b>アカウント</b>

            <form method="post" action="<%=ctx%>/profile">

                <input type="hidden"
                       name="action"
                       value="logout">

                <button class="secondary">
                    ログアウト
                </button>

            </form>

            <p class="muted small">
                登録情報はセッション内だけ保持されます。
                DB連携は未実装です。
            </p>

        </div>

    <% } %>

    </div>

</main>

</div>


<!-- ================================= -->
<!-- 課題追加ポップアップ -->
<!-- ================================= -->

<dialog id="addDialog" class="add-dialog">

    <form method="post" action="<%=ctx%>/task">

        <input type="hidden"
               name="action"
               value="save">


        <!-- ポップアップヘッダー -->

        <div class="dialog-header">

            <h2>＋ 新しい課題を追加</h2>

            <button type="button"
                    class="dialog-close"
                    aria-label="閉じる"
                    onclick="document.getElementById('addDialog').close()">

                ×

            </button>

        </div>


        <!-- 入力項目 -->

        <div class="dialog-body">


            <!-- 課題名 -->

            <label>
                課題名 *

                <input type="text"
                       name="title"
                       placeholder="課題名を入力してください"
                       required>
            </label>


            <!-- 授業名 -->

            <label>
                授業名 *

                <input type="text"
                       name="course"
                       placeholder="授業名を入力してください"
                       required>
            </label>


            <!-- 期限 -->

            <label>
                期限 *

                <input type="date"
                       name="due"
                       value="<%=LocalDate.now().plusDays(3)%>"
                       required>
            </label>


            <!-- 作業時間 -->

            <label>
                予想作業時間（分）

                <input type="number"
                       name="estimate"
                       value="60"
                       min="1">
            </label>


            <!-- 優先度 -->

            <label>
                優先度

                <select name="priority">

                    <option value="高">高</option>

                    <option value="普通" selected>
                        普通
                    </option>

                    <option value="低">低</option>

                </select>
            </label>


            <!-- 詳細 -->

            <label>
                課題の詳細

                <textarea name="description"
                          rows="3"
                          placeholder="課題の内容を入力してください"></textarea>
            </label>


        </div>


        <!-- ポップアップフッター -->

        <div class="dialog-footer">


            <!-- キャンセル -->

            <button type="button"
                    class="secondary"
                    onclick="document.getElementById('addDialog').close()">

                キャンセル

            </button>


            <!-- 保存 -->

            <button type="submit" class="primary">

                保存

            </button>


        </div>

    </form>

</dialog>


<!-- ================================= -->
<!-- JavaScript -->
<!-- ================================= -->

<script>

    const addDialog = document.getElementById("addDialog");


    // ダッシュボードから課題追加を押した場合
    // 課題一覧画面に移動してポップアップを開く

    if (window.location.hash === "#openAdd") {

        addDialog.showModal();

    }


    // ポップアップの外側をクリックした場合に閉じる

    addDialog.addEventListener("click", function(event) {

        if (event.target === addDialog) {

            addDialog.close();

        }

    });

</script>


</body>
</html>
