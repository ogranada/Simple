package com.framework.simple.datalayer.sources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.framework.simple.interfaces.Callback;
import com.framework.simple.interfaces.DataSource;

public class RESTDataSource implements DataSource{

	CookieStore cookieStore;
	HttpContext localContext;
	DefaultHttpClient dhttpclient;

	String username;
	String password;
	String host;
	int port;
	String uri;
	
	public RESTDataSource(String host, int port) {
		this.host = host.contains("http://")?host.split("http://")[1]:host;
		this.port = port;
		uri = String.format("http://%s:%d/", this.host, this.port);
		cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		dhttpclient = new DefaultHttpClient();
	}
	
	public void setAuthData(String username, String password) {
		this.username = username;
		this.password = password;
		dhttpclient.getCredentialsProvider().setCredentials(new AuthScope(host, AuthScope.ANY_PORT), new UsernamePasswordCredentials(username, password));
        HttpGet dhttpget = new HttpGet(uri);
        dhttpget.setHeader("Content-Type", "application/json;charset=UTF-8");
        System.out.println("executing request " + dhttpget.getRequestLine());
        try {
            HttpResponse dresponse = dhttpclient.execute(dhttpget, localContext);
            StatusLine resp = dresponse.getStatusLine();
            System.out.println( resp );
		} catch (Exception e) {
			System.out.println( "Error: "+e );
		}
	}

	@Override
	public void getData(String apiSection, Callback callback) {
        try
        {     
        	String spec = apiSection;
        	spec = spec.startsWith("/")?spec.substring(1):spec;
        	spec = spec.endsWith("/")?spec:spec+"/";
        	HttpGet dhttpget = new HttpGet(uri+spec);
    		dhttpget.setHeader("Content-Type", "application/json;charset=UTF-8");
            try {
                HttpResponse dresponse = dhttpclient.execute(dhttpget, localContext);
                if(callback!=null){
                	List<Map<String,Object>> l = new ArrayList<Map<String,Object>>(0);
                	String respStr = EntityUtils.toString(dresponse.getEntity());
                	if(respStr.startsWith("[")){
                		JSONArray arr = new JSONArray(respStr);
                		for (int i = 0; i < arr.length(); i++) {
							l.add(toMap(arr.getJSONObject(i)));
						}
                	} else if(respStr.startsWith("{")){
                		JSONObject json = new JSONObject(respStr);
                		Map<String, Object> mapa = toMap(json);
                		l.add(mapa);
                	}
                	callback.onFinish(l);
                }
    		} catch (Exception e) {
    			System.out.println( "Error: "+e );
    		}
        	/*
            HttpPost post = new HttpPost("http://192.168.100.7:8000/autores/");
			post.setHeader("content-type", "application/json; charset=utf-8");
			JSONObject json = new JSONObject();
			json.put("nombre", "pedro");
			json.put("apellido", "paramo");
			StringEntity entity = new StringEntity(json.toString());
			post.setEntity(entity);
			HttpResponse resp2 = dhttpclient.execute(post, localContext);
            System.out.println( resp2.getStatusLine() );
            String respStr = EntityUtils.toString(resp2
					.getEntity());
			*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}

	public Map<String, Object> toMap(JSONObject json) {
		Map<String, Object> mapa = new HashMap<String, Object>();
		Iterator<String> iter = json.keys();
	    while (iter.hasNext()) {
	        String key = iter.next();
	        try {
	            mapa.put(key, json.get(key));
	        } catch (JSONException e) {
	        }
	    }
	    return mapa;
	}
	
}
