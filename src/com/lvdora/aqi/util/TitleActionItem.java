package com.lvdora.aqi.util;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class TitleActionItem {
	public Drawable mDrawable;
	public CharSequence mTitle;
	
	public TitleActionItem(Drawable drawable, CharSequence title){
		this.mDrawable = drawable;
		this.mTitle = title;
	}
	
	public TitleActionItem(Context context, int titleId, int drawableId){
		this.mTitle = context.getResources().getText(titleId);
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}
	
	public TitleActionItem(Context context, CharSequence title, int drawableId) {
		this.mTitle = title;
		this.mDrawable = context.getResources().getDrawable(drawableId);
	}
}
