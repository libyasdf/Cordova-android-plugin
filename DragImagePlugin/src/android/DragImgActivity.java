package org.apache.cordova.dragImage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.example.IMClientliby.R;

import java.io.File;

public class DragImgActivity extends Activity {
	private String picture_adress="";
	private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/";
	Bitmap bitmap;
	ImageView imageView;
	Boolean judge = true;
	//手机分辨率
//	DisplayMetrics dm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drag_img_activity);

//		dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm); //获取分辨率

		picture_adress=this.getIntent().getStringExtra("picture_adress");
		Log.i("picture_adress:",picture_adress.toString());

		imageView = (ImageView)findViewById(R.id.imageView);
	//内置存储卡
		File file = new File(getSDCardBaseDir() + File.separator + picture_adress.toString());
		Log.i("file exits :", String.valueOf(file.exists()));
		if(file.exists()){
			judge = true;
		} else {
			judge = false;
		}

		if(judge.equals(true)){
			//获取被缩放的图片
			bitmap = BitmapFactory.decodeFile(ALBUM_PATH+picture_adress.replace("/show", "newsdetail"));
			//设置ImageView初始化时显示的图片
			imageView.setImageBitmap(bitmap);
			imageView.setOnTouchListener(new TouchListener());
		}else {
			imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.nopic));
		}
	}
	// 判断SD卡是否被挂载
	public static boolean isSDCardMounted() {
		// return Environment.getExternalStorageState().equals("mounted");
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	// 获取SD卡的根目录
	public static String getSDCardBaseDir() {
		if (isSDCardMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}
//双击
	private final int DOUBLE_TAP_TIMEOUT = 200;
	private MotionEvent mCurrentDownEvent;
	private MotionEvent mPreviousUpEvent;
	boolean DoubleClick = true;

	private final class TouchListener implements OnTouchListener {
		
		/** 记录是拖拉照片模式还是放大缩小照片模式 */
		private int mode = 0;// 初始状态  
		/** 拖拉照片模式 */
		private static final int MODE_DRAG = 1;
		/** 放大缩小照片模式 */
		private static final int MODE_ZOOM = 2;
		
		/** 用于记录开始时候的坐标位置 */
		private PointF startPoint = new PointF();
		/** 用于记录拖拉图片移动的坐标位置 */
		private Matrix matrix = new Matrix();
		/** 用于记录图片要进行拖拉时候的坐标位置 */
		private Matrix currentMatrix = new Matrix();
	
		/** 两个手指的开始距离 */
		private float startDis;
		/** 两个手指的中间点 */
		private PointF midPoint;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/**双击时间**/
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				imageView.setScaleType(ImageView.ScaleType.MATRIX);//恢复矩阵类型
				if (mPreviousUpEvent != null && mCurrentDownEvent != null && isConsideredDoubleTap(mCurrentDownEvent, mPreviousUpEvent, event)) {
					if (DoubleClick){
						ViewPropertyAnimatorCompat animate = ViewCompat.animate(imageView);
						animate.scaleXBy(1.2f).scaleYBy(1.2f).start();
						DoubleClick = false;
//						Toast.makeText(getApplicationContext(), "双击big", Toast.LENGTH_SHORT).show();//双击Toast
					}else{
						ViewPropertyAnimatorCompat animate = ViewCompat.animate(imageView);
						animate.scaleXBy(-1.2f).scaleYBy(-1.2f).start();
						DoubleClick = true;
//						Toast.makeText(getApplicationContext(), "双击small", Toast.LENGTH_SHORT).show();//双击Toast
					}
				}
				mCurrentDownEvent = MotionEvent.obtain(event);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mPreviousUpEvent = MotionEvent.obtain(event);
			}
		/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 手指压下屏幕
			case MotionEvent.ACTION_DOWN:
				mode = MODE_DRAG;
				// 记录ImageView当前的移动位置
				currentMatrix.set(imageView.getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				break;
			// 手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				// 拖拉图片
				if (mode == MODE_DRAG) {
					float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
					float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
					// 在没有移动之前的位置上进行移动
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
				}
				// 放大缩小图片
				else if (mode == MODE_ZOOM) {
					float endDis = distance(event);// 结束距离
					if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
						float scale = endDis / startDis;// 得到缩放倍数
						String s = String.valueOf(scale);
//						Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
						matrix.set(currentMatrix);
						matrix.postScale(scale, scale,midPoint.x,midPoint.y);
					}
				}
				break;
			// 手指离开屏幕
			case MotionEvent.ACTION_UP:

				// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				/** 计算两个手指间的中间点 */
				if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
					midPoint = mid(event);
					//记录当前ImageView的缩放倍数
					currentMatrix.set(imageView.getImageMatrix());
				}
				break;
			}
			imageView.setImageMatrix(matrix);
			return true;
		}

		/**双击**/
	private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
		if (secondDown.getEventTime() - firstUp.getEventTime() > DOUBLE_TAP_TIMEOUT) {
			return false;
		}
		int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
		int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
		return deltaX * deltaX + deltaY * deltaY < 10000;
	}
		/** 计算两个手指间的距离 */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** 使用勾股定理返回两点之间的距离 */
			return (float)Math.sqrt(dx * dx + dy * dy);
		}

		/** 计算两个手指间的中间点 */
		private PointF mid(MotionEvent event) {
			float midX = (event.getX(1) + event.getX(0)) / 2;
			float midY = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(midX, midY);
		}

//		/**图片居中**/
//		protected void center() {
//			center(true,true);
//		}
//
//		private void center(boolean horizontal, boolean vertical) {
//			Matrix m = new Matrix();
//			m.set(matrix);
//			RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
//			m.mapRect(rect);
//			float height = rect.height();
//			float width = rect.width();
//			float deltaX = 0, deltaY = 0;
//			if (vertical) {
//				int screenHeight = dm.heightPixels;  //手机屏幕分辨率的高度
//				if (height < screenHeight) {
//					deltaY = (screenHeight - height)/2 - rect.top;
//				}else if (rect.top > 0) {
//					deltaY = -rect.top;
//				}else if (rect.bottom < screenHeight) {
//					deltaY = imageView.getHeight() - rect.bottom;
//				}
//			}
//
//			if (horizontal) {
//				int screenWidth = dm.widthPixels;  //手机屏幕分辨率的宽度
//				if (width < screenWidth) {
//					deltaX = (screenWidth - width)/2 - rect.left;
//				}else if (rect.left > 0) {
//					deltaX = -rect.left;
//				}else if (rect.right < screenWidth) {
//					deltaX = screenWidth - rect.right;
//				}
//			}
//			matrix.postTranslate(deltaX, deltaY);
//		}

	}

}