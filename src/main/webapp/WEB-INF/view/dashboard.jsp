<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Dashboard</title>

<style>

body{
    font-family: Arial, sans-serif;
    background:#f4f4f4;
}

.container{
    width:600px;
    margin:80px auto;
    padding:30px;
    background:white;
    border-radius:8px;
    box-shadow:0 0 10px rgba(0,0,0,.1);
    text-align:center;
}

h2{
    color:#198754;
}

button{
    padding:10px 20px;
    background:#dc3545;
    color:white;
    border:none;
    cursor:pointer;
}

button:hover{
    background:#bb2d3b;
}

</style>

</head>

<body>

<div class="container">

    <h2>Welcome</h2>

    <p>
        Login Successful.
    </p>

    <p>
        You are authenticated using Spring Security.
    </p>

    <form action="/logout" method="post">
        <button type="submit">
            Logout
        </button>
    </form>

</div>

</body>
</html>