<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
${user}
${message}
<#assign info={'姓名':'张三','年龄':'18','性别':'男'}/>
<br>
<br>${info.年龄}
<hr>${info.性别}
${info.姓名}
<hr>
<#--遍历数组-->
<#list people as p>
姓名：${p.name}<hr>
年龄：${p.age}<br>
性别：${p.sex}<br>
</#list>

<#--获取数字-->
${num}
${num?c}
<br>

<#--获取日期-->
${now?date}
${now?time}
${now?datetime}
${now?string("yyyy年MM月dd天 E")}

</body>
</html>