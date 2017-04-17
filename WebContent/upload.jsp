<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="./css/webuploader.css">
<script type="text/javascript" src="./js/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="./js/webuploader.js"></script>
<title>Insert title here</title>
</head>
<body>
	<div id="uploader" class="wu-example">
		<div id="theList" class="uploader-list"></div>
		<div class="ui active progress" style="width: 80%; margin: 0 auto;">
			<div class="bar">
				<div class="progress"></div>
			</div>
		</div>
		<div class="prs"></div>
		<div>
			<div id="picker">选择文件</div>
			<button id="ctlBtn" class="btn btn-default">开始上传</button>
		</div>
	</div>
	<script type="text/javascript">
		var uploader = WebUploader.create({
			swf : './js/Uploader.swf',
			server : 'http://localhost:8080/FileUpload/Upload',
			pick : '#picker',
			chunked : true,//开启分片上传
			chunkSize : 10 * 1024 * 1024,
			threads : 1,//上传并发数
			auto : true

		});
		//用户选择
		uploader.on('fileQueued', function(file) {
			$('#theList').append(
					'<div id="' + file.id + '" class="item">'
							+ '<h4 class="info">' + file.name + '</h4>'
							+ '<p class="state">等待上传...</p>' + '</div>');
		});
		//上传进度
		uploader
				.on(
						'uploadProgress',
						function(file, precentage) {
							var $li = $('#' + file.id), $percent = $li
									.find('.progress .progress-bar');

							// 避免重复创建
							if (!$percent.length) {
								$percent = $(
										'<div class="progress progress-striped active">'
												+ '<div class="progress-bar" role="progressbar">'
												+ '123</div>' + '</div>')
										.appendTo($li).find('.progress-bar');
							}

							$li.find('p.state').text('上传中');

							$percent.css('width', precentage * 100 + '%');
						});
		//文件成功，失败，完成处理
		uploader.on('uploadSuccess', function(file,response) {
			alert(response._raw);
			$('#' + file.id).find('p.state').text('已上传');
		});

		uploader.on('uploadError', function(file) {
			$('#' + file.id).find('p.state').text('上传出错');
		});

		uploader.on('uploadComplete', function(file) {
			$('#' + file.id).find('.progress').fadeOut();
		});
	</script>
</body>
</html>