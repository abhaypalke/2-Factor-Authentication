<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Forgot Password</title>

<style>
body{
    font-family: Arial, sans-serif;
    background:#f4f4f4;
}

.container{
    width:350px;
    margin:80px auto;
    padding:20px;
    background:white;
    border-radius:8px;
    box-shadow:0 0 10px rgba(0,0,0,.1);
}

input{
    width:100%;
    padding:10px;
    margin:8px 0;
    box-sizing:border-box;
}

button{
    width:100%;
    padding:10px;
    background:#dc3545;
    color:white;
    border:none;
    cursor:pointer;
}

button:hover{
    background:#bb2d3b;
}

.error{
    color:red;
    font-size:13px;
}

.link{
    margin-top:15px;
    text-align:center;
}
</style>

</head>

<body>

<div class="container">

<h2>Forgot Password</h2>

<p>Enter your registered email address to receive a password reset link.</p>

<form:form action="/forgot-password"
           method="post"
           modelAttribute="forgotPasswordRequest">

    <label>Email</label>

    <form:input path="email"/>
    <form:errors path="email" cssClass="error"/>

    <br><br>

    <button type="submit">
        Send Reset Link
    </button>

</form:form>

<div class="link">
    <a href="/login">Back to Login</a>
</div>

</div>

</body>
</html>