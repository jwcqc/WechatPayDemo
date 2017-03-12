package luluteam.wxpay.util;

import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;

public class HttpUtil {

	// User-Agent
	public static final String USERAGENT_FIREFOX = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0";  
	public static final String USERAGENT_IE = "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko";  
	
	private CloseableHttpClient httpClient;

	private BasicCookieStore cookieStore;
	private HttpGet get;
	private HttpPost post;

    public static StringBuffer httpsRequest(String requestUrl, String requestMethod, String output) throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod(requestMethod);
        if (null != output) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(output.getBytes("UTF-8"));
            outputStream.close();
        }
        // 从输入流读取返回内容
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        StringBuffer buffer = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        inputStream = null;
        connection.disconnect();
        return buffer;
    }

	
	public HttpResult doGet(String url, Map<String, String> headers, Map<String, String> params) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException {

		if (url == null|| url.equals("")) {
			return null;
		}

		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
		cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.setSSLSocketFactory(sslsf).build();

		HttpResult result = null;
		try {

			url = url + "?" + parseParams(params);
			HttpGet httpget = new HttpGet(url);
			httpget.setHeaders(parseHeader(headers));

			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					result = new HttpResult();
					result.setCookies(cookieStore.getCookies());
					result.setStatusCode(response.getStatusLine().getStatusCode());
					result.setHeaders(response.getAllHeaders());
					result.setBody(EntityUtils.toString(entity));
				}

			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

		return result;
		
	}

	public HttpResult doPost(String url, Map<String, String> headers, Map<String, String> postData, String encoding) throws Exception {

		if (url == null|| url.equals("")) {
			return null;
		}
		if (encoding == null|| encoding.equals("")) {
			encoding = "utf-8";
		}
		
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
		cookieStore = new BasicCookieStore();
		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.setSSLSocketFactory(sslsf).build();

		post = new HttpPost(url);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		for (String tmp : postData.keySet()) {
			list.add(new BasicNameValuePair(tmp, postData.get(tmp)));
		}
		post.setEntity(new UrlEncodedFormEntity(list, encoding));
		post.setHeaders(parseHeader(headers));

		CloseableHttpResponse response = httpClient.execute(post);
		HttpEntity entity = response.getEntity();

		HttpResult result = new HttpResult();
		result.setCookies(cookieStore.getCookies());
		result.setStatusCode(response.getStatusLine().getStatusCode());
		result.setHeaders(response.getAllHeaders());
		result.setBody(EntityUtils.toString(entity, encoding));

		close(entity, response);

		return result;
	}

	private String parseParams(Map<String, String> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (String key : params.keySet()) {
			sb.append(key + "=" + params.get(key) + "&");
		}
		return sb.substring(0, sb.length() - 1);

	}

	private Header[] parseHeader(Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return getDefaultHeaders();
		}

		Header[] retHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet()) {
			retHeader[i++] = new BasicHeader(str, headers.get(str));
		}
		return retHeader;
	}

	private Header[] getDefaultHeaders() {
		Header[] headers = new BasicHeader[3];
		headers[0] = new BasicHeader("User-Agent", USERAGENT_IE);
		headers[1] = new BasicHeader("Accept-Encoding", "gzip, deflate");
		headers[2] = new BasicHeader("Accept-Language", "en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3");
		return headers;
	}

	private void close(HttpEntity entity, CloseableHttpResponse response) {
		try {
			if (entity != null) {
				InputStream input = entity.getContent();
				input.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	
	/**
	 * 下载文件
	 * @param url 下载文件的链接
	 * @param destFile 包含路径的目标文件名
	 * @param headers 请求头
	 * @return 
	 */
	public HttpResult downloadFile(String url, String destFile, Map<String, String> headers) throws Exception {
		
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
		BasicCookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).setSSLSocketFactory(sslsf).build();
		
		HttpGet get = new HttpGet(url);
		get.setHeaders(parseHeader(headers));
		InputStream input = null;
		CloseableHttpResponse response = null;
		HttpResult result = null;
		
		try {
			response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			input = entity.getContent();
			File file = new File(destFile);
			
			FileOutputStream fos = new FileOutputStream(file);
			int len = -1;
			byte[] tmp = new byte[1024];
			while((len=input.read(tmp)) != -1) {
				fos.write(tmp, 0, len);
			}
			fos.flush();
			fos.close();
			
			result = new HttpResult();
			result.setCookies(cookieStore.getCookies());
			result.setStatusCode(response.getStatusLine().getStatusCode());
			result.setHeaders(response.getAllHeaders());
			result.setBody(EntityUtils.toString(entity, Consts.UTF_8));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(input != null) {
					input.close();
				}
				if(response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
