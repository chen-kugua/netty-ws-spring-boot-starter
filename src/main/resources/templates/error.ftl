<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>接口超时告警</title>
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }

        th, td {
            text-align: left;
            padding: 8px;
        }

        th {
            background-color: #333;
            color: white;
        }

        tr:nth-child(even) {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<table>
    <thead>
    <tr>
        <th>项目</th>
        <th>值</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>接口签名：</td>
        <td>${method}</td>
    </tr>
    <tr>
        <td>接口描述：</td>
        <td>${desc}</td>
    </tr>
    <tr>
        <td>接口耗时：</td>
        <td>${speedTime}ms</td>
    </tr>
    <#if err??>
        <tr>
            <td>异常信息：</td>
            <td>${err}</td>
        </tr>
    </#if>
    <tr>
        <td>日志ID：</td>
        <td>${logId}</td>
    </tr>
    </tbody>
</table>
</body>
</html>
