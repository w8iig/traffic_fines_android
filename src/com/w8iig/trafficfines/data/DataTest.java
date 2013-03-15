package com.w8iig.trafficfines.data;

import com.w8iig.trafficfines.R;

public class DataTest extends DataAbstract {

	@Override
	public int getTitleResId() {
		return R.string.data_test_title;
	}

	@Override
	public String[] getSearchTexts() {
		return new String[] { "abc", "def", "ghi", "jkl", "mno", "pqr", "stu",
				"vwx", "yz", "123" };
	}

	@Override
	public int[] getNameResIds() {
		return new int[] { R.string.data_test_fine_1_name,
				R.string.data_test_fine_2_name, R.string.data_test_fine_3_name,
				R.string.data_test_fine_4_name, R.string.data_test_fine_5_name,
				R.string.data_test_fine_6_name, R.string.data_test_fine_7_name,
				R.string.data_test_fine_8_name, R.string.data_test_fine_9_name,
				R.string.data_test_fine_10_name };
	}

	@Override
	public int[] getDescriptionResIds() {
		return new int[] { R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description,
				R.string.dummy_fine_description };
	}

	public int[] getValuePairs() {
		return new int[] { 0, 100000, 0, 200000, 0, 500000, 0, 1000000, 0, 1250000, 0, 60, 0, 70, 0,
				80, 0, 90, 50, 100 };
	}
}
