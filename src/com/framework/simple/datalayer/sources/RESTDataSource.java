package com.framework.simple.datalayer.sources;

import android.os.AsyncTask;
import android.util.Log;

import com.framework.simple.interfaces.Callback;
import com.framework.simple.interfaces.DataSource;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class RESTDataSource implements DataSource {

	CookieStore cookieStore;
	HttpContext localContext;
	DefaultHttpClient dhttpclient;

	String username;
	String password;
	String host;
	int port;
	String uri;

    int status;
    String statusMessage;

	public RESTDataSource(String host, int port) {
		this.host = host.contains("http://") ? host.split("http://")[1] : host;
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
		dhttpclient.getCredentialsProvider().setCredentials(
				new AuthScope(host, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(username, password));
		HttpGet dhttpget = new HttpGet(uri);
		dhttpget.setHeader("Content-Type", "application/json;charset=UTF-8");
		System.out.println("executing request " + dhttpget.getRequestLine());
		try {
			HttpResponse dresponse = dhttpclient
					.execute(dhttpget, localContext);
			StatusLine resp = dresponse.getStatusLine();
			System.out.println(resp);
		} catch (Exception e) {
			Log.e("REST Auth Error", e.toString());
		}
	}

	private String parseParamsForGet(Map<String, String> params) {
		if (params == null || (params != null && params.size() == 0)) {
			return "";
		}
		List<NameValuePair> _params = new LinkedList<NameValuePair>();
		for (String key : params.keySet()) {
			_params.add(new BasicNameValuePair(key, params.get(key)));
		}
		return "?" + URLEncodedUtils.format(_params, "UTF-8");
	}

	private StringEntity parsePostParams(Map<String, String> params)
			throws Exception {
		if (params == null || (params != null && params.size() == 0)) {
			return new StringEntity("");
		}
		JSONObject dato = new JSONObject();
		for (String key : params.keySet()) {
			dato.put(key, params.get(key));
		}
		return new StringEntity(dato.toString());
	}

	@Override
	public void getData(String apiSection, Map<String, String> params,
			Callback callback) {
		String spec = apiSection;
		if (!spec.equals("")) {
			spec = spec.startsWith("/") ? spec.substring(1) : spec;
			spec = spec.endsWith("/") ? spec : spec + "/";
		}
		final HttpGet dhttpget = new HttpGet(uri + spec
				+ parseParamsForGet(params));
		dhttpget.setHeader("Content-Type", "application/json;charset=UTF-8");
		final Callback _callback = callback;
		AsyncTask<Void, Integer, List<Map<String, Object>>> atAsyncTask = new AsyncTask<Void, Integer, List<Map<String, Object>>>() {

            Exception error = null;

			@Override
			protected List<Map<String, Object>> doInBackground(Void... params) {
				List<Map<String, Object>> l = new ArrayList<Map<String, Object>>(
						0);
				try {
					HttpResponse dresponse = dhttpclient.execute(dhttpget,
							localContext);
					if (_callback != null) {
                        String respStr = EntityUtils.toString(dresponse
								.getEntity());
                        status = dresponse.getStatusLine().getStatusCode();
                        statusMessage = dresponse.getStatusLine().getReasonPhrase();
						if (respStr.startsWith("[")) {
                            ArrayList<HashMap<String,Object>> objs = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                objs = mapper.readValue(respStr,
                                        new TypeReference<ArrayList<HashMap<String,Object>>>(){});
                                for (HashMap<String,Object> o : objs){
                                    l.add(o);
                                }
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        } else if (respStr.startsWith("{")) {
                            Map<String,Object> map = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                map = mapper.readValue(respStr,
                                        new TypeReference<HashMap<String,Object>>(){});
                                l.add(map);
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
						}
					}
				} catch (Exception e) {
                    error = e;
					Log.e("REST Get Error", e.toString());
				}
				return l;
			}

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
                if (error!=null) {
                    _callback.onError(error, -1);
                } else if(status>=400 && status<600){
                    _callback.onError(new Exception(statusMessage),status);
                } else {
                    _callback.onFinish(result);
                }
			}
		};
		atAsyncTask.execute(null, null, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String apiSection, Map<String, String> params,
			Callback callback) {
		String spec = apiSection;
		if (!spec.equals("")) {
			spec = spec.startsWith("/") ? spec.substring(1) : spec;
			spec = spec.endsWith("/") ? spec : spec + "/";
		}
		final HttpPost dhttppost = new HttpPost(uri + spec);
		dhttppost.setHeader("Content-Type", "application/json;charset=UTF-8");
		final Callback _callback = callback;
		AsyncTask<Map<String, String>, Integer, List<Map<String, Object>>> at = new AsyncTask<Map<String, String>, Integer, List<Map<String, Object>>>() {

            Exception error = null;

			@Override
			protected List<Map<String, Object>> doInBackground(
					Map<String, String>... params) {
				List<Map<String, Object>> l = new ArrayList<Map<String, Object>>(
						0);
				try {
					dhttppost.setEntity(parsePostParams(params[0]));
					HttpResponse dresponse = dhttpclient.execute(dhttppost,
							localContext);
					if (_callback != null) {
						String respStr = EntityUtils.toString(dresponse
								.getEntity());
                        status = dresponse.getStatusLine().getStatusCode();
                        statusMessage = dresponse.getStatusLine().getReasonPhrase();
                        if (respStr.startsWith("[")) {
                            ArrayList<HashMap<String,Object>> objs = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                objs = mapper.readValue(respStr,
                                        new TypeReference<ArrayList<HashMap<String,Object>>>(){});
                                for (HashMap<String,Object> o : objs){
                                    l.add(o);
                                }
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        } else if (respStr.startsWith("{")) {
                            Map<String,Object> map = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                map = mapper.readValue(respStr,
                                        new TypeReference<HashMap<String,Object>>(){});
                                l.add(map);
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    error = e;
                    Log.e("REST Post Error", e.toString());
                }
                return l;
            }

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
                if (error!=null) {
                    _callback.onError(error, -1);
                } else if(status>=400 && status<600){
                    _callback.onError(new Exception(statusMessage),status);
                } else {
                    _callback.onFinish(result);
                }
			}
		};
		at.execute(params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateData(String apiSection, String pk,
			Map<String, String> params, Callback callback) {
		if (!params.containsKey(pk)) {
			Log.e("SQLiteDataSource Error", String.format(
					"'%s' key does not exists into params map", pk));
			return;
		}
		String spec = apiSection;
		if (!spec.equals("")) {
			spec = spec.startsWith("/") ? spec.substring(1) : spec;
			spec = spec.endsWith("/") ? spec : spec + "/";
		}
		spec += params.get(pk) + "/";
		final HttpPut dhttpput = new HttpPut(uri + spec);
		dhttpput.setHeader("Content-Type", "application/json;charset=UTF-8");
		final Callback _callback = callback;
		AsyncTask<Map<String, String>, Integer, List<Map<String, Object>>> at = new AsyncTask<Map<String, String>, Integer, List<Map<String, Object>>>() {

            Exception error = null;

			@Override
			protected List<Map<String, Object>> doInBackground(
					Map<String, String>... params) {
				List<Map<String, Object>> l = new ArrayList<Map<String, Object>>(
						0);
				try {
					dhttpput.setEntity(parsePostParams(params[0]));
					HttpResponse dresponse = dhttpclient.execute(dhttpput,
							localContext);
					if (_callback != null) {
						String respStr = EntityUtils.toString(dresponse
								.getEntity());
                        status = dresponse.getStatusLine().getStatusCode();
                        statusMessage = dresponse.getStatusLine().getReasonPhrase();
                        if (respStr.startsWith("[")) {
                            ArrayList<HashMap<String,Object>> objs = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                objs = mapper.readValue(respStr,
                                        new TypeReference<ArrayList<HashMap<String,Object>>>(){});
                                for (HashMap<String,Object> o : objs){
                                    l.add(o);
                                }
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        } else if (respStr.startsWith("{")) {
                            Map<String,Object> map = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                map = mapper.readValue(respStr,
                                        new TypeReference<HashMap<String,Object>>(){});
                                l.add(map);
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        }
					}
				} catch (Exception e) {
					Log.e("REST Update Error", e.toString());
				}
				return l;
			}

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
                if (error!=null) {
                    _callback.onError(error, -1);
                } else if(status>=400 && status<600){
                    _callback.onError(new Exception(statusMessage),status);
                } else {
                    _callback.onFinish(result);
                }
			}
		};
		at.execute(params);
	}

	@Override
	public void deleteData(String apiSection, String id, Callback callback) {
		String spec = apiSection;
		if (!spec.equals("")) {
			spec = spec.startsWith("/") ? spec.substring(1) : spec;
			spec = spec.endsWith("/") ? spec : spec + "/";
		}
		final HttpDelete dhttpdel = new HttpDelete(uri + spec + id);
		dhttpdel.setHeader("Content-Type", "application/json;charset=UTF-8");
		final Callback _callback = callback;
		AsyncTask<Void, Integer, List<Map<String, Object>>> at = new AsyncTask<Void, Integer, List<Map<String, Object>>>() {

            Exception error = null;

			@Override
			protected List<Map<String, Object>> doInBackground(Void... params) {
				List<Map<String, Object>> l = new ArrayList<Map<String, Object>>(
						0);
				try {
					HttpResponse dresponse = dhttpclient.execute(dhttpdel,
							localContext);
					if (_callback != null) {
						String respStr = EntityUtils.toString(dresponse
								.getEntity());
                        status = dresponse.getStatusLine().getStatusCode();
                        statusMessage = dresponse.getStatusLine().getReasonPhrase();
                        if (respStr.startsWith("[")) {
                            ArrayList<HashMap<String,Object>> objs = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                objs = mapper.readValue(respStr,
                                        new TypeReference<ArrayList<HashMap<String,Object>>>(){});
                                for (HashMap<String,Object> o : objs){
                                    l.add(o);
                                }
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        } else if (respStr.startsWith("{")) {
                            Map<String,Object> map = null;
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                map = mapper.readValue(respStr,
                                        new TypeReference<HashMap<String,Object>>(){});
                                l.add(map);
                                error = null;
                            } catch (Exception e) {
                                error = e;
                                Log.e("REST Get Error", e.toString());
                            }
                        }
					}
				} catch (Exception e) {
					Log.e("REST Delete Error", e.toString());
				}
				return l;
			}

			@Override
			protected void onPostExecute(List<Map<String, Object>> result) {
                if (error!=null) {
                    _callback.onError(error, -1);
                } else if(status>=400 && status<600){
                    _callback.onError(new Exception(statusMessage),status);
                } else {
                    _callback.onFinish(result);
                }
			}
		};
		at.execute(null, null, null);
	}

}
