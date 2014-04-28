package com.framework.simple.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.framework.simple.interfaces.injection.InjectMethod;
import com.framework.simple.interfaces.injection.InjectView;
import com.framework.simple.interfaces.Injectable;

public class Injector {

	View view;
	Context context;

	public Injector(View v) {
		view = v;
		context = v.getContext();
	}

	public Injector(Context c) {
		view = null;
		context = c;
	}

	public void injectViews(Injectable obj) {
		Field[] f = obj.getClass().getDeclaredFields();
		Object inst;
		for (Field a : f) {
			inst = null;
			InjectView i = a.getAnnotation(InjectView.class);
			if (i != null && i.id() != -1) {
				a.setAccessible(true);
				try {
					if (view != null) {
						inst = view.findViewById(i.id());
					} else {
						inst = ((Activity) context).findViewById(i.id());
					}
					a.set(obj, inst);
				} catch (Exception e) {
					if (inst == null) {
						SimpleLog.error(new Exception(String.format(
								"Id %s doesn't exists.", i.id()), e));
					}
					if (!a.getType().equals(inst.getClass())) {
						String msg = String
								.format("Injection Error, verify target type, %s != %s",
										a.getType().getName(), inst.getClass()
												.getName());
						SimpleLog.error(new Exception(msg, e));
					}
				}
			}
		}
	}

	public void injectMethodsIntoViews(Injectable obj) {
		Method[] methods = obj.getClass().getMethods();
		for (Method method : methods) {
			InjectMethod i = method.getAnnotation(InjectMethod.class);
			if (i != null && i.id() != -1) {
				View v = null;
				if (view != null) {
					v = view.findViewById(i.id());
				} else {
					v = ((Activity) context).findViewById(i.id());
				}
				if (v != null) {
					try {
						MethodWrapper wrapper = new MethodWrapper();
						Method mth = wrapper.getClass().getMethod(i.method(),
								Object.class, Method.class);
						Object listener = mth.getReturnType().cast(mth.invoke(wrapper, obj, method));						
						mth = v.getClass().getMethod("set"+i.method(), mth.getReturnType());
						mth.invoke(v, listener);
					} catch (NoSuchMethodException e) {
						SimpleLog.error(new Exception(String.format(
								"Error on method Injection (Method %s not found)",
								e.getMessage()), e));
						e.printStackTrace();
					}catch (Exception e) {
						SimpleLog.error(new Exception(String.format(
								"Error on method Injection (%s)",
								e.getMessage()), e));
						e.printStackTrace();
					}
				} else {
					if (v == null) {
						SimpleLog.error(new Exception(String.format(
								"Id %s doesn't exists.", i.id())));
					}
				}
			}
		}
	}

	// TODO: inject menu items
	// TODO: inject actionbar items
	// TODO: inject action modes
	// TODO: inject WebView javascript methods
}
