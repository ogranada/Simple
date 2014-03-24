package com.framework.simple.views;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.view.View;

@SuppressWarnings("all")
public class SimpleView {

	private View view;
	private Class<?> identifiers;
	private Map<String, Object> values;
	private Map<String, View> views;

	public SimpleView(View view, Class<?> R_id_class) {
		this.view = view;
		identifiers = R_id_class;
		views = new HashMap<String, View>();
	}

	public SimpleView(Activity activity, Class<?> R_id_class) {
		this(activity.getWindow().getDecorView()
				.findViewById(android.R.id.content), R_id_class);
	}

	public void setValues(Map<String, Object> values) {
		this.values = values;
		if (values == null) {
			return;
		}
		for (String key : this.values.keySet()) {
			try {
				Field campo = identifiers.getField(key.toLowerCase());
				try {
					int id = Integer.valueOf(campo.get(null).toString());
					View subview = this.view.findViewById(id);
					if (subview != null) {
						ViewValueTool.storeValue(subview, values.get(key));
						views.put(key, subview);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

}
