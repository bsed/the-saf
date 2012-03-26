package cn.salesuite.saf.weibo;

import java.util.HashMap;


public class SinaOAuthRequest extends OAuthRequest{

	public SinaOAuthRequest(String paramString1, String paramString2) {
		super(paramString1, paramString2);
	}

	@Override
	public OAuthToken getToken() {
		OAuthToken oAuthToken = null;
		/**header**/
		//返回值 callback会失效 
//		try {
//			String result = http.exePost(mUrl,"Authorization" ,this.getHeaders().get("Authorization"));
//			Utils.Log(">>", "result:"+result);
//			oAuthToken = this.extract(result);
//			Utils.Log(">>", oAuthToken.getToken()+":=:"+oAuthToken.getSecret());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		/**queryString**/
		HashMap<String, String> map = this.getOAuthParams();
		map.putAll(this.getBodyParams());
		String str = OAuthUtils.encodeMap(map);
		try {
			String result = http.exePost(mUrl, str);
			oAuthToken = this.extract(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oAuthToken;
	}

	@Override
	public String pushMsg(){
		String result =null;
		/**header**/
		
		/**queryString0**/
		HashMap<String, String> map = this.getOAuthParams();
		map.putAll(this.getBodyParams());
		try {
			result = http.exePost(mUrl,map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**queryString1**/
//		HashMap<String, String> map = this.getOAuthParams();
//		map.putAll(this.getBodyParams());
//		String str = OAuthUtils.encodeMap(map);
//		try {
//			result = http.exePost(mUrl,str);
//			Utils.Log(">>", "result:"+result);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return result;
	}
	@Override
	public String pushMsg(String pic){
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
