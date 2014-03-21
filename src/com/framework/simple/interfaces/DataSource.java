package com.framework.simple.interfaces;

import java.util.Map;

public interface DataSource {
	public void getData(String section, Map<String, String> params,
			Callback callback);

	public void saveData(String section, Map<String, String> params,
			Callback callback);

	void updateData(String section, String pk, Map<String, String> params,
			Callback callback);

	void deleteData(String section, String id, Callback callback);
}
