package com.framework.simple.interfaces;

import java.util.Map;

public interface DataSource {
	public void getData(String apiSection, Map<String, String> params,
			Callback callback);

	public void postData(String apiSection, Map<String, String> params,
			Callback callback);

	void putData(String apiSection, Map<String, String> params,
			Callback callback);

	void deleteData(String apiSection, String id, Callback callback);
}
