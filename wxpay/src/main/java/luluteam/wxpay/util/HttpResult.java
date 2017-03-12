package luluteam.wxpay.util;

import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

public class HttpResult {

	private List<Cookie> cookies;
	 
    private HashMap<String, Header> headers;
 
    private int statusCode;
 
    private String body;
    
	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public HashMap<String, Header> getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headerAll) {
		headers = new HashMap<String, Header>();
        for (Header header : headerAll) {
        	headers.put(header.getName(), header);
        }
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
    
	@Override
    public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("======================= HttpResult toString start ========================\n");
		sb.append("----- statusCode: " + statusCode + "\n");
    	if(headers != null) {
    		sb.append("----- headers:\n");
    		for(String key : headers.keySet()) {
    			sb.append("\t" + key + " : " + headers.get(key) + "\n");
    		}
    	}
    	if(cookies != null) {
    		sb.append("----- cookies:\n");
    		for(Cookie cookie : cookies) {
    			sb.append("\t" + cookie.getName() + " : " + cookie.getValue() + "\n");
    		}
    	}
    	sb.append("======================= body start ========================\n");
		sb.append(body);
    	sb.append("======================= body end ========================\n");
    	sb.append("======================= HttpResult toString end   =======================");
    	
    	return sb.toString();
    }
	
	public String getCookieValue(String cookieName) {
		
		if(cookies.isEmpty()) {
			return null;
		}
		
		for(Cookie cookie : cookies) {
			if(cookie.getName().equals(cookieName)) {
				return cookie.getValue();
			}
		}
		
		return null;
	}
}
