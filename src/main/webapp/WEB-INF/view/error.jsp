<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Error</title>

<style>

body{
    font-family: Arial, sans-serif;
    background:#f4f4f4;
}

.container{
    width:500px;
    margin:100px auto;
    padding:30px;
    background:white;
    border-radius:8px;
    box-shadow:0 0 10px rgba(0,0,0,.1);
    text-align:center;
}

h2{
    color:#dc3545;
}

.message{
    margin:20px 0;
    color:#555;
}

a{
    text-decoration:none;
    color:#0d6efd;
}

</style>

</head>

<body>

<div class="container">

    <h2>Something Went Wrong</h2>

    <p class="message">
        ${error}
    </p>

    <a href="/login">Go to Login</a>

</div>

</body>
</html>