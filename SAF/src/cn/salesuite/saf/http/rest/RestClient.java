/**
 * 
 */
package cn.salesuite.saf.http.rest;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * 使用HttpURLConnection实现http的get、post、put、delete方法</br>
 * get方法：
 * <pre>
 * <code>
 * RestClient request = RestClient.get(url);
 * String body = request.body();
 * </code>
 * </pre>
 * post方法：post body内容为json
 * <pre>
 * <code>
 * RestClient request = RestClient.post(url);
 * request.acceptJson().contentType("application/json", null);
 * request.send(jsonString);
 * String body = request.body();
 * </code>
 * </pre>
 * 
 * @author Tony
 * 
 */
public class RestClient {

	private final HttpURLConnection connection;
	private RestOutputStream output;

	private boolean multipart;
	private boolean ignoreCloseExceptions = true;
	private boolean uncompress = false;
	private boolean form;

	private int bufferSize = 8192;

	/**
	 * 创建 HTTP connection wrapper
	 * 
	 * @param url
	 * @param method
	 * @throws RestException
	 */
	public RestClient(final String url, final String method)
			throws RestException {
		try {
			connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod(method);
		} catch (IOException e) {
			throw new RestException(e);
		}
	}

	/**
	 * 发起get请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient get(final String url) throws RestException {
		return new RestClient(url, RestConstant.METHOD_GET);
	}

	/**
	 * 发起post请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient post(final String url) throws RestException {
		return new RestClient(url, RestConstant.METHOD_POST);
	}

	/**
	 * 发起put请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient put(final String url) throws RestException {
		return new RestClient(url, RestConstant.METHOD_PUT);
	}

	/**
	 * 发起delete请求
	 * 
	 * @param url
	 * @return RestClient
	 * @throws RestException
	 */
	public static RestClient delete(final String url) throws RestException {
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
	 * 返回response body的stream，如果需要压缩返回的将是GZIPInputStream
	 * 
	 * @return stream
	 * @throws RestException
	 */
	public InputStream stream() throws RestException {
		InputStream stream;
		if (code() < HTTP_BAD_REQUEST)
			try {
				stream = connection.getInputStream();
			} catch (IOException e) {
				throw new RestException(e);
			}
		else {
			stream = connection.getErrorStream();
			if (stream == null)
				try {
					stream = connection.getInputStream();
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
			return connection.getResponseCode();
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
		return connection.getHeaderFieldInt(name, defaultValue);
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
	 * Get a response header
	 * 
	 * @param name
	 * @return response header
	 * @throws RestException
	 */
	public String header(final String name) throws RestException {
		closeOutputQuietly();
		return connection.getHeaderField(name);
	}

	/**
	 * Set header name to given value
	 * 
	 * @param name
	 * @param value
	 * @return RestClient
	 */
	public RestClient header(final String name, final String value) {
		connection.setRequestProperty(name, value);
		return this;
	}

	/**
	 * Call {@link #closeOutput()} and re-throw a caught {@link IOException}s as
	 * an {@link RestException}
	 * 
	 * @return RestClient
	 * @throws RestException
	 */
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
	public RestClient send(String json) throws RestException, IOException {
		return send(json.getBytes("UTF-8"));
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
		connection.setDoOutput(true);
		final String charset = getParam(
				connection.getRequestProperty(RestConstant.HEADER_CONTENT_TYPE),
				RestConstant.PARAM_CHARSET);
		output = new RestOutputStream(connection.getOutputStream(), charset,
				bufferSize);
		return this;
	}
}
