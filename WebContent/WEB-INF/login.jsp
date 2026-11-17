<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="campustask.AppUtil" %>
<%
    String error = (String) request.getAttribute("error");
    boolean signup = "signup".equals(request.getAttribute("mode"));
    String studentId = AppUtil.escape((String) request.getAttribute("studentId"));
    String name = AppUtil.escape((String) request.getAttribute("name"));
    String email = AppUtil.escape((String) request.getAttribute("email"));
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Campus Task - ログイン</title><link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body class="auth-body">
<div class="auth-wrap">
  <section class="auth-brand">
    <div class="brand-icon">🎓</div><div class="brand-bottom">
      <h1>Campus Task</h1><h2>課題を、もっとスマートに。</h2>
      <p>授業ごとの課題と期限をまとめて管理。リマインドで提出忘れを防ぎましょう。</p>
      <p>✓ 課題管理・期限通知・学習計画</p>
    </div>
  </section>
  <section class="auth-form">
    <h1>おかえりなさい</h1><p class="muted">ログインして課題管理を始めましょう。</p>
    <% if (error != null) { %><div class="alert"><%=AppUtil.escape(error)%></div><% } %>
    <form method="post" action="<%=request.getContextPath()%>/auth">
      <input type="hidden" name="mode" value="login">
      <label>学生ID</label><input name="studentId" value="<%= signup ? "" : studentId %>" maxlength="20" required autofocus>
      <label>パスワード</label><input type="password" name="password" required>
      <button class="primary full" type="submit">ログイン</button>
    </form>
    <details <%= signup ? "open" : "" %>><summary>アカウント作成</summary>
      <form method="post" action="<%=request.getContextPath()%>/auth">
        <input type="hidden" name="mode" value="signup">
        <label>学生ID（半角英数字・_・- の3〜20文字）</label>
        <input name="studentId" value="<%= signup ? studentId : "" %>" pattern="[A-Za-z0-9_\-]{3,20}" maxlength="20" required>
        <label>氏名</label><input name="name" value="<%= signup ? name : "" %>" maxlength="50" required>
        <label>メールアドレス</label><input type="email" name="email" value="<%= signup ? email : "" %>" maxlength="255" required>
        <label>パスワード（8文字以上）</label><input type="password" name="password" minlength="8" required>
        <button class="primary full" type="submit">登録して始める</button>
      </form>
    </details>
  </section>
</div>
</body></html>
