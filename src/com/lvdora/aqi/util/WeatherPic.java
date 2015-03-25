package com.lvdora.aqi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lvdora.aqi.R;
/**
 * 根据图片编号返回天气图片
 * @author Dave
 *
 */
public class WeatherPic {
	public static Bitmap getPic(Context c, int index, int type){
		Bitmap bmp = null;
		switch(index){
		case 0:
			if(type == 0){
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_sunny);
			}else{
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_sunny_n);
			}
			break;
		case 1:
			if(type == 0){
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_cloudy);
			}else{
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_cloudy_n);
			}
			break;
		case 3:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_showers);
			break;
		case 4:
		case 5:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_thunderstorm);
			break;
		case 9:
		case 10:
		case 22:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_heavy_rain);
			break;
		case 13:
			if(type == 0){
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_chance_of_snow);
			}else{
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_chance_of_snow_n);
			}
			break;
		case 20:
		case 29:
		case 30:
		case 31:
			if(type == 0){
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_dust);
			}else{
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_dust_n);
			}
			break;
		case 18:
		case 32:
			if(type == 0){
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_fog);
			}else{
				bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_fog_n);
			}
			break;
		case 19:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_icy);
			break;
		case 16:
		case 17:
		case 27:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_heavy_snow);
			break;
		case 26:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_light_snow);
			break;
		case 2:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_overcast);
			break;
		case 7:
		case 8:
		case 21:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_rain);
			break;
			/*bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_sleet);
			break;*/
		case 14:
		case 15:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_snow);
			break;
		case 6:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_snow_rain);
			break;
		case 11:
		case 12:
		case 23:
		case 24:
		case 25:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_storm);
			break;
		case 28:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_storm_snow);
			break;
		default:
			bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.wip_sleet);
			break;
		}
		return bmp;
	}
	public static Bitmap getSmallPic(Context c, int index, int type){
		return Bitmap.createScaledBitmap(getPic(c, index, type), Constants.picSize, Constants.picSize, true);
	}
}
