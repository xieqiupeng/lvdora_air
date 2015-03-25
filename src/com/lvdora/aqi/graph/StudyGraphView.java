package com.lvdora.aqi.graph;

import java.util.ArrayList;

import com.lvdora.aqi.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;


/**
 * 
 * @author sml
 * 
 */
@SuppressLint("DrawAllocation")
public class StudyGraphView extends View {
	

	public static final float TOTAL_Y_DP=150f;		//y坐标总的高度
	
	public static final float COORDINATE_MARGIN_TOP_DP = 30f; // 坐标系距离顶部的高度
	public static final float COORDINATE_MARGIN_BOTTOM_DP = 30f; // 坐标系距离上边的间距
	
	public static final int SHOW_NUM=13;
	
	
	private float mTotalWidth; // X轴长度（只是数据的宽度，不包括左边和右边的留空）
	private float mTotalHeight; // Y轴的长度（只是数据的高度，不包括上方和下方的留空）
	private float spacingOfX; // x坐标的单元间距(px)
	private float spacingOfY; // y坐标的单元间距(px)
	private float coordinateMarginTop;	//坐标系距离顶部的距离(px)
	private float coordinateMarginLeft;	//坐标系距离左边的距离(px)
	private float coordinateMarginRight;	//坐标系距离右边的距离(px)
	private float coordinateMarginBottom;	//坐标系距离下方的高度(px)
	
	private ArrayList<PointF> points; // 各个点
	private ArrayList<StudyGraphItem> studyGraphItems;
	
	private StudyGraphItem maxEnergy;
	private StudyGraphItem minEnergy;
	private Context mContext;
	
	private int currentIndex;
	
	public StudyGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		initParam();
	}
	
	private void initParam(){
		//Y坐标总高度设为定值，单位间距动态计算
		mTotalHeight=Utils.dip2px(mContext, TOTAL_Y_DP);
		
		spacingOfX=getResources().getDisplayMetrics().widthPixels/SHOW_NUM;
		
		coordinateMarginTop=Utils.dip2px(mContext, COORDINATE_MARGIN_TOP_DP);
		coordinateMarginLeft= 20f;//spacingOfX;
		coordinateMarginRight= 40f;//spacingOfX;
		coordinateMarginBottom=Utils.dip2px(mContext, COORDINATE_MARGIN_BOTTOM_DP);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paint=new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(3);
		paint.setTextSize(Utils.sp2px(mContext, 13));
		
		/* 绘制底部的横线、文字、以及向上的线条 */
		canvas.drawLine(coordinateMarginLeft, mTotalHeight + coordinateMarginTop,
				mTotalWidth+coordinateMarginLeft+coordinateMarginRight, mTotalHeight + coordinateMarginTop, paint);
		
		for (int i = 0; i < studyGraphItems.size(); i++) {
			StudyGraphItem energy = studyGraphItems.get(i);
			PointF textPoint = points.get(i);
					
			paint.setStrokeWidth(1);
		//	paint.setColor(getResources().getColor(R.color.graph_text));
			// 绘制底部的 文字
			canvas.drawText(energy.getDate(), textPoint.x - Utils.sp2px(mContext, 10), mTotalHeight
					+ coordinateMarginTop + Utils.sp2px(mContext, 13+5), paint);
			
			//在节点上方表数据
			 canvas.drawText(energy.getAqi() + "",textPoint.x - 10,textPoint.y - 20, paint);// 这里注意，坐标(180,180)是文本的左下点坐标
				
		}
		
		/* 绘制曲线*/
		for (int i = 0; i < points.size(); i++) {
			StudyGraphItem energy = studyGraphItems.get(i);
			paint.setStrokeWidth(3);
			paint.setColor(Color.WHITE);
			PointF startPoint = points.get(i);
			
			if (i + 1 != points.size()) {
				
				PointF endPoint = points.get(i + 1);
				// 绘制曲线，并且覆盖剪切后的锯齿
				canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
			}
			
			
			//绘制节点坐标
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setStrokeWidth(10.0f);
			 if ( energy.getAqi() < 50) {
				paint.setColor(getResources().getColor(R.color.you));
			} else if(energy.getAqi() >= 50 && energy.getAqi() < 100){
			    paint.setColor(getResources().getColor(R.color.liang));
			} else if(energy.getAqi() >= 100 && energy.getAqi() < 150){
				paint.setColor(getResources().getColor(R.color.qingdu));
			} else if(energy.getAqi() >= 150 && energy.getAqi() < 200){
				paint.setColor(getResources().getColor(R.color.zhongdu));
			} else if(energy.getAqi() >= 200 && energy.getAqi() < 300){
				paint.setColor(getResources().getColor(R.color.yanzhong));
			}  else if(energy.getAqi() > 300 ){
				paint.setColor(getResources().getColor(R.color.da));
			}
			 

			canvas.drawPoints(new float[]{startPoint.x,startPoint.y}, paint);
		}
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		setMeasuredDimension((int)(mTotalWidth+coordinateMarginLeft+coordinateMarginRight), (int)(mTotalHeight+coordinateMarginTop+coordinateMarginBottom));
	}
	
	/**
	 * 设置数据(初始化进行)
	 */
	public void setData(ArrayList<StudyGraphItem> studyGraphItems) {
		
		this.studyGraphItems = studyGraphItems;
		maxEnergy = findMaxPowers(studyGraphItems);
		minEnergy = findMinPowers(studyGraphItems);
		
		
		//mTotalWidth=(studyGraphItems.size()-1)*spacingOfX;
		mTotalWidth=23*spacingOfX;
		

		//y坐标间隔是根据用户的最大值动态计算的
		spacingOfY = (mTotalHeight)/(maxEnergy.getAqi()-minEnergy.getAqi()+10);
		
		points = new ArrayList<PointF>();
		for (int i = 0; i < studyGraphItems.size(); i++) {
			studyGraphItems.get(i);
			float f = studyGraphItems.get(i).getAqi()-minEnergy.getAqi();
			float y = mTotalHeight+coordinateMarginTop - f * spacingOfY - 5*spacingOfY;
			float x = (i * spacingOfX + coordinateMarginLeft);
			PointF point = new PointF(x, y);
			points.add(point);
		}		
		
	}

	/**
	 * 找到 数据集合中 最高能量 对应的脚标
	 * 
	 * @param powers
	 * @return
	 */
	private static StudyGraphItem findMaxPowers(ArrayList<StudyGraphItem> energys) {
		StudyGraphItem energy = new StudyGraphItem();
		energy.setAqi(0);
		for (int i = 0; i < energys.size(); i++) {
			if (energys.get(i).getAqi() > energy.getAqi()) {
				energy = energys.get(i);
			}
		}
		return energy;
	}
	
	private static StudyGraphItem findMinPowers(ArrayList<StudyGraphItem> energys) {
		StudyGraphItem energy = new StudyGraphItem();
		energy.setAqi(1000);
		for (int i = 0; i < energys.size(); i++) {
			if (energys.get(i).getAqi() < energy.getAqi()) {
				energy = energys.get(i);
			}
		}
		return energy;
	}
	
	public float getSpacingOfX(){
		return spacingOfX;
	}
	
	public ArrayList<PointF> getPoints(){
		return points;
	}
	
	public ArrayList<StudyGraphItem> getStudyGraphItems(){
		return studyGraphItems;
	}
	
	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public int getCurrentIndex() {
		return currentIndex;
	}
	
}
