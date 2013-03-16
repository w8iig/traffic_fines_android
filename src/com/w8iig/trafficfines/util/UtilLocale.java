package com.w8iig.trafficfines.util;

import java.util.Locale;

import android.util.Log;

public class UtilLocale {
	static private String TAG = "UtilLocale";
	static private Locale VI = null;

	static public Locale getVI() {
		if (VI == null) {
			Locale[] locales = Locale.getAvailableLocales();
			for (Locale locale : locales) {
				if ("vi".equals(locale.getLanguage())) {
					VI = locale;
				}
			}
			
			if (VI == null) {
				// no VI locale available, use the default locale
				VI = Locale.getDefault();
				Log.w(TAG, String.format("Using default locale: %s", VI));
			} else {
				Log.v(TAG, String.format("Obtained VI locale: %s", VI));
			}
		}
		
		return VI;
	}
}
