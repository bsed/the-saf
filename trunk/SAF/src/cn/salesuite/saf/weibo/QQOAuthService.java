package cn.salesuite.saf.weibo;

import java.util.HashMap;

import android.util.Log;

public class QQOAuthService extends OAuthService {

	public QQOAuthService(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
	}

	private void addBodyParams() {

		mOAuthRequest.addBodyParam("oauth_consumer_key", mConsumerKey);
		mOAuthRequest.addBodyParam("oauth_nonce", getNonce());
		mOAuthRequest.addBodyParam("oauth_signature_method", "HMAC-SHA1");
		mOAuthRequest.addBodyParam("oauth_timestamp", getTimestampInSeconds());
		mOAuthRequest.addBodyParam("oauth_version", "1.0");
	}

	@Override
	public OAuthToken getRequestToken() {
		mOAuthRequest = new QQOAuthRequest("POST",
				"http://open.t.qq.com/cgi-bin/request_token");
		mOAuthRequest.addBodyParam("oauth_callback", OAuthUtils.CALLBACK);
		addBodyParams();
		OAuthToken oauthToken = OAuthConstants.EMPTY_TOKEN;
		String str = getSignature(mOAuthRequest, oauthToken);
		Log.i("signature:",str);
		mOAuthRequest.addBodyParam("oauth_signature", str);

		return mOAuthRequest.getToken();
	}

	@Override
	public OAuthToken getAccessToken(OAuthToken oAuthToken, String verifier) {
		mOAuthRequest = new QQOAuthRequest("POST",
				"http://open.t.qq.com/cgi-bin/access_token");
		mOAuthRequest.addBodyParam("oauth_verifier", verifier);
		addBodyParams();
		mOAuthRequest.addBodyParam("oauth_token", oAuthToken.getToken());
		String str2 = getSignature(mOAuthRequest, oAuthToken);
		mOAuthRequest.addBodyParam("oauth_signature", str2);
		return mOAuthRequest.getToken();
	}

	@Override
	public String pushMsg(HashMap<String,String> params, OAuthToken oAuthToken){
		mOAuthRequest = new QQOAuthRequest("POST","http://open.t.qq.com/api/t/add");
		mOAuthRequest.addBodyParam("content",params.get("content"));
		mOAuthRequest.addBodyParam("format", params.get("format"));
		
		if(params.get("clientip")!=null)mOAuthRequest.addBodyParam("clientip", params.get("clientip"));
		if(params.get("jing")!=null)mOAuthRequest.addBodyParam("jing", params.get("jing"));
		if(params.get("wei")!=null)mOAuthRequest.addBodyParam("wei", params.get("wei"));
		
		addBodyParams();
		mOAuthRequest.addBodyParam("oauth_token", oAuthToken.getToken());
		String str2 = getSignature(mOAuthRequest, oAuthToken);
		mOAuthRequest.addBodyParam("oauth_signature", str2);
		return mOAuthRequest.pushMsg();
	}
	@Override
	public String pushPicMsg(HashMap<String,String> params, OAuthToken oAuthToken){
		mOAuthRequest = new QQOAuthRequest("POST","http://open.t.qq.com/api/t/add_pic");
		mOAuthRequest.addBodyParam("content",params.get("content"));
		mOAuthRequest.addBodyParam("format", params.get("format"));
		
		if(params.get("clientip")!=null)mOAuthRequest.addBodyParam("clientip", params.get("clientip"));
		if(params.get("jing")!=null)mOAuthRequest.addBodyParam("jing", params.get("jing"));
		if(params.get("wei")!=null)mOAuthRequest.addBodyParam("wei", params.get("wei"));
		
		addBodyParams();
		mOAuthRequest.addBodyParam("oauth_token", oAuthToken.getToken());
		String str2 = getSignature(mOAuthRequest, oAuthToken);
		mOAuthRequest.addBodyParam("oauth_signature", str2);
		return mOAuthRequest.pushMsg(params.get("pic"));
	}

}
