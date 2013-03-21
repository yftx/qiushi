package douzi.android.caodan;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpHelper {
	
	private DefaultHttpClient mClient = new DefaultHttpClient();
	
	public  String post(String uri,List<BasicNameValuePair> values,String encoding){
		HttpPost post = new HttpPost(uri);
		HttpResponse rsp;
		try {
			HttpEntity entity = new UrlEncodedFormEntity(values, encoding);
			post.setEntity(entity);
			rsp = mClient.execute(post);
			if(rsp.getStatusLine().getStatusCode() == 200){
				return EntityUtils.toString(rsp.getEntity());
			}
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e){
			return null;
		}
	}
	
	public  InputStream getForStream(String uri){
		HttpGet get = new HttpGet(uri);
		HttpResponse rsp;
		try {
			rsp = mClient.execute(get);
			if(rsp.getStatusLine().getStatusCode() == 200){
				return rsp.getEntity().getContent();
			}
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e){
			return null;
		}
	}
	
	public String getForString(String uri,String charset){
		HttpGet get = new HttpGet(uri);
		HttpResponse rsp;
		try {
			rsp = mClient.execute(get);
			if(rsp.getStatusLine().getStatusCode() == 200){
				return EntityUtils.toString(rsp.getEntity(),charset);
			}
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (Exception e){
			return null;
		}
	}
	
}
