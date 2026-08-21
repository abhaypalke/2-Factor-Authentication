<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reset Password</title>

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
    background:#198754;
    color:white;
    border:none;
    cursor:pointer;
}

button:hover{
    background:#157347;
}

.error{
    color:red;
    font-size:13px;
}
</style>

</head>

<body>

<div class="container">

<h2>Reset Password</h2>

<form:form action="/reset-password"
           method="post"
           modelAttribute="resetPasswordRequest">

    <form:hidden path="token"/>

    <label>New Password</label>
    <form:password path="password"/>
    <form:errors path="password" cssClass="error"/>

    <label>Confirm Password</label>
    <form:password path="confirmPassword"/>
    <form:errors path="confirmPassword" cssClass="error"/>

    <br><br>

    <button type="submit">
        Reset Password
    </button>

</form:form>

</div>

</body>
</html>