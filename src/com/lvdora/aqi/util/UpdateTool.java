package com.lvdora.aqi.util;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.lvdora.aqi.R;

public class UpdateTool {

	public static void startLoadingAnim(Activity activity,
			ImageButton updateBtn, ImageView imageAnimation) {

		updateBtn.setVisibility(8);
		imageAnimation.setVisibility(0);
		Animation localAnimation = AnimationUtils.loadAnimation(activity,
				R.anim.update_rotate);
		localAnimation.setInterpolator(new LinearInterpolator());
		if (localAnimation != null)
			imageAnimation.startAnimation(localAnimation);
	}

	public static void stopLoadingAnim(ImageButton updateBtn,
			ImageView imageAnimation) {
		imageAnimation.clearAnimation();
		updateBtn.setVisibility(0);
		imageAnimation.setVisibility(8);
	}
}
