package com.w8iig.trafficfines.data;

import android.util.Log;

public abstract class DataAbstract {

	static private final String TAG = "DataAbstract";

	public abstract int getTitleResId();

	public abstract int[] getNameResIds();

	public abstract int[] getDescriptionResIds();

	public abstract int[] getValuePairs();

	public abstract String[] getSearchTexts();

	public int getFineNameResId(int fineId) {
		int[] resIds = getNameResIds();
		int resId = 0;

		if (resIds != null) {
			if (fineId >= 0 && fineId < resIds.length) {
				resId = resIds[fineId];
			} else {
				Log.e(TAG, String.format("getFineNameResId -> "
						+ "fineId invalid: %d (resIds.length=%d)", fineId,
						resIds.length));
			}
		} else {
			Log.e(TAG, "getFineNameResId -> resIds=null");
		}

		return resId;
	}

	public int getFineDescriptionResId(int fineId) {
		int[] resIds = getDescriptionResIds();
		int resId = 0;

		if (resIds != null) {
			if (fineId >= 0 && fineId < resIds.length) {
				resId = resIds[fineId];
			} else {
				Log.e(TAG, String.format("getFineDescriptionResId -> "
						+ "fineId invalid: %d (resIds.length=%d)", fineId,
						resIds.length));
			}
		} else {
			Log.e(TAG, "getFineDescriptionResId -> resIds=null");
		}

		return resId;
	}

	public FineValues getFineValue(int fineId) {
		int[] pairs = getValuePairs();
		FineValues value = null;

		if (pairs != null) {
			if (fineId >= 0 && fineId < pairs.length / 2) {
				value = new FineValues();
				value.low = pairs[fineId * 2];
				value.high = pairs[fineId * 2 + 1];
			} else {
				Log.e(TAG, String.format("getFineValue -> "
						+ "fineId invalid: %d (pairs.length=%d", fineId,
						pairs.length));
			}
		} else {
			Log.e(TAG, "getFineValue -> pairs=null");
		}

		return value;
	}

	public FineValues getFineValueBigCity(int fineId) {
		return null;
	}

	public int getLicenseDays(int fineId) {
		return 0;
	}

	public int getVehicleDay(int fineId) {
		return 0;
	}

	public static class FineValues {
		int low;
		int high;
		
		public int getLow() {
			return low;
		}

		public int getHigh() {
			return high;
		}

		public boolean isRange() {
			return 0 < low && low < high;
		}
	}
}
