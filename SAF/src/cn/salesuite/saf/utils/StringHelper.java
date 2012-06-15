/**
 * 
 */
package cn.salesuite.saf.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串的帮助类
 * @author Tony Shen
 *
 */
public class StringHelper {

	private final static int BUFFER_SIZE = 4096;
    private static final char CHAR_CHINESE_SPACE = '\u3000';//中文（全角）空格
	
	/**
	 * 判断字符串是否为手机号码</br>
	 * 只能判断是否为大陆的手机号码
	 * @param str
	 * @return
	 */
	public static boolean checkMobile(String str) {
		Pattern p = Pattern.compile("1[358][0-9]{9}");
		Matcher m = p.matcher(str);
		return m.matches();
	}
	
	/**
	 * 验证email的合法性
	 * 
	 * @param emailStr
	 * @return
	 */
	public static boolean checkEmail(String emailStr) {
		String check = "^([a-z0-9A-Z]+[-|._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(emailStr.trim());
		boolean isMatched = matcher.matches();
		if (isMatched) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 对字符串进行MD5加密</br>
	 * 如果返回为空，则表示加密失败
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// 将每个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 将InputStream转换成String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String inputStream2String(InputStream in) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[BUFFER_SIZE];
		int count = -1;
		while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
			outStream.write(data, 0, count);
		data = null;
		return new String(outStream.toByteArray(), "utf-8");
	}

	/**
	 * 去掉字符串的空格
	 * @param input
	 * @return
	 */
	public static String trimContent(String input){
		if(input==null) return "";
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<input.length();i++){
			if(input.charAt(i)==' '){
				continue;
			}else{
				sb.append(input.charAt(i));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 从字符串s中截取某一段字符串
	 * @param s
	 * @param startToken 开始标记
	 * @param endToken 结束标记
	 * @return
	 */
    public static String mid(String s, String startToken, String endToken) {
        return mid(s, startToken, endToken, 0);
    }

    public static String mid(String s, String startToken, String endToken, int fromStart) {
        if (startToken==null || endToken==null)
            return null;
        int start = s.indexOf(startToken, fromStart);
        if (start==(-1))
            return null;
        int end = s.indexOf(endToken, start + startToken.length());
        if (end==(-1))
            return null;
        String sub = s.substring(start + startToken.length(), end);
        return sub.trim();
    }
    
    /**
     * 简化字符串，通过删除空格键、tab键、换行键等实现
     * @param s
     * @return
     */
    public static String compact(String s) {
        char[] cs = new char[s.length()];
        int len = 0;
        for(int n=0; n<cs.length; n++) {
            char c = s.charAt(n);
            if(c==' ' || c=='\t' || c=='\r' || c=='\n' || c==CHAR_CHINESE_SPACE)
                continue;
            cs[len] = c;
            len++;
        }
        return new String(cs, 0, len);
    }
    
    /**
     * 生成uuid
     * @return
     */
	public static String getUUID() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
}
