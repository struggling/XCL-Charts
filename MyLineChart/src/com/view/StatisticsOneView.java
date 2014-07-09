package com.view;

import java.util.HashMap;
import java.util.Map.Entry;






import com.data.Common;
import com.data.MyData;
import com.example.mylinechart.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
/**
 * @author Seven
 */
public class StatisticsOneView extends View {
	private float xPoint = 0;// 原点X坐标
    private float yPoint = 0;// 原点Y坐标
    private float xLengh = 240;// X轴长度
    private float yLengh = 320;// Y轴长度
    private float xScale = 5;// X轴一个刻度长度
    private int widthBorder = 5;// 内边缘宽度，为了统计图不靠在屏幕的边缘上，向边缘缩进距离。最好大于30。
    private String[] xLableArray;// X轴标签
    private int[] yLableArray;// Y轴标签,用于计算
    private String levelName[];// 污染级别
    private Bitmap bitmap[] = null;
    private int count = 1;
    private boolean mp=false;
    
    private float[] begins;//Y轴刻度的y坐标
    private float each;//均分后刻度之间的长度
    public StatisticsOneView(Context context) {
        super(context);
        init();
    }

    private void init() {
    	if(Common.screenWidth<540)
    		widthBorder = 5;
        
        getResources().getIntArray(R.array.aqi_choice_color);
        levelName = getResources().getStringArray(R.array.aqi_level);
        getResources().getStringArray(R.array.aqi_data_view);
        bitmap = new Bitmap[levelName.length];
        for (int i = 0; i < levelName.length; i++) {
            bitmap[i] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.l_1 + i);
        }
    }

    /**
     * 实例化值
     *
     * @param screenWidth  图表宽度
     * @param ScreenHeight 图表高度
     * @param xLable       X轴标签
     * @param yLable       Y轴标签
     * @param dataArray    统计数据
     */
    public void initValue(int Width, int Height, boolean mp) {
        xPoint = widthBorder;
        yPoint = Height - 20;
        xLengh = Width - widthBorder * 2 ;
        yLengh = Height ;
        xScale = getScale(Common.xScaleArray.length - 1, xLengh);
        xLableArray = Common.xScaleArray;
        yLableArray = Common.yScaleArray;
        this.mp=mp;

        if (xLableArray.length <= 10) {
            count = 2;
        } else {
            count = 4;
        }

        help2getPoint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置画笔
        Paint paint = new Paint();
        Paint linepaint = new Paint();
        paint.setStyle(Paint.Style.FILL);// 设置画笔样式
        paint.setAntiAlias(true);// 去锯齿
        paint.setColor(Color.rgb(25, 156, 240));// 设置颜色
        if(mp)
        	paint.setTextSize(Integer.parseInt(getResources().getString(R.string.textsize)));// 设置字体
        else 
        	paint.setTextSize(Integer.parseInt(getResources().getString(R.string.textsizesmall)));
        paint.setStrokeWidth(1);

        linepaint.setStyle(Paint.Style.FILL);// 设置画笔样式
        linepaint.setAntiAlias(true);// 去锯齿
        linepaint.setColor(Common.xScaleColor);// 设置颜色
        linepaint.setTextSize(16);// 设置字体
        linepaint.setStrokeWidth(3);
        // 画X轴轴线
        canvas.drawLine(xPoint, yPoint, xPoint + xLengh, yPoint, linepaint);

//        for (int i = 1; yLableArray != null && i < yLableArray.length-2; i++) {
//            int j = i;
//            // 画Y轴刻度
//            canvas.drawLine(xPoint, yPoint - yScale * i, xPoint +xLengh, yPoint
//                    - yScale * i, mypaint);
//        }
        for (int i = 0; xLableArray != null && i < xLableArray.length; i++) {
            // 画X轴刻度
        	if(i%count == 0 && i != 0){
        		canvas.drawLine(xPoint+xScale*i, yPoint, xPoint+xScale*i, yPoint - 8, linepaint);
        		canvas.drawText(xLableArray[i]+"", xPoint+xScale*i-5, yPoint+20, linepaint);
        	}
        	else 
        		canvas.drawLine(xPoint+xScale*i, yPoint, xPoint+xScale*i, yPoint - 4, linepaint);
            
        }
        // 画折线
        drawCurve(canvas);
    }

    private void drawCurve(Canvas canvas) {
    	for(MyData data:Common.DataSeries){
    		if(data.getData().length > 0)
    	    	drawLine(canvas, data.getData(), data.getColor());
    	}
    }

    /**
     * 画AQI折线
     *
     * @param @param canvas
     * @param @param i
     * @return void
     * @Title: drawCurve
     * @Description: TODO
     */
    private void drawLine(Canvas canvas, int[] array, int color) {
    	int radius=3;
    	int lineWidth=2;
    	
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setTextSize(Integer.parseInt(getResources().getString(R.string.textsize)));// 设置字体
        for (int i = 0; i < array.length; i++) {
            if (array[i] > -1) {
                float ydata = getYDataPoint(array[i]);
                if (ydata >= 0) {
                    if(mp){
                    	paint.setStrokeWidth(1);
                    	if(i < array.length - 1)
                    		canvas.drawText(array[i]+"", xPoint + xScale * i, ydata-5, paint);
                    	else 
                    		canvas.drawText(array[i]+"", xPoint - 30 + xScale * i, ydata-5, paint);
                    	paint.setStrokeWidth(3);
                    	if(Common.screenWidth<540)
                    		radius=5;
                    	else 
                    		radius=8;
                    	lineWidth=3;
                    }
                    paint.setStrokeWidth(lineWidth);
                	canvas.drawCircle(xPoint + xScale * i, ydata, radius, paint);
                    if (i < array.length - 1) {
                        if (array[i + 1] > -1) {
                            canvas.drawLine(
                                    xPoint + xScale * i,
                                    ydata,
                                    xPoint + xScale * (i + 1),
                                    getYDataPoint(array[i + 1]),
                                    paint);
                        }
                    } else {
                        canvas.drawLine(xPoint + xScale * i, ydata, xPoint
                                + xScale * i, ydata, paint);
                    }
                }
            }
        }
    }

    
    
    /**
     * 得到每一等分的长度
     *
     * @param num    所要分成的等份
     * @param length 要分割的总长度
     * @return
     */
    private float getScale(int num, float length) {
        if (num > 0 && length > 0) {
            length -= 10;// 为了美观，缩进
            length = length - (length % num);
            return length / num;
        } else {
            return 0;
        }

    }

    /**
     * 均分Y轴
     */
    private void help2getPoint(){
    	begins = new float[yLableArray.length];
    	each = yLengh / (yLableArray.length-1);
    	for(int i=0;i<yLableArray.length-1;i++){
    		if(i==0){
    			begins[i] = yPoint;
    		}
    		else{
    			begins[i] = begins[i-1] - each;
    		}
    	}
    }
    
    /**
     * 得到点的Y坐标
     * @param data 输入值
     * @return 对应Y坐标
     */
    private float getYDataPoint(int data){
    	for(int i=0;i<yLableArray.length;i++){
    		if(data < yLableArray[i]){
    			float f = begins[i] + each*(yLableArray[i] - data)/(yLableArray[i] - yLableArray[i-1]);
    			return f;
    		}
    	}
    	return 0;
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension((int)xLengh, (int)Common.screenHeight);  
	}
}