package cn.salesuite.saf.weibo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public abstract class OAuthService {
	protected String mConsumerKey;
	protected String mConsumerSecret;
	protected OAuthRequest mOAuthRequest;
	
	
	public abstract OAuthToken getRequestToken();

	public abstract OAuthToken getAccessToken(OAuthToken oAuthToken, String verifier);
	
	public abstract String pushMsg(HashMap<String,String> params,OAuthToken oAuthToken);
	
	public abstract String pushPicMsg(HashMap<String,String> params,OAuthToken oAuthToken);

	public OAuthService(String consumerKey, String consumerSecret) {
		this.mConsumerKey = consumerKey;
		this.mConsumerSecret = consumerSecret;
	}

	public String extractHeader(OAuthRequest paramOAuthRequest) {
		HashMap<String, String> map = paramOAuthRequest.getOAuthParams();
		map.putAll(paramOAuthRequest.getOAuthParams());
		map.putAll(paramOAuthRequest.getBodyParams());
		StringBuffer sb = new StringBuffer();
		sb.append("OAuth ");
		Iterator<String> iterator = map.keySet().iterator();
		while (true) {
			if (!iterator.hasNext())
				return sb.substring(0, sb.length() - 1).toString();
			String str1 = iterator.next();	
			Object[] array = new String[2];
			array[0] = str1;
			String str2 = encode(map.get(str1));
			array[1] = str2;
			String str3 = String.format("%s=\"%s\"", array);
			sb.append(str3).append(",");
		}
	}

	public String doSign(String data, String key) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes("UTF-8"));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException ignore) {
			ignore.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Base64Encoder.encode(byteHMAC);
	}

	public String encode(String value) {
		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
			ignore.printStackTrace();
		}
		StringBuffer buf = new StringBuffer(encoded.length());
		char focus;
		for (int i = 0; i < encoded.length(); i++) {
			focus = encoded.charAt(i);
			if (focus == '*') {
				buf.append("%2A");
			} else if (focus == '+') {
				buf.append("%20");
			} else if (focus == '%' && (i + 1) < encoded.length()
					&& encoded.charAt(i + 1) == '7'
					&& encoded.charAt(i + 2) == 'E') {
				buf.append('~');
				i += 2;
			} else {
				buf.append(focus);
			}
		}
		return buf.toString();
	}

	protected String getSignature(OAuthRequest request, OAuthToken oauthToken) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.putAll(mOAuthRequest.getOAuthParams());
		map.putAll(mOAuthRequest.getBodyParams());
		String str1 = mOAuthRequest.getMethod();
		String str2 = encode(mOAuthRequest.getUrl());
		String str3 = getParamsString(map);
		String str4 = str1 + "&" + str2 + "&" + str3;
		String str5 = null;
		if (null!=oauthToken.getSecret()&&!"".equals(oauthToken.getSecret())) {
			str5 = mConsumerSecret + "&"+oauthToken.getSecret();
		} else {
			str5 = mConsumerSecret + "&";
		}
		return doSign(str4, str5);
	}

	public String formBaseString(String method, String url, String params) {

		return null;
	}

	private String getParamsString(HashMap<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		Set<String> set = parameters.keySet();
		ArrayList<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		Iterator<String> iterator = list.iterator();
		while (true) {
			if (!iterator.hasNext())
				return encode(sb.substring(0, sb.length() - 1).toString());
			String str1 = (String) iterator.next();
			String str2 = parameters.get(str1);
			str2=encode(str2);
			sb.append(str1).append("=").append(str2).append("&");
		}
	}

	protected String getNonce() {
		long l = getTs();
		return String.valueOf(new Random().nextInt() + l);
	}

	protected String getTimestampInSeconds() {
		return String.valueOf(getTs());
	}

	protected long getTs() {
		return System.currentTimeMillis() / 1000L;
	}
}
