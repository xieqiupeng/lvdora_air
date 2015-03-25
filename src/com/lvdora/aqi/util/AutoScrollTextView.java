package com.lvdora.aqi.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * 
 * 功能：实现跑马灯文字效果 ,可控制文字速度,可控制文字方向,可控制滚动状态
 * 
 */
public class AutoScrollTextView extends TextView implements Runnable {


	private int currentScrollX;			// 当前滚动的位置
    private boolean isStop = false;		// 控制滚动的布尔变量
    private int textWidth;				// 显示文字的宽度
    private boolean isMeasure = false;  // 文字是否测量的宽度
    private int scrollSpeed = 1; 		// 默认滚动速度
    private boolean scrollDirectionIsLeft = false; // 默认滚动的方向速度
   
    /**
     * 构造器
     * @param context
     */
    public AutoScrollTextView(Context context) {
            super(context);
    }

    /**
     * 构造器
     * @param context
     * @param attrs
     */
    public AutoScrollTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
    }

    /**
     * 构造器
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
    }

    
    @Override
    protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!isMeasure) {// 文字宽度只需获取一次就可以了
                    getTextWidth();
                    isMeasure = true;
            }
    }

    /**
     * 获取文字宽度
     */
    private void getTextWidth() {   	
            Paint paint = this.getPaint();
            String str = this.getText().toString();
            textWidth = (int) paint.measureText(str);
    }

    
  
    @Override
    public void setText(CharSequence text, BufferType type) {  //重写setText 在setText的时候重新计算text的宽度
            super.setText(text, type);
            this.isMeasure = false;
    }
    
    @Override
    public void run() {
    	
        if (isStop) {
                return;
        }
    	
    	if(scrollDirectionIsLeft){
    		
    		currentScrollX += scrollSpeed;
            scrollTo(currentScrollX, 0);
            if (getScrollX()>= this.getWidth() || getScrollX()>=textWidth) {
                currentScrollX = -getWidth();             
                scrollTo(getWidth(),0);           
            }
            
    	}else{
    		currentScrollX -= scrollSpeed;
    		scrollTo(currentScrollX, 0);
	         if (getScrollX() <= -(this.getWidth()) || getScrollX()<=-textWidth) {	        	
	        	 	scrollTo(textWidth, 0);
	                currentScrollX =getWidth();
	        }
    	}        
        postDelayed(this, 5);
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
            isStop = false;
            this.removeCallbacks(this);
            post(this);
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
            isStop = true;
    }

    /**
     * 从头开始滚动
     */
    public void startFor0() {
        currentScrollX = 0;
        startScroll();
    }
        
    /**
     * 设置滚动速度
     * @param speed 滚动速度
     */
    public void setScrollSpeed(int speed){    	
    	scrollSpeed=speed;    	
    }
    
    /**
     * 设置滚动方向
     */
    public void setScrollRightToLeft(){
    	scrollDirectionIsLeft=true;
    }
    
    /**
     * 设置滚动方向
     */
    public void setScrollLeftToRight(){
    	scrollDirectionIsLeft=false;
    }

	@Override
	public void append(CharSequence text, int start, int end) {
		// TODO Auto-generated method stub
		super.append(text, start, end);
	}
    
}
