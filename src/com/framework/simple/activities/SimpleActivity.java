package com.framework.simple.activities;

import android.app.Activity;
import android.os.Bundle;

import com.framework.simple.interfaces.Injectable;
import com.framework.simple.tools.Injector;

public class SimpleActivity extends Activity implements Injectable {

	Injector injector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		injector = new Injector(this);
	}

	public void inject() {
		if (injector != null) {
			injector.injectViews(this);
			injector.injectMethodsIntoViews(this);
		}
	}

}
