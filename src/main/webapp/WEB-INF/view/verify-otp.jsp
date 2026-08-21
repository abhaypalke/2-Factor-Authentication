<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Verify OTP</title>

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

input, select{
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

.actions{
    margin-top:15px;
    text-align:center;
}
</style>

</head>

<body>

<div class="container">

<h2>Verify OTP</h2>

<form:form action="/verify-otp"
           method="post"
           modelAttribute="otpRequest">

    <form:hidden path="email"/>
    <form:hidden path="purpose"/>

    <label>Enter OTP</label>
    <form:input path="otp"/>

    <form:errors path="otp" cssClass="error"/>

    <br><br>

    <button type="submit">Verify OTP</button>

</form:form>

<div class="actions">

    <form action="/resend-otp" method="post">

        <input type="hidden" name="email" value="${otpRequest.email}">
        <input type="hidden" name="purpose" value="${otpRequest.purpose}">

        <button type="submit">
            Resend OTP
        </button>

    </form>

</div>

</div>

</body>
</html>