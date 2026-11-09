<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
    <% if (request.getAttribute("error") != null) { %><div class="alert"><%=request.getAttribute("error")%></div><% } %>
    <form method="post" action="<%=request.getContextPath()%>/auth">
      <input type="hidden" name="mode" value="login">
      <label>学生ID</label><input name="studentId" value="student01" required>
      <label>メールアドレス</label><input type="email" name="email" value="student@example.com">
      <label>パスワード</label><input type="password" name="password" value="password123" required>
      <button class="primary full" type="submit">ログイン</button>
    </form>
    <p class="muted small">プロトタイプ：入力内容の実認証・DB保存は行いません。</p>
    <details><summary>アカウント作成</summary>
      <form method="post" action="<%=request.getContextPath()%>/auth">
        <input type="hidden" name="mode" value="signup">
        <label>氏名</label><input name="name" required>
        <label>メールアドレス</label><input type="email" name="email" required>
        <label>パスワード（8文字以上）</label><input type="password" name="password" minlength="8" required>
        <button class="primary full" type="submit">登録して始める</button>
      </form>
    </details>
  </section>
</div>
</body></html>