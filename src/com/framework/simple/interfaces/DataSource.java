package com.framework.simple.interfaces;

import java.util.Map;

public interface DataSource {
	public void getData(String apiSection, Map<String, String> params,
			Callback callback);

	public void saveData(String apiSection, Map<String, String> params,
			Callback callback);

	void updateData(String apiSection, Map<String, String> params,
			Callback callback);

	void deleteData(String apiSection, String id, Callback callback);
}
