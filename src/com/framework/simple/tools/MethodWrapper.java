package com.framework.simple.tools;

import java.lang.reflect.Method;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class MethodWrapper {

	public OnClickListener OnClickListener(final Object receiver,
			final Method mth) {
		OnClickListener r = new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					mth.invoke(receiver, v);
				} catch (Exception e) {
					SimpleLog.error(new Exception(
							"Wrapped Method called exception: "
									+ e.getMessage(), e));
				}
			}
		};
		return r;
	}

	public OnLongClickListener OnLongClickListener(final Object receiver,
			final Method mth) {
		OnLongClickListener r = new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Object res = null;
				try {
					res = mth.invoke(receiver, v);
					return (Boolean) res;
				} catch (ClassCastException cce) {
					SimpleLog.error(new Exception(String.format(
							"Cast exception, Boolean expected, given %s", res
									.getClass().getName()), cce));
				} catch (Exception e) {
					SimpleLog.error(new Exception(
							"Wrapped Method called exception: "
									+ e.getMessage(), e));
				}
				return false;
			}

		};
		return r;
	}

	// TODO: implement another methods
	
}
