<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="http://localhost:8080/FileUpload/css/webuploader.css">
<script type="text/javascript" src="http://localhost:8080/FileUploadjs/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="http://localhost:8080/FileUpload/js/webuploader.js"></script>
<title>Insert title here</title>
</head>
<body>
	<form action="Upload" method="post" enctype="multipart/form-data"> 
    文件上传：<input type="file" name="fileupload"/><br/>
    文件上传：<input type="file" name="fileuploads"/>  <br/>
    描述：<input type="text" name="desc"/>  <br/>
    <input type="submit" value="submit"/>  
    </form>
</body>
</html>