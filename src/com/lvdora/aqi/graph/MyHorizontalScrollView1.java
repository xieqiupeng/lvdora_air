package com.lvdora.aqi.graph;

import java.util.ArrayList;
import com.lvdora.aqi.R;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class MyHorizontalScrollView1 extends HorizontalScrollView {

	private StudyGraphView1 studyGraphView1;
	private ArrayList<PointF> graphPoints;
	
	private float showTextX;
	
	private float marginRight;
	private float marginLeft;
	private float scrollSpacing;
	private float screenWidth;
	
	public MyHorizontalScrollView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);

		if (hasWindowFocus) {
			studyGraphView1 = (StudyGraphView1) findViewById(R.id.study_graph1);
		
			scrollSpacing=studyGraphView1.getSpacingOfX();
			
			marginRight=scrollSpacing;
			marginLeft=scrollSpacing;
			
			graphPoints = studyGraphView1.getPoints();
			
			showTextX=screenWidth-marginRight;
			
			this.scrollTo(studyGraphView1.getWidth(), 0);
//			
//			Log.d("lvdora", "sml:"+studyGraphView1.getWidth());
		}
	}
	
	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		
		if(oldl==0){
			return ;
		}
		
		float scaleplateNow=l + showTextX;
		float scaleplatePre=oldl + showTextX;
		
		if (graphPoints != null) {
			for (int i = 0; i < graphPoints.size(); i++) {
				float pointX=graphPoints.get(i).x;
				if ((pointX>=scaleplateNow && pointX<=scaleplatePre)
						||(pointX>=scaleplatePre && pointX<=scaleplateNow)) {
					
					if(l>oldl){
						if(showTextX<=(screenWidth-marginRight) ){
							showTextX=		
								scrollSpacing*(float)(i+1)*((float)(StudyGraphView.SHOW_NUM)/(float)(graphPoints.size()-1));
						}
					}else{
						
						if(showTextX>=marginLeft ){
							showTextX=screenWidth
								-scrollSpacing*((float)(graphPoints.size()-1-i))*(((float)StudyGraphView.SHOW_NUM)/(float)(graphPoints.size()-1));	
						}
						
					}
					
					studyGraphView1.setCurrentIndex(i);
					studyGraphView1.invalidate();
					break;
				}
			}
		}

	}
	
}
