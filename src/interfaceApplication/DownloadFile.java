package interfaceApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet(name = "Download", urlPatterns = { "/Download" })
public class DownloadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public DownloadFile() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String downfile = request.getParameter("downPath"); // 得到要下载的文件名
			String downpath = this.getServletContext().getRealPath("/WEB-INF/upload");// 要下载文件的路径
			File file = new File(downpath + "\\" + downfile);
			if (!file.exists()) {
				return;
			} else {
				String realname = downfile.substring(downfile.indexOf("_") + 1);
				response.setHeader("content-disposition",
						"attachment;filename=" + URLEncoder.encode(realname, "UTF-8"));
				FileInputStream in = new FileInputStream(downpath + "\\" + downfile);
//				FileOutputStream os = new FileOutputStream("C://downfile//" + downfile);
				OutputStream out = response.getOutputStream();
				byte[] buffer = new byte[4 * 1024];
				int read;
				while ((read = in.read(buffer)) > 0) {
//					os.write(buffer, 0, read);
					out.write(buffer, 0, read);
				}
//				os.close();
				out.close();
				in.close();
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
