package com.w8iig.trafficfines.util;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;

public class UtilString {
	static public String toLowerCaseAndRemoveAccents(String string) {
		string = string.toLowerCase(UtilLocale.getVI());
		string = string.replace("đ", "d");

		String normalized = Normalizer.normalize(string, Normalizer.Form.NFD);
		String noAccents = normalized.replaceAll("[^\\p{ASCII}]", "");

		return noAccents;
	}

	static public String formatNumber(double number) {
		NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
		double delta = number - Math.floor(number);
		if (delta < 0.1) {
			return nf.format(number);
		} else {
			nf.setMaximumFractionDigits(1);
		}

		return nf.format(number);
	}
}
