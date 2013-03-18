package com.w8iig.trafficfines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class ActivityDoc extends SherlockActivity {

	static public final String EXTRA_FINE_UNIQUE_ID = "fineUniqueId";
	static private final String TAG = "ActivityDoc";
	static private final String DOC_2010_PREFIX = "34_2010_ndcp";
	static private final String DOC_2010_FILEPATH = "file:///android_res/raw/docs_34_2010_ndcp.html";
	static private final String DOC_2012_PREFIX = "71_2012_ndcp";
	static private final String DOC_2012_FILEPATH = "file:///android_res/raw/docs_71_2012_ndcp.html";

	private WebView mWebView;
	private Pattern mDoc2012Pattern = Pattern
			.compile("(\\d+)_(\\d+)_(\\d+)_(\\d+)(_(\\w+))?");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doc);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mWebView = (WebView) findViewById(R.id.wv_doc);
		mWebView.setClickable(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		Intent intent = getIntent();
		if (intent != null) {
			initDoc(intent);
		} else {
			Log.w(TAG, "onResume: getIntent()=null");
		}
	}

	private void initDoc(Intent intent) {
		String fineUniqueId = intent.getStringExtra(EXTRA_FINE_UNIQUE_ID);
		if (fineUniqueId == null) {
			Log.w(TAG, "initDoc: fineUniqueId=null");
			return;
		}
		String filepath = null;
		String hash = null;

		if (fineUniqueId.startsWith(DOC_2010_PREFIX)) {
			// 2010 document
			filepath = DOC_2010_FILEPATH;

			hash = fineUniqueId.substring(DOC_2010_PREFIX.length()).replace(
					"_", "");
		} else if (fineUniqueId.startsWith(DOC_2012_PREFIX)) {
			// 2012 document
			filepath = DOC_2012_FILEPATH;

			String hashRaw = fineUniqueId
					.substring(DOC_2012_PREFIX.length() + 1);
			Matcher m = mDoc2012Pattern.matcher(hashRaw);

			if (m.matches()) {
				hash = String.format("%s%s_%s%s%s", m.group(1), m.group(2),
						m.group(3), m.group(4),
						m.group(6) == null ? "" : m.group(6));
			} else {
				hash = hashRaw.replace("_", "");
			}
		} else {
			Log.w(TAG, String.format("initDoc -> "
					+ "Unrecognized unique ID '%s'", fineUniqueId));
			return;
		}

		String url = String.format("%s#%s", filepath, hash);
		Log.v(TAG, String.format("initDoc -> mWebView.loadUrl(%s)", url));

		mWebView.loadUrl(url);
	}
}
