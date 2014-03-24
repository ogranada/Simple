package com.framework.simple.views;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class ViewValueTool {

	public static void storeValue(View view, Object value) {
		String classname = view.getClass().getName();
		if (classname.endsWith("TextView")) {
			TextView tv = (TextView) view;
			tv.setText(String.valueOf(value));
		} else if (classname.endsWith("EditText")) {
			TextView tv = (TextView) view;
			tv.setText(String.valueOf(value));
		} else if (classname.endsWith("ImageView")) {
			ImageView imageView = (ImageView) view;
			try {
				URL url = new URL(String.valueOf(value));
				Bitmap bmp = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());
				imageView.setImageBitmap(bmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (classname.endsWith("RadioButton")) {
			RadioButton rb = (RadioButton) view;
			rb.setChecked(((Boolean) value).booleanValue());
		} else if (classname.endsWith("CheckBox")) {
			CheckBox chb = (CheckBox) view;
			chb.setChecked(((Boolean) value).booleanValue());
		} else if (classname.endsWith("ProgressBar")) {
			ProgressBar pb = (ProgressBar) view;
			pb.setProgress(((Integer) value).intValue());
		}
	}

}
