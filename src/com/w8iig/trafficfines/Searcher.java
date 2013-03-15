package com.w8iig.trafficfines;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.w8iig.trafficfines.data.DataAbstract;

class Searcher {

	static private final String TAG = "Searcher";

	private DataAbstract mData;
	private String[] mSearchTexts;

	Searcher(DataAbstract data) {
		mData = data;

		prepareData();
	}

	List<Integer> search(String query) {
		List<Integer> fineIds = new ArrayList<Integer>();

		query = normalizeQuery(query);

		if (mSearchTexts != null) {
			Log.v(TAG, String.format("search(%s): records=%d", query,
					mSearchTexts.length));

			for (int i = 0; i < mSearchTexts.length; i++) {
				if (match(mSearchTexts[i], query)) {
					fineIds.add(i);
				}
			}

			Log.v(TAG, String.format("search(%s) -> fineIds.size=%d", query,
					fineIds.size()));
		} else {
			Log.e(TAG, "search: mSearchTexts=null");
		}

		return fineIds;
	}

	static private String normalizeQuery(String query) {
		String normalized;

		if (null == query) {
			normalized = "";
		} else {
			normalized = query.toLowerCase(Locale.US);
		}

		// TODO

		return normalized;
	}

	static private boolean match(String searchText, String query) {
		// TODO

		return searchText.indexOf(query) != -1;
	}

	private void prepareData() {
		if (mData != null) {
			mSearchTexts = mData.getSearchTexts();
		} else {
			Log.e(TAG, "prepareData: mData=null");
		}
	}

}
