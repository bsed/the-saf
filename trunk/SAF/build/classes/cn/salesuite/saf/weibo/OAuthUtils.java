package cn.salesuite.saf.weibo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OAuthUtils {
	public static final String CALLBACK="snsoauth://callback";
	public static final String OAUTH_TOKEN_URL_SINA="http://api.t.sina.com.cn/oauth/authorize?oauth_token=";
	public static final String OAUTH_TOKEN_URL_QQ="http://open.t.qq.com/cgi-bin/authorize?oauth_token=";
	public static String encodeMap(HashMap<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		Set<String> set = parameters.keySet();
		ArrayList<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		Iterator<String> iterator = list.iterator();
		while (true) {
			if (!iterator.hasNext())
				return sb.substring(0, sb.length() - 1).toString();
			String str1 = (String) iterator.next();
			String str2 = encode(parameters.get(str1));
			sb.append(str1).append("=").append(str2).append("&");
		}
	}

	public static String decode(String paramString) {
		try {
			String str = URLDecoder.decode(paramString, "UTF-8");
			return str;
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			throw new OAuthException("connection error,please retry",
					localUnsupportedEncodingException);
		}
	}

	public static String encode(String paramString) {
		try {
			String str = URLEncoder.encode(paramString, "UTF-8").replace("*",
					"%2A").replace("+", "%20").replace("%7E", "~");
			return str;
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
			throw new OAuthException("connection error,please retry",
					localUnsupportedEncodingException);
		}
	}
	public static String[] parse(String str) {
		Matcher localMatcher = Pattern.compile("\\S*oauth_token=(\\S*)&oauth_verifier=(\\S*)(&(.*))?")
				.matcher(str);
		if (localMatcher.matches()) {
			String str1 = localMatcher.group(1);
			String str2 = localMatcher.group(2);
			return new String[]{str1,str2};
		}
		return null;
	}

}
