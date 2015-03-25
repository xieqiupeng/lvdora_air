package com.lvdora.aqi.util;

import android.content.Context;

import com.lvdora.aqi.R;

public class Constants {
	public static int picSize;
	public Constants(Context c){
		picSize = (int) c.getResources().getDimension(R.dimen.picSize);
		//System.out.println(picSize);
	}
}
