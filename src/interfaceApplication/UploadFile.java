package interfaceApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;

import esayhelper.DBHelper;
import esayhelper.jGrapeFW_Message;

@WebServlet(name = "Upload", urlPatterns = { "/Upload" })
public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String oldname = ""; // 原名称
	private String newname = ""; // 新名称
	private String type = ""; // 类型
	private String ExtName = ""; // 扩展名
	private String size = ""; // 文件大小
	
	public UploadFile() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		response.addHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
//		response.setContentType("application/json");
//		response.setCharacterEncoding("utf-8");
		String msg ="";
		try {
			// String path = request.getParameter("path");
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if (isMultipart) {
				FileItemFactory factory = new DiskFileItemFactory();
//				String path = request.getRealPath("/file/upload");
				String path = this.getServletContext().getRealPath("/WEB-INF/upload");
				if (!new File(path).exists()) {
					new File(path).mkdir();
				}
				ServletFileUpload upload = new ServletFileUpload(factory);
				// 得到所有的表单域，它们目前都被当作FileItem
				List<FileItem> fileItems = upload.parseRequest(request);
				String id = "";
				String fileName = "";
				// 如果大于1说明是分片处理
				int chunks = 1;
				int chunk = 0;
				long filesize=0;
				FileItem tempFileItem = null;
				for (FileItem fileItem : fileItems) {
					if (fileItem.getFieldName().equals("id")) {
						id = fileItem.getString();
					} else if (fileItem.getFieldName().equals("name")) {
						fileName = new String(fileItem.getString().getBytes("ISO-8859-1"), "UTF-8");
					} else if (fileItem.getFieldName().equals("chunks")) {
						chunks = NumberUtils.toInt(fileItem.getString());
					} else if (fileItem.getFieldName().equals("chunk")) {
						chunk = NumberUtils.toInt(fileItem.getString());
					} else if (fileItem.getFieldName().equals("file")) {
						tempFileItem = fileItem;
					}
					filesize +=fileItem.getSize();
				}
				oldname = fileName;
				ExtName = ext(fileName);
				newname = mknew(fileName);
				size = filesize+"";
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("fileoldname", oldname);
				map.put("filenewname", newname);
				map.put("filetype", GetFileType(ExtName));
				map.put("fileextname", ExtName);
				map.put("size", size);
				map.put("fatherid", 0);
				JSONObject object = new JSONObject(map);
				msg = inserts(object);
				// 临时目录用来存放所有分片文件
				String tempFileDir = getTempFilePath(path) + File.separator + id;
				File parentFileDir = new File(tempFileDir);
				if (!parentFileDir.exists()) {
					parentFileDir.mkdirs();
				}
				// 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台(默认每片为5M)
				File tempPartFile = new File(parentFileDir, fileName + "_" + chunk + ".part");
				FileUtils.copyInputStreamToFile(tempFileItem.getInputStream(), tempPartFile);
				// 是否全部上传完成
				// 所有分片都存在才说明整个文件上传完成
				boolean uploadDone = true;
				for (int i = 0; i < chunks; i++) {
					File partFile = new File(parentFileDir, fileName + "_" + i + ".part");
					if (!partFile.exists()) {
						uploadDone = false;
					}
				}
				// 所有分片文件都上传完成
				// 将所有分片文件合并到一个文件中
				if (uploadDone) {
					File destTempFile = new File(path, fileName);
					for (int i = 0; i < chunks; i++) {
						File partFile = new File(parentFileDir, fileName + "_" + i + ".part");
						FileOutputStream destTempfos = new FileOutputStream(destTempFile, true);
						FileUtils.copyFile(partFile, destTempfos);
						destTempfos.close();
					}
					// 得到 destTempFile 就是最终的文件
					// 添加到文件系统或者存储中
					
					// 删除临时目录中的分片文件
					FileUtils.deleteDirectory(parentFileDir);
					// 删除临时文件
//					destTempFile.delete();
					// ResponseUtil.responseSuccess(response, null);
				} else {
					// 临时文件创建失败
					if (chunk == chunks - 1) {
						FileUtils.deleteDirectory(parentFileDir);
						// ResponseUtil.responseFail(response, "500", "内部错误");
					}
				}
			}
		} catch (Exception e) {
		}
		response.getWriter().print(msg);
	}

	private String getTempFilePath(String tempath) {
//		String tempath = "E://temp";
		File file = new File(tempath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return tempath;
	}
	//新文件名称
	public String mknew(String name) {
		String names = ext(name);
		return !names.equals(".") ? (UUID.randomUUID().toString() + "." + names)
				: (UUID.randomUUID().toString());
	}
	//获取扩展名
	public String ext(String name) {
		if (oldname.contains(".")) {
			ExtName = oldname.substring(oldname.lastIndexOf(".") + 1);
		} else {
			ExtName = "";
		}
		return ExtName;
	}
	//插入文件信息到数据库
	public String inserts(JSONObject object){
		DBHelper helper = new DBHelper("mongodb", "file");
		return jGrapeFW_Message.netMSG(0, helper.data(object).insertOnce().toString());
	}
	
	//判断文件类型
	public int GetFileType(String extname){
		int type;
		switch (extname.toLowerCase()) {
		//图片
		case "png":
		case "jpg":
		case "gif":
		case "jpeg":
		case "tiff":
		case "raw":
		case "bmp":
			type = 1;
			break;
		//视频
		case "avi":
		case "rmvb":
		case "rm":
		case "mkv":
		case "mp4":
		case "wmv":
		case "ogg":
			type = 2;
			break;
		//文档
		case "doc":
		case "docx":
		case "wps":
		case "xls":
		case "ppt":
		case "txt":
		case "rar":
		case "htm":
		case "html":
		case "pdf":
		case "dwg":
		case "exe":
			type = 3;
			break;
		//音频
		case "mp3":
		case "wav":
		case "wma":
			type = 4;
			break;
		//其他
		default:
			type=99;
			break;
		}
		return type;
	}
}
