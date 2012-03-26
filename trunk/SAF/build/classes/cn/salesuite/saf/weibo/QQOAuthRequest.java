package cn.salesuite.saf.weibo;

import java.util.HashMap;

public class QQOAuthRequest extends OAuthRequest {

	public QQOAuthRequest(String httpMethod, String url) {
		super(httpMethod, url);
	}

	@Override
	public OAuthToken getToken() {
		String str = OAuthUtils.encodeMap(this.getBodyParams());
		OAuthToken oAuthToken = null;
		try {
			String result = OAuthHttpClient.exePost(mUrl, str);
			oAuthToken = this.extract(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oAuthToken;
	}

	@Override
	public String pushMsg() {
		String str = OAuthUtils.encodeMap(this.getBodyParams());
		String result =null;
		try {
			result = OAuthHttpClient.exePost(mUrl, str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public String pushMsg(String pic) {
		String result =null;
		HashMap<String, String> map = this.getOAuthParams();
		map.putAll(this.getBodyParams());
		try {
			result = http.exePost(mUrl,map,pic);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
