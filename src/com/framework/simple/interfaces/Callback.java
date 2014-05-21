package com.framework.simple.interfaces;

import java.util.List;
import java.util.Map;

public interface Callback {
	void onFinish(List<Map<String,Object>> l);
    void onError(Exception e, int status);
}
