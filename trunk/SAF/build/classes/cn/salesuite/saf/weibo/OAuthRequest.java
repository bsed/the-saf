package cn.salesuite.saf.weibo;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class OAuthRequest {
	protected HashMap<String, String> mBodyParams;
	protected HashMap<String, String> mHeaders;
	protected HashMap<String, String> mOAuthParams;
	protected String mMethod;
	protected String mUrl;
	protected OAuthHttpClient http;

	public OAuthRequest(String httpMethod, String url) {
		mMethod = httpMethod;
		mUrl = url;
		mBodyParams = new HashMap<String, String>();
		mHeaders = new HashMap<String, String>();
		mOAuthParams = new HashMap<String, String>();
		http=new OAuthHttpClient();
	}

	public void addBodyParam(String paramKey, String paramValue) {
		this.mBodyParams.put(paramKey, paramValue);
	}

	public void addHeader(String paramKey, String paramValue) {
		this.mHeaders.put(paramKey, paramValue);
	}

	public void addOAuthParam(String paramKey, String paramValue) {
		this.mOAuthParams.put(paramKey, paramValue);
	}

	public OAuthToken extract(String resultString) {
		Matcher localMatcher = Pattern.compile(
				"oauth_token=(\\S*)&oauth_token_secret=(\\S*?)(&(.*))?")
				.matcher(resultString);
		if (localMatcher.matches()) {
			String str1 = localMatcher.group(1);
			String str2 = localMatcher.group(2);
			return new OAuthToken(str1, str2);
		}
		throw new OAuthException("连接错误，请稍候重试");
	}
	public OAuthToken extract1(String resultString) {
		Matcher localMatcher = Pattern.compile(
				"oauth_token_secret=(\\S*)&oauth_token=(\\S*?)(&(.*))?")
				.matcher(resultString);
		if (localMatcher.matches()) {
			String str1 = localMatcher.group(1);
			String str2 = localMatcher.group(2);
			return new OAuthToken(str2,str1);
		}
		throw new OAuthException("连接错误，请稍候重试");
	}
	public abstract OAuthToken getToken();
	
	public abstract String pushMsg();
	public abstract String pushMsg(String pic);
	/**
	 * @return the mBodyParams
	 */
	public HashMap<String, String> getBodyParams() {
		return mBodyParams;
	}

	/**
	 * @return the mHeaders
	 */
	public HashMap<String, String> getHeaders() {
		return mHeaders;
	}

	/**
	 * @return the mOAuthParams
	 */
	public HashMap<String, String> getOAuthParams() {
		return mOAuthParams;
	}

	/**
	 * @return the mMethod
	 */
	public String getMethod() {
		return mMethod;
	}

	/**
	 * @return the mUrl
	 */
	public String getUrl() {
		return mUrl;
	}
}
