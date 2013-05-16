package ru.commenthere.comment.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import ru.commenthere.comment.AppContext;

import android.preference.PreferenceActivity.Header;
import android.util.Log;


public class ConnectionClient {
	
	public static final String TAG = "ConnectionClient";
	
	public static final int STATUS_CODE_OK = 200;
	public static final int STATUS_BAD_REQUEST = 400;
	public static final int STATUS_UNAUTHORIZED = 401;

	protected final String CONTENT_TYPE = "application/json";
	protected final String TYPE_HEADER = "Content-Type";
	protected final String ACCEPT_HEADER = "Accept";
	protected final String LINE_DIVIDER = "\n";
	protected final String HTTP_SCHEME = "http";
	
	protected final int HTTP_SCHEME_PORT = 80;
	
	private HttpClient client;
	
	public ConnectionClient() {
		this.client = getHttpClient();
	}

	public String sendGetRequest(String url) throws ConnectionClientException {
		HttpResponse response = null;
		InputStream is = null;
				
		Log.d(TAG, url);
		
		HttpGet request = new HttpGet(url);
		if (AppContext.USE_GZIP){
			request.addHeader("Accept-Encoding", "gzip");
		};
		
		try {
			response = client.execute(request);
			if(response != null) {

				StatusLine status = response.getStatusLine();
				if (status.getStatusCode() != STATUS_CODE_OK) {
					throw new ConnectionClientException("Invalid response from server: " + status.toString());
				}
				is = response.getEntity().getContent();
				if (AppContext.USE_GZIP) {
				    is = new GZIPInputStream(is);
				}
			}
		} catch (ClientProtocolException e) {
			throw new ConnectionClientException("ClientProtocolException", e);
		} catch (IOException e) {
			throw new ConnectionClientException("IOException", e);
		}
		
		return convertStreamToString(is);
	}	
	
	public String sendPostRequest(String url, String postData) throws ConnectionClientException {
		HttpResponse response = null;
		
		HttpPost request = new HttpPost(url);

		if (AppContext.USE_GZIP){
			request.addHeader("Accept-Encoding", "gzip");
		};

		//String req = "data=" + encodeToBase64(postData);
		String req = null;
		try {
			req = URLEncoder.encode(postData, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new ConnectionClientException("UnsupportedEncodingException", e);
		}
		postData = null;

		HttpEntity post_entity;
		StringEntity s;
		try {
			s = new StringEntity(req);
			s.setContentType("application/x-www-form-urlencoded; charset=utf-8");
			post_entity = s;
			request.setEntity(post_entity);
		} catch (UnsupportedEncodingException e) {
			throw new ConnectionClientException("UnsupportedEncodingException", e);
		}

		try {
			response = client.execute(request);
			if(response != null) {
				StatusLine status = response.getStatusLine();
	
				if (status.getStatusCode() != STATUS_CODE_OK) {
					throw new ConnectionClientException("Invalid response from server: " + status.toString());
				}
			}
		} catch (ClientProtocolException e) {
			throw new ConnectionClientException("ClientProtocolException", e);
		} catch (IOException e) {
			throw new ConnectionClientException("IOException", e);
		}		
		
		try {
			return  EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ConnectionClientException("ParseException", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionClientException("IOException", e);
		}
	}
	
	public String sendPostRequest(String url, HttpEntity entity) throws ConnectionClientException {
		HttpResponse response = null;
		
		HttpPost request = new HttpPost(url);

		if (AppContext.USE_GZIP){
			request.addHeader("Accept-Encoding", "gzip");
		};


		request.setEntity(entity);

		try {
			response = client.execute(request);
			if(response != null) {
				StatusLine status = response.getStatusLine();
	
				if (status.getStatusCode() != STATUS_CODE_OK) {
					throw new ConnectionClientException("Invalid response from server: " + status.toString());
				}
			}
		} catch (ClientProtocolException e) {
			throw new ConnectionClientException("ClientProtocolException", e);
		} catch (IOException e) {
			throw new ConnectionClientException("IOException", e);
		}		
		
		try {
			return  EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ConnectionClientException("ParseException", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectionClientException("IOException", e);
		}
	}

	
	private HttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 15000;
		HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
		
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(params, timeoutSocket);
		
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme(HTTP_SCHEME, 
				PlainSocketFactory.getSocketFactory(), HTTP_SCHEME_PORT));
		
		ClientConnectionManager conectionManager = new 
			ThreadSafeClientConnManager(params, registry);
		
		return new DefaultHttpClient(conectionManager, params);
	}
	
	private String convertStreamToString(InputStream is) throws ConnectionClientException {
		if(is != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException e) {
				throw new ConnectionClientException("IOException", e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					//do nothing, because it's not important for us
				}
			}
			return sb.toString();
		} else {
			return null;
		}		
	}
	
}
