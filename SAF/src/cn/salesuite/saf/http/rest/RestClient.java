/**
 * 
 */
package cn.salesuite.saf.http.rest;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.Proxy.Type.HTTP;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 使用HttpURLConnection实现http的get、post、put、delete方法</br>
 * 同步调用get方法：
 * <pre>
 * <code>
 * RestClient request = RestClient.get(url);
 * String body = request.body();
 * </code>
 * </pre>
 * 异步调用get方法：
 * <pre>
 * <code>
 * 		RestClient.get(url,new HttpResponseHandler(){
 * 
 *			public void onSuccess(String content) {
 *			}
 *			
 *		});
 * </code>
 * </pre>
 * 同步调用post方法：post body内容为json
 * <pre>
 * <code>
 * RestClient request = RestClient.post(url);
 * request.acceptJson().contentType("application/json", null);
 * request.send(jsonString);
 * String body = request.body();
 * </code>
 * </pre>
 * 异步调用post方法：post body内容为json
 * <pre>
 * <code>
 * 		RestClient.post(url,json,new HttpResponseHandler(){
 *
 *			public void onSuccess(String content) {
 *			}
 *			
 *		});
 * </code>
 * </pre>
 * 使用gzip压缩的方法：
 * <pre>
 * <code>
 * RestClient request = RestClient.get(url);
 * request.acceptGzipEncoding().uncompress(true);
 * String body = request.body();
 * </code>
 * </pre>
 * @author Tony Shen
 * 
 */
public class RestClient {

	private HttpURLConnection connection = null;
	private RestOutputStream output;

	private boolean multipart;
	private boolean ignoreCloseExceptions = true;
	private boolean uncompress = false;
	
	private URL url;
	private String requestMethod;
	private String httpProxyHost; // 代理服务器的url
	private int httpProxyPort;    // 代理服务器的端口

	private int bufferSize = 8192;
	
	/**
	 * 默认的ConnectionFactory
	 */
    static ConnectionFactory DEFAULT = new ConnectionFactory() {
      public HttpURLConnection create(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
      }

      public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
        return (HttpURLConnection) url.openConnection(proxy);
      }
    };
    
    private static ConnectionFactory CONNECTION_FACTORY = DEFAULT;

    /**
     * 可以自定义ConnectionFactory，只需实现ConnectionFactory接口即可
     * @param connectionFactory
     */
    public static void setConnectionFactory(final ConnectionFactory connectionFactory) {
      if (connectionFactory == null)
        CONNECTION_FACTORY = DEFAULT;
      else
        CONNECTION_FACTORY = connectionFactory;
    }
    
	private HttpURLConnection createConnection() {
		try {
			final HttpURLConnection connection;
			if (httpProxyHost != null)
				connection = CONNECTION_FACTORY.create(url, createProxy());
			else
				connection = CONNECTION_FACTORY.create(url);
			connection.setRequestMethod(requestMethod);
			return connection;
		} catch (IOException e) {
			throw new RestException(e);
		}
	}

	public HttpURLConnection getConnection() {
		if (connection == null)
			connection = createConnection();
		return connection;
	}
    
	private Proxy createProxy() {
		return new Proxy(HTTP, new InetSocketAddress(httpProxyHost,httpProxyPort));
	}
    
	/**
	 * 配置http代理
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 */
    public RestClient useProxy(final String proxyHost, final int proxyPort) {
      if (connection != null)
        throw new IllegalStateException("The connection has already been created. This method must be called before reading or writing to the request.");

      this.httpProxyHost = proxyHost;
      this.httpProxyPort = proxyPort;
      return this;
    }

	/**
	 * 创建 HTTP connection wrapper
	 * 
	 * @param url
	 * @param method
	 * @throws RestException
	 */
	public RestClient(String url, String method)
			throws RestException {
		try {
			this.url = new URL(url);
			this.requestMethod = method;
		} catch (MalformedURLException e) {
			throw new RestException(e);
		}
	}

	/**
	 * 同步发起get请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient get(String url) throws RestException {
		System.out.println("get url="+url);
		return new RestClient(url, RestConstant.METHOD_GET);
	}
	
	/**
	 * 异步发起get请求
	 * @param url
	 * @param callback
	 * @throws RestException
	 */
	public static void get(String url,HttpResponseHandler callback) throws RestException {
		System.out.println("get url="+url);
		RestClient client = new RestClient(url, RestConstant.METHOD_GET);
		String body = client.body();
		callback.onSuccess(body);
	}

	/**
	 * 同步发起post请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient post(String url) throws RestException {
		System.out.println("post url="+url);
		return new RestClient(url, RestConstant.METHOD_POST);
	}

	/**
	 * 异步发起post请求
	 * @param url
	 * @param json 其中post body是json字符串
	 * @param callback
	 * @throws RestException
	 */
	public static void post(String url,JSONObject json,HttpResponseHandler callback) throws RestException {
		System.out.println("post url="+url+"\n"+"post body="+JSON.toJSONString(json));
		
		RestClient request = new RestClient(url, RestConstant.METHOD_POST);
		request.acceptJson().contentType("application/json");
		try {
			request.send(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String body = request.body();
		callback.onSuccess(body);
	}

	/**
	 * 发起put请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient put(String url) throws RestException {
		return new RestClient(url, RestConstant.METHOD_PUT);
	}

	/**
	 * 发起delete请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient delete(String url) throws RestException {
		return new RestClient(url, RestConstant.METHOD_DELETE);
	}

	/**
	 * 将response返回的stream封装成BufferedInputStream
	 * 
	 * @see #bufferSize(int)
	 * @return stream
	 * @throws RestException
	 */
	public BufferedInputStream buffer() throws RestException {
		return new BufferedInputStream(stream(), bufferSize);
	}

	/**
	 * 返回response body的是否自动压缩
	 * @param uncompress
	 * @return
	 */
	public RestClient uncompress(final boolean uncompress) {
		this.uncompress = uncompress;
		return this;
	}

	/**
	 * 返回response body的stream，如果需要压缩返回的将是GZIPInputStream
	 * 
	 * @return stream
	 * @throws RestException
	 */
	public InputStream stream() throws RestException {
		InputStream stream;
		if (code() < HTTP_BAD_REQUEST)
			try {
				stream = getConnection().getInputStream();
			} catch (IOException e) {
				throw new RestException(e);
			}
		else {
			stream = getConnection().getErrorStream();
			if (stream == null)
				try {
					stream = getConnection().getInputStream();
				} catch (IOException e) {
					throw new RestException(e);
				}
		}

		if (!uncompress
				|| !RestConstant.ENCODING_GZIP.equals(contentEncoding()))
			return stream;
		else
			try {
				return new GZIPInputStream(stream);
			} catch (IOException e) {
				throw new RestException(e);
			}
	}

	/**
	 * 从response中返回header的Content-Encoding
	 * 
	 * @return
	 */
	public String contentEncoding() {
		return header(RestConstant.HEADER_CONTENT_ENCODING);
	}

	/**
	 * 返回HTTP Status Code
	 * 
	 * @return the response code
	 * @throws RestException
	 */
	public int code() throws RestException {
		try {
			closeOutput();
			return getConnection().getResponseCode();
		} catch (IOException e) {
			throw new RestException(e);
		}
	}

	public int intHeader(final String name) throws RestException {
		return intHeader(name, -1);
	}

	public int intHeader(final String name, final int defaultValue)
			throws RestException {
		closeOutputQuietly();
		return getConnection().getHeaderFieldInt(name, defaultValue);
	}

	/**
	 * 从response中返回header的Content-Length
	 * 
	 * @return response header value
	 */
	public int contentLength() {
		return intHeader(RestConstant.HEADER_CONTENT_LENGTH);
	}

	/**
	 * 创建ByteArrayOutputStream
	 * 
	 * @return stream
	 */
	protected ByteArrayOutputStream byteStream() {
		final int size = contentLength();
		if (size > 0)
			return new ByteArrayOutputStream(size);
		else
			return new ByteArrayOutputStream();
	}

	/**
	 * 返回http response
	 * 
	 * @return string
	 * @throws RestException
	 */
	public String body() throws RestException {
		return body(charset());
	}

	/**
	 * 返回指定charset的http response
	 * 
	 * @param charset
	 * @return string
	 * @throws RestException
	 */
	public String body(final String charset) throws RestException {
		final ByteArrayOutputStream output = byteStream();
		try {
			copy(buffer(), output);
			return output.toString(RestUtil.getValidCharset(charset));
		} catch (IOException e) {
			throw new RestException(e);
		}
	}
	
	/**
	 * 设置{@link HttpURLConnection#setUseCaches(boolean)}的值
	 * 
	 * @param useCaches
	 * @return RestClient
	 */
	public RestClient useCaches(final boolean useCaches) {
		getConnection().setUseCaches(useCaches);
		return this;
	}

	/**
	 * 设置header中accept的值
	 * 
	 * @param value
	 * @return RestClient
	 */
	public RestClient accept(final String value) {
		return header(RestConstant.HEADER_ACCEPT, value);
	}

	/**
	 * 设置header中accept的值为application/json
	 * 
	 * @return RestClient
	 */
	public RestClient acceptJson() {
		return accept(RestConstant.CONTENT_TYPE_JSON);
	}
	
	/**
	 * 设置header中Accept-Encoding的值为gzip
	 * 
	 * @return RestClient
	 */
	public RestClient acceptGzipEncoding() {
		return acceptEncoding(RestConstant.ENCODING_GZIP);
	}
	
	/**
	 * 设置header中Accept-Encoding的值
	 * 
	 * @param value
	 * @return RestClient
	 */
	public RestClient acceptEncoding(final String value) {
		return header(RestConstant.HEADER_ACCEPT_ENCODING, value);
	}

	/**
	 * 拷贝inputstream到outputstream
	 * 
	 * @param input
	 * @param output
	 * @return RestClient
	 * @throws IOException
	 */
	protected RestClient copy(final InputStream input, final OutputStream output)
			throws IOException {
		return new CloseOperation<RestClient>(input, ignoreCloseExceptions) {

			@Override
			public RestClient run() throws IOException {
				final byte[] buffer = new byte[bufferSize];
				int read;
				while ((read = input.read(buffer)) != -1)
					output.write(buffer, 0, read);
				return RestClient.this;
			}
		}.call();
	}

	/**
	 * 拷贝reader到writer
	 * 
	 * @param input
	 * @param output
	 * @return RestClient
	 * @throws IOException
	 */
	protected RestClient copy(final Reader input, final Writer output)
			throws IOException {
		return new CloseOperation<RestClient>(input, ignoreCloseExceptions) {

			@Override
			public RestClient run() throws IOException {
				final char[] buffer = new char[bufferSize];
				int read;
				while ((read = input.read(buffer)) != -1)
					output.write(buffer, 0, read);
				return RestClient.this;
			}
		}.call();
	}

	/**
	 * 从response header中返回Content-Type的charset参数
	 * 
	 * @return charset or null if none
	 */
	public String charset() {
		return parameter(RestConstant.HEADER_CONTENT_TYPE,
				RestConstant.PARAM_CHARSET);
	}

	/**
	 * 从response header中返回指定的参数值
	 * 
	 * @param headerName
	 * @param paramName
	 * @return parameter value or null if missing
	 */
	public String parameter(final String headerName, final String paramName) {
		return getParam(header(headerName), paramName);
	}

	/**
	 * 从header中获取参数值
	 * 
	 * @param value
	 * @param paramName
	 * @return parameter value or null if none
	 */
	protected String getParam(final String value, final String paramName) {
		if (value == null || value.length() == 0)
			return null;

		final int length = value.length();
		int start = value.indexOf(';') + 1;
		if (start == 0 || start == length)
			return null;

		int end = value.indexOf(';', start);
		if (end == -1)
			end = length;

		while (start < end) {
			int nameEnd = value.indexOf('=', start);
			if (nameEnd != -1 && nameEnd < end
					&& paramName.equals(value.substring(start, nameEnd).trim())) {
				String paramValue = value.substring(nameEnd + 1, end).trim();
				int valueLength = paramValue.length();
				if (valueLength != 0)
					if (valueLength > 2 && '"' == paramValue.charAt(0)
							&& '"' == paramValue.charAt(valueLength - 1))
						return paramValue.substring(1, valueLength - 1);
					else
						return paramValue;
			}

			start = end + 1;
			end = value.indexOf(';', start);
			if (end == -1)
				end = length;
		}

		return null;
	}

	/**
	 * 获取response header中的值
	 * 
	 * @param name
	 * @return response header
	 * @throws RestException
	 */
	public String header(final String name) throws RestException {
		closeOutputQuietly();
		return getConnection().getHeaderField(name);
	}

	/**
	 * 设置request header中的值
	 * 
	 * @param name
	 * @param value
	 * @return RestClient
	 */
	public RestClient header(final String name, final String value) {
		getConnection().setRequestProperty(name, value);
		return this;
	}

	protected RestClient closeOutputQuietly() throws RestException {
		try {
			return closeOutput();
		} catch (IOException e) {
			throw new RestException(e);
		}
	}

	/**
	 * 关闭output
	 * 
	 * @return RestClient
	 * @throws RestException
	 * @throws IOException
	 */
	protected RestClient closeOutput() throws IOException {
		if (output == null)
			return this;
		if (multipart)
			output.write(RestConstant.CRLF + "--" + RestConstant.BOUNDARY
					+ "--" + RestConstant.CRLF);
		if (ignoreCloseExceptions)
			try {
				output.close();
			} catch (IOException ignored) {
				// Ignored
			}
		else
			output.close();
		output = null;
		return this;
	}
	
	/**
	 * 将json的内容写入post body
	 * 
	 * @param json
	 * @return RestClient
	 * @throws RestException
	 * @throws IOException 
	 */
	public RestClient send(JSONObject json) throws RestException, IOException {
		return send(JSON.toJSONString(json));
	}
	
	/**
	 * 将jsonString的内容写入post body
	 * 
	 * @param json
	 * @return RestClient
	 * @throws RestException
	 * @throws IOException 
	 */
	public RestClient send(String jsonString) throws RestException, IOException {
		return send(jsonString.getBytes("UTF-8"));
	}
	
	/**
	 * 将字节数组写入post body
	 * 
	 * @param input
	 * @return RestClient
	 * @throws RestException
	 */
	public RestClient send(final byte[] input) throws RestException {
		return send(new ByteArrayInputStream(input));
	}

	/**
	 * 将InputStream写入post body，并且InputStream流会在发送完毕后关闭
	 * 
	 * @param input
	 * @return RestClient
	 * @throws RestException
	 */
	public RestClient send(final InputStream input) throws RestException {
		try {
			openOutput();
			copy(input, output);
		} catch (IOException e) {
			throw new RestException(e);
		}
		return this;
	}
	
	/**
	 * 设置header的Content-Type
	 * 
	 * @param value
	 * @return RestClient
	 */
	public RestClient contentType(final String value) {
		return contentType(value,null);
	}

	/**
	 * 设置header的Content-Type
	 * 
	 * @param value
	 * @param charset
	 * @return RestClient
	 */
	public RestClient contentType(final String value, final String charset) {
		if (charset != null && charset.length() > 0) {
			final String separator = "; " + RestConstant.PARAM_CHARSET + '=';
			return header(RestConstant.HEADER_CONTENT_TYPE, value + separator
					+ charset);
		} else
			return header(RestConstant.HEADER_CONTENT_TYPE, value);
	}

	/**
	 * 返回response的header中Content-Type的内容
	 * 
	 * @return response header value
	 */
	public String contentType() {
		return header(RestConstant.HEADER_CONTENT_TYPE);
	}

	/**
	 * 打开output，在使用post时会用到
	 * 
	 * @return RestClient
	 * @throws IOException
	 */
	protected RestClient openOutput() throws IOException {
		if (output != null)
			return this;
		getConnection().setDoOutput(true);
		final String charset = getParam(
				getConnection().getRequestProperty(RestConstant.HEADER_CONTENT_TYPE),
				RestConstant.PARAM_CHARSET);
		output = new RestOutputStream(getConnection().getOutputStream(), charset,
				bufferSize);
		return this;
	}
}
