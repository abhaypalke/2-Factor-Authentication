<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login</title>

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
    background:#0d6efd;
    color:white;
    border:none;
    cursor:pointer;
}

button:hover{
    background:#0b5ed7;
}

.error{
    color:red;
    font-size:13px;
}

.links{
    margin-top:15px;
    display:flex;
    justify-content:space-between;
}
</style>

</head>

<body>

<div class="container">

<h2>Login</h2>

<form:form action="/login"
           method="post"
           modelAttribute="loginRequest">

    <label>Email</label>
    <form:input path="email"/>
    <form:errors path="email" cssClass="error"/>

    <label>Password</label>
    <form:password path="password"/>
    <form:errors path="password" cssClass="error"/>

    <br><br>

    <button type="submit">Login</button>

</form:form>

<div class="links">
    <a href="/register">Create Account</a>
    <a href="/forgot-password">Forgot Password?</a>
</div>

</div>

</body>
</html>