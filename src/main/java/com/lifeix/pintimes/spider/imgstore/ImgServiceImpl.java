package com.lifeix.pintimes.spider.imgstore;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ImgServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImgServiceImpl.class);

	/** 访问前缀 */
	private String imgPrefix = "http://imgs.pintimes.com/";
	
	/** 下载图片临时目录 */
	@Value("${db.image.path}")
	private String tempImgPath;
	
	/** 云服务bucketName */
	private String bucketName = "pintimes-imgs";
	

	private OSSClient client;

	public Map<String, String> convertImgPath(List<String> urls) {
		Map<String, String> result = new HashMap<String, String>(urls.size());
		for (String url : urls) {
			String newUrl;
			String tempPath;
			File tempFile = null;
			try {
				tempPath = downLoadImage(getTempImgPath(), url);
				tempFile = new File(tempPath);
				newUrl = uploadImg(tempPath);
			} catch (Exception e) {
				LOGGER.error("下载转换图片出错.", e);
				newUrl = url;
			} finally {
				if (tempFile != null && tempFile.exists()) {
					tempFile.delete();
				}
			}
			result.put(url, newUrl);
		}
		return result;
	}

	private String uploadImg(String filePath) throws IOException {

		// 获取指定文件的输入流
		File file = new File(filePath);
		InputStream content = null;
		if (file.length()>80*1024){//当图片大于80k时 压缩
			content = imageResize(file);
		}else {
			content = new FileInputStream(file);
		}

		// 创建上传Object的Metadata
		ObjectMetadata meta = new ObjectMetadata();
		// 必须设置ContentLength
		meta.setContentLength(file.length());
		meta.setContentType("image/jpeg");
		meta.setContentDisposition("inline");

		String key = "pintimes/" + UUID.randomUUID().toString();
		// 上传Object.
		PutObjectResult result = client.putObject(bucketName, key, content, meta);
		// 打印ETag
//		System.out.println(result.getETag());
		return getImgPrefix() + key;
	}

	public static void main(String[] args) throws IOException {
//		String path = "/home/lifeix/temp/d3/imgs";
//		String url = "http://image.l99.com/653/1431865064217_9xytz9.jpg";
//		// String filePath =
//		// "/home/lifeix/temp/d3/imgs/1431943157057_1431865064217_9xytz9.jpg";
//		ImgServiceImpl service = new ImgServiceImpl();
//		service.setTempImgPath("/home/lifeix/temp/d3/imgs");
//
//		List<String> originUrls = new ArrayList<String>();
//		originUrls.add("http://image.l99.com/be1/1431951683360_w6x99y.jpg");
//		originUrls.add("http://photo.l99.com/bigger/12e/1432021970961_mw585h.png");
//
//		Map<String, String> newUrls = service.convertImgPath(originUrls);
//		// String filePath = service.downLoadImage(path, url);
//		// service.uploadImg(filePath);
//		System.out.println(newUrls);
//		System.out.println("over!!");

		File file = new File("/Users/neoyin/Downloads/JPEG/00060e9d-42f4-40a7-9033-c932b35c3189.jpeg");
		Image image = new Image(file);

		System.out.println(file.length());
//
		image.resize(80);
		image.saveAs("/Users/neoyin/Downloads/JPEG/2_1.jpg");
		BufferedImage buffer =  image.getAsBufferedImage();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(buffer, image.getType(), os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());

	}

	private InputStream imageResize(File file) throws IOException {
		Image image = new Image(file);
		image.resize(80);
		BufferedImage buffer =  image.getAsBufferedImage();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(buffer, image.getType(), os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		return is;
	}


	private String downLoadImage(String path, String url) {
		try {

			String filename = url.substring(url.lastIndexOf("/") + 1);
			url = url.replaceAll(" ", "%20");

			URL picUrl = new URL(url);
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) picUrl.openConnection();
			conn.setConnectTimeout(30000);

			InputStream inputStream = conn.getInputStream();
			DataInputStream in = new DataInputStream(inputStream);
			String localFile = path + "/" + UUID.randomUUID().toString() + "_" + filename;

			FileOutputStream f = new FileOutputStream(localFile);
			DataOutputStream out = new DataOutputStream(f);
			byte[] buff = new byte[8996];
			int count = 0;
			while ((count = in.read(buff)) > 0) {
				out.write(buff, 0, count);
			}
			out.close();
			in.close();
			conn.disconnect();

			return localFile;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getImgPrefix() {
		return imgPrefix;
	}

	public void setImgPrefix(String imgPrefix) {
		this.imgPrefix = imgPrefix;
	}

	public String getTempImgPath() {
		return tempImgPath;
	}

	public void setTempImgPath(String tempImgPath) {
		this.tempImgPath = tempImgPath;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public OSSClient getClient() {
		return client;
	}

	public void setClient(OSSClient client) {
		this.client = client;
	}

}
