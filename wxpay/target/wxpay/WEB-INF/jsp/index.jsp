<%--
  Created by IntelliJ IDEA.
  User: Hyman
  Date: 2016/4/25
  Time: 18:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

hello world 2

<br><br><br>

<form action="http://i-test.com.cn/yjy/file/upload" enctype="multipart/form-data" method="post">
<%--<form action="http://localhost:8080/yjy/file/upload" enctype="multipart/form-data" method="post">--%>
    <input type="file" name="file" value="">
    <input type="hidden" name="levelId" value="2">
    <input type="hidden" name="userId" value="beea001551bf5f29b0000">
    <input type="hidden" name="money" value="10">
    <input type="submit" name="submit" value="提交"/>
</form>

</body>
</html>
