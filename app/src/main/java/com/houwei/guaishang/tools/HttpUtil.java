package com.houwei.guaishang.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.util.Log;

public class HttpUtil {
//	http://zd.liexianghudong.com/index.php/Api/
	public static final String IP = HttpUtil.IP_NOAPI + "index.php/Api/";
	// 115.159.75.163
	public static final String IP_NOAPI = "http://www.guaishangfaming.com/";
//	public static final String IP_NOAPI = "http://zd.liexianghudong.com/";
//	public static final String IP = HttpUtil.IP_NOAPI + "api/";
//	// 115.159.75.163
//	public static final String IP_NOAPI = "http://120.76.190.118/";

	// http请求时候的md5加密用的秘钥，开发者（包括android，ios，php）请自行统一修改，以确保app网络安全
	public static final String SIG_KEY = "iTopic2015";

	public static final String SHARE_TOPIC_IP = "http://www.guaishangfaming.com/share/topic?id=";
//	public static final String SHARE_TOPIC_IP = "http://zd.liexianghudong.com/index.php/share/topic?id=";

	//微信支付key
	public static final String WECHATPAY_KEY = "wxfeff4f7bfe9d1282";
//	//微信支付key
//	public static final String WECHATPAY_KEY = "wx36d2317425abaf8f";

	/**
	 * 拼接http请求参数，并加密访问服务器，若不想加密，请查看getDataWithOutSig方法
	 * 
	 * 加密规则：比如要传递的参数为 sex：1 name：张三 age：25 第一步，先按key的首字母排序 并拼接成 String buffermd5
	 * = “age=25&name=张三&sex=1” 第二步，buffermd5再拼接上sig = SIG_KEY。结果为 buffermd5 =
	 * “age=25&name=张三&sex=1&sig=iTopic2015”
	 * 第三步，将buffermd5做md5加密，得到一个32位小写string
	 * 第四步，将上一步得到的string当做一个参数传给服务器，key是“sig”
	 */
	public static String getData(Map<String, String> map)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
				map.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
					Map.Entry<String, String> o2) {
				// return (o2.getValue() - o1.getValue());
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		StringBuffer buffer = new StringBuffer();
		StringBuffer buffermd5 = new StringBuffer();
		for (int i = 0; i < infoIds.size(); i++) {
			String key = infoIds.get(i).getKey();
			buffer.append(key);
			buffermd5.append(key);
			buffer.append("=");
			buffermd5.append("=");

			String value = infoIds.get(i).getValue();
			value = value == null ? "" : value;
			buffer.append(URLEncoder.encode(value, "utf-8"));
			buffermd5.append(value);
			buffer.append("&");
			buffermd5.append("&");
		}

		buffer.append("sig");
		buffermd5.append("sig");
		buffer.append("=");
		buffermd5.append("=");
		buffer.append(getMD5(buffermd5.toString() + SIG_KEY));

		return buffer.toString();
	}
	public static String getDataUnSig(Map<String, String> map) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
							   Map.Entry<String, String> o2) {
				// return (o2.getValue() - o1.getValue());
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		StringBuffer buffer = new StringBuffer();
		int size = infoIds.size();
		for (int i = 0; i < (size-1); i++) {
			String key = infoIds.get(i).getKey();
			buffer.append(key);
			buffer.append("=");

			String value = infoIds.get(i).getValue();
			value = value == null ? "" : value;
			buffer.append(URLEncoder.encode(value, "utf-8"));
			buffer.append("&");
		}
		String key = infoIds.get(size-1).getKey();
		buffer.append(key);
		buffer.append("=");
		String value = infoIds.get(size-1).getValue();
		value = value == null ? "" : value;
		buffer.append(URLEncoder.encode(value, "utf-8"));

		//Log.i("WXCH","buffer:"+buffer.toString());
		return buffer.toString();
	}

	public static String getMD5(String val) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String sb = MD5Tool.getMD5(val);
		return sb.toLowerCase();
	}

	public static String getMD5WithCatch(String val) {
		String sb = val;
		try {
			sb = MD5Tool.getMD5(val);
			return sb.toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * 采用不加密的请求方式去拼接参数
	 */
	public static String getDataWithOutSig(Map<String, String> map)
			throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> ite = map.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			String value = map.get(key);
			buffer.append(key);
			buffer.append("=");
			buffer.append(URLEncoder.encode(value, "utf-8"));
			if (ite.hasNext()) {
				buffer.append("&");
			}
		}

		return buffer.toString();
	}

	public static String getDataWithoutEncoder(Map<String, String> map)
			throws UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> ite = map.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			String value = map.get(key);
			buffer.append(key);
			buffer.append("=");
			buffer.append(value);
			if (ite.hasNext()) {
				buffer.append("&");
			}
		}
		return buffer.toString();
	}

	/**
	 * 发出post请求
	 * 
	 * @param data
	 *            是通过getData方法返回的string
	 * @param serverUrl
	 *            请求的url
	 * @return
	 * @throws IOException
	 */
	public static String postMsg(String data, String serverUrl)
			throws IOException {
		LogUtil.i("postMsg = " + serverUrl);
		LogUtil.i("" + data);
		String callback = "";
		URL url = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		HttpURLConnection httpUrlConnection = null;
		url = new URL(serverUrl);
		httpUrlConnection = (HttpURLConnection) url.openConnection();

		httpUrlConnection.setRequestMethod("POST");
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setDoOutput(true);
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setRequestProperty("Charset", "UTF-8");
		httpUrlConnection.setConnectTimeout(15 * 1000);
		httpUrlConnection.setReadTimeout(15 * 1000);
		if (data != null) {
			outputStream = httpUrlConnection.getOutputStream();
			outputStream.write(data.getBytes());
		}

		if (httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_OK
				|| httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED
				|| httpUrlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
			inputStream = httpUrlConnection.getInputStream();
		} else {
			inputStream = httpUrlConnection.getErrorStream();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream, "utf-8"));
		StringBuilder buffer = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		callback = buffer.toString();
		//Log.i("WXCH","callback:" + callback);

		if (outputStream != null) {
			outputStream.close();
			outputStream = null;
		}

		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
		if (httpUrlConnection != null) {
			httpUrlConnection.disconnect();
			httpUrlConnection = null;
		}
		LogUtil.e(callback);
		return callback;
	}

	/**
	 * 发出get请求
	 */
	public static String getMsg(String serverUrl) throws IOException {
		LogUtil.i("getMsg = " + serverUrl);
		String callback = "";
		URL url = null;
		InputStream inputStream = null;
		HttpURLConnection httpUrlConnection = null;
		url = new URL(serverUrl);
		httpUrlConnection = (HttpURLConnection) url.openConnection();

		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.setDoInput(true);
		httpUrlConnection.setUseCaches(false);
		httpUrlConnection.setConnectTimeout(15 * 1000);
		httpUrlConnection.setReadTimeout(15 * 1000);

		int responseCode = httpUrlConnection.getResponseCode();
		if (responseCode != 200) {
			return callback;
		}
		inputStream = httpUrlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream, "utf-8"));
		StringBuilder buffer = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		callback = buffer.toString();
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
		LogUtil.i(callback);
		return callback;
	}

	/* 上传 单个文件至服务器的方法 */
	public static String uploadFile(String actionUrl, File tempPhotoFile,String userid)
			throws Exception {
		LogUtil.i(actionUrl);

		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = UUID.randomUUID().toString();
		URL url = new URL(actionUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		/* 允许Input、Output，不使用Cache */
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		/* setRequestProperty */
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);
		/* 设置传送的method=POST */
		con.setRequestMethod("POST");
		con.setConnectTimeout(20 * 1000);
		con.setReadTimeout(20 * 1000);
		/* 设置DataOutputStream */
		DataOutputStream ds = new DataOutputStream(con.getOutputStream());

		/* 开始拼接秘钥sig参数，若不需要加密，这一段可以删除 */
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("userid",userid );
//		paramsMap.put("sig", HttpUtil.getSig(paramsMap));
		Iterator<String> ite = paramsMap.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			String value = paramsMap.get(key);
			StringBuffer params = new StringBuffer();
			params.append("--" + boundary + "\r\n");
			params.append("Content-Disposition: form-data; name=\"" + key
					+ "\"\r\n\r\n");
			params.append(value);
			params.append("\r\n");
			ds.write(params.toString().getBytes());
		}
		/* 秘钥sig参数添加完毕 */

		ds.writeBytes(twoHyphens + boundary + end);
		ds.writeBytes("Content-Disposition: form-data; "
				+ "name=\"photo\";filename=\"" + " Content-Type:image/jpeg "
				+ tempPhotoFile.getName() + "\"" + end);
		ds.writeBytes(end);
		/* 取得文件的FileInputStream */
		FileInputStream fStream = new FileInputStream(tempPhotoFile);
		/* 设置每次写入1024bytes */
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int length = -1;
		/* 从文件读取数据至缓冲区 */
		while ((length = fStream.read(buffer)) != -1) {
			/* 将资料写入DataOutputStream中 */
			ds.write(buffer, 0, length);
		}
		ds.writeBytes(end);
		ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
		/* close streams */
		fStream.close();
		ds.flush();

		/* 取得Response内容 */
		InputStream inputStream = con.getInputStream();

		BufferedInputStream bs = new BufferedInputStream(inputStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(bs));
		int temp = -1;
		char[] b = new char[1];
		StringBuilder resultbuffer = new StringBuilder();
		while ((temp = reader.read(b, 0, b.length)) != -1) {
			resultbuffer.append(String.valueOf(b));
		}
		reader.close();
		bs.close();
		String callback = resultbuffer.toString();
		LogUtil.i(callback);
		return callback;
	}

	/**
	 * 得到sig值 这个方法只给上传图片（upload）接口用
	 * 
	 * @param map
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	private static String getSig(Map<String, String> map)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
				map.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
					Map.Entry<String, String> o2) {
				// return (o2.getValue() - o1.getValue());
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		StringBuffer buffermd5 = new StringBuffer();
		for (int i = 0; i < infoIds.size(); i++) {
			String key = infoIds.get(i).getKey();
			buffermd5.append(key);
			buffermd5.append("=");
			String value = infoIds.get(i).getValue();
			value = value == null ? "" : value;
			buffermd5.append(value);
			buffermd5.append("&");
		}
		buffermd5.append("sig");
		buffermd5.append("=");
		return getMD5(buffermd5.toString() + SIG_KEY);
	}

	/**
	 * 上传多文件 + 自定义参数
	 * 
	 * @param paramsMap
	 *            参数
	 * @param list
	 *            本地文件路径
	 * @param urlstring
	 *            接口url
	 *           
	 * @return
	 * @throws Exception
	 */
	public static String upload(Map<String, String> paramsMap,
			List<String> list, String urlstring, String form_name,
			String contentType) throws Exception {

		// 定义数据分隔线
		String BOUNDARY = "------------------------7dc2fd5c0894";
		// 定义最后数据分隔线
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();

		URL url = new URL(urlstring);
		//Log.d("CCC","url;"+urlstring);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "Keep-Alive");

		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);

		OutputStream out = new DataOutputStream(conn.getOutputStream());

		Map<String, String> sigMap = new HashMap<String, String>();

		paramsMap.put("sig", HttpUtil.getSig(paramsMap));
		Iterator<String> ite = paramsMap.keySet().iterator();
		while (ite.hasNext()) {
			String key = ite.next();
			String value = paramsMap.get(key);
			// 附带参数
			StringBuffer params = new StringBuffer();
			params.append("--" + BOUNDARY + "\r\n");
			params.append("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n");
			params.append(value);
			params.append("\r\n");

			out.write(params.toString().getBytes());
		}

		int leng = list.size();
		for (int i = 0; i < leng; i++) {
			String fname = list.get(i);
			File file = new File(fname);

			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\""+form_name+"\";filename=\""
					+ file.getName() + "\"\r\n");
			// 这里不能漏掉，根据文件类型来来做处理，由于上传的是图片，所以这里可以写成image/pjpeg
			sb.append("Content-Type:"+contentType+"\r\n\r\n");
			out.write(sb.toString().getBytes());

			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			out.write("\r\n".getBytes());
			in.close();
		}
		out.write(end_data);
		out.flush();
		out.close();

		/* 取得Response内容 */
		InputStream inputStream = conn.getInputStream();
		BufferedInputStream bs = new BufferedInputStream(inputStream);
		BufferedReader reader = new BufferedReader(new InputStreamReader(bs));
		int temp = -1;
		char[] b = new char[1];
		StringBuilder resultbuffer = new StringBuilder();
		while ((temp = reader.read(b, 0, b.length)) != -1) {
			resultbuffer.append(String.valueOf(b));
		}
		reader.close();
		bs.close();
		String callback = resultbuffer.toString();
		LogUtil.i(callback);
		return callback;

	}

	/**
	 * 上传多文件 + 自定义参数
	 * 
	 * @param paramsMap
	 *            参数
	 * @param list
	 *            本地文件路径
	 * @param urlstring
	 *            接口url
	 * @return
	 * @throws Exception
	 */
	public static String upload(Map<String, String> paramsMap, List<String> list, String urlstring) throws Exception{
		return HttpUtil.upload(paramsMap, list, urlstring, "photo[]", "image/pjpeg");
	}

}
