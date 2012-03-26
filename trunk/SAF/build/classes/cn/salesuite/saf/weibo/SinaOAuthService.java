package cn.salesuite.saf.weibo;


import java.util.HashMap;


public class SinaOAuthService extends OAuthService {

	public SinaOAuthService(String consumerKey, String consumerSecret) {
		super(consumerKey,consumerSecret);
	}
	public OAuthToken getRequestToken(){
		mOAuthRequest = new SinaOAuthRequest("POST", "http://api.t.sina.com.cn/oauth/request_token");
		mOAuthRequest.addBodyParam("oauth_callback", OAuthUtils.CALLBACK);
		mOAuthRequest.addBodyParam("source", mConsumerKey);
		mOAuthRequest.addOAuthParam("oauth_timestamp", getTimestampInSeconds());
		mOAuthRequest.addOAuthParam("oauth_nonce", getNonce());
		mOAuthRequest.addOAuthParam("oauth_consumer_key", mConsumerKey);
		mOAuthRequest.addOAuthParam("oauth_signature_method", "HMAC-SHA1");
		mOAuthRequest.addOAuthParam("oauth_version", "1.0");
	    OAuthToken oAuthToken = OAuthConstants.EMPTY_TOKEN;
	    String str5 = getSignature(mOAuthRequest, oAuthToken);
	    mOAuthRequest.addOAuthParam("oauth_signature", str5);
	    String str6 = extractHeader(mOAuthRequest);
	    mOAuthRequest.addHeader("Authorization", str6);
	    return mOAuthRequest.getToken();
	  }
	@Override
	public OAuthToken getAccessToken(OAuthToken oAuthToken, String verifier) {
		mOAuthRequest = new SinaOAuthRequest("POST", "http://api.t.sina.com.cn/oauth/access_token");
		mOAuthRequest.addBodyParam("oauth_verifier", verifier);
		//mOAuthRequest.addBodyParam("source", mConsumerKey);
		mOAuthRequest.addOAuthParam("oauth_timestamp", getTimestampInSeconds());
		mOAuthRequest.addOAuthParam("oauth_nonce", getNonce());
		mOAuthRequest.addOAuthParam("oauth_consumer_key", mConsumerKey);
		mOAuthRequest.addOAuthParam("oauth_signature_method", "HMAC-SHA1");
		mOAuthRequest.addOAuthParam("oauth_version", "1.0");
	    mOAuthRequest.addOAuthParam("oauth_token", oAuthToken.getToken());
	    String str6 = getSignature(mOAuthRequest, oAuthToken);
	    mOAuthRequest.addOAuthParam("oauth_signature", str6);
	    String str7 = extractHeader(mOAuthRequest);
	    mOAuthRequest.addHeader("Authorization", str7);
	    return mOAuthRequest.getToken();
	}
	@Override
	public String pushMsg(HashMap<String,String> params, OAuthToken oAuthToken){
		mOAuthRequest = new SinaOAuthRequest("POST", "http://api.t.sina.com.cn/statuses/update.json");
		mOAuthRequest.addBodyParam("source", mConsumerKey);
		mOAuthRequest.addBodyParam("status", params.get("status"));	
		
		if(params.get("lat")!=null)mOAuthRequest.addBodyParam("lat", params.get("lat"));	
		if(params.get("long")!=null)mOAuthRequest.addBodyParam("long", params.get("long"));	
		if(params.get("annotations")!=null)mOAuthRequest.addBodyParam("annotations", params.get("annotations"));
		
		mOAuthRequest.addOAuthParam("oauth_timestamp", getTimestampInSeconds());
		mOAuthRequest.addOAuthParam("oauth_nonce", getNonce());
		mOAuthRequest.addOAuthParam("oauth_consumer_key", mConsumerKey);
		mOAuthRequest.addOAuthParam("oauth_signature_method", "HMAC-SHA1");
		mOAuthRequest.addOAuthParam("oauth_version", "1.0");
	    mOAuthRequest.addOAuthParam("oauth_token", oAuthToken.getToken());
	    String str6 = getSignature(mOAuthRequest, oAuthToken);
	    mOAuthRequest.addOAuthParam("oauth_signature", str6);
	    String str7 = extractHeader(mOAuthRequest);
	    mOAuthRequest.addHeader("Authorization", str7);
	    
		return mOAuthRequest.pushMsg();
	}
	@Override
	public String pushPicMsg(HashMap<String,String> params, OAuthToken oAuthToken){
		mOAuthRequest = new SinaOAuthRequest("POST", "http://api.t.sina.com.cn/statuses/upload.json");
		mOAuthRequest.addBodyParam("source", mConsumerKey);
		mOAuthRequest.addBodyParam("status", params.get("status"));	
		
		if(params.get("lat")!=null)mOAuthRequest.addBodyParam("lat", params.get("lat"));	
		if(params.get("long")!=null)mOAuthRequest.addBodyParam("long", params.get("long"));	
		
		mOAuthRequest.addOAuthParam("oauth_timestamp", getTimestampInSeconds());
		mOAuthRequest.addOAuthParam("oauth_nonce", getNonce());
		mOAuthRequest.addOAuthParam("oauth_consumer_key", mConsumerKey);
		mOAuthRequest.addOAuthParam("oauth_signature_method", "HMAC-SHA1");
		mOAuthRequest.addOAuthParam("oauth_version", "1.0");
	    mOAuthRequest.addOAuthParam("oauth_token", oAuthToken.getToken());
	    String str6 = getSignature(mOAuthRequest, oAuthToken);
	    mOAuthRequest.addOAuthParam("oauth_signature", str6);
	    String str7 = extractHeader(mOAuthRequest);
	    mOAuthRequest.addHeader("Authorization", str7);
	    
		return mOAuthRequest.pushMsg(params.get("pic"));
	}
	
}
