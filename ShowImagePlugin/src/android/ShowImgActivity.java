package org.apache.cordova.showImage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.IMClientliby.R;

@SuppressLint("HandlerLeak")  
public class ShowImgActivity extends Activity implements GestureDetector.OnGestureListener,View.OnTouchListener {//继承Activity方法，实践onCreate抽象方法
    private String picture_adress="";
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/";
            //    //初始状态
            //    private int mode = 0;
            //    //拖拉照片模式
            //    private static final int MODE_DRAG = 1;
            //    //放大缩小照片模式
            //    private static final int MODE_ZOOM = 2;
            //    //用于记录开始时候的坐标位置
            //    private PointF startPoint = new PointF();
            //    //用于记录拖拉图片移动的坐标位置
            //    private Matrix matrix = new Matrix();
            //    //用于记录图片要进行拖拉时候的坐标位置
            //    private Matrix currentMatrix = new Matrix();
            //    //两手指开始的距离
            //    private float startDis;
            //    //两手指的中间点
            //    private PointF midPoint;
    //定义手势检测器实例
    GestureDetector detector;
    ImageView imageView;
    //初始的图片资源
    Bitmap bitmap;
    //定义图片的高、宽
    int width, height;
    //记录当前的缩放比
    float currentScale = 1;
    //控制图片的缩放Matrix对象
    Matrix matrix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//调用父类方法
        setContentView(R.layout.show_img_activity);//加载布局文件

        //获取回退按键
//        Button previous = (Button) findViewById(R.id.previous);
        //监听回退按键
//        previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View source) {
//                Intent intent = new Intent(ShowImgActivity.this,ShowDetailImg.class);
//                startActivity(intent);
////                finish();
//            }
//        });
        //
        picture_adress=this.getIntent().getStringExtra("picture_adress");
        Log.i("picture_adress:",picture_adress.toString());
        //创建手势监测器
        detector = new GestureDetector(this, this);
        imageView = (ImageView)findViewById(R.id.show);
        matrix = new Matrix();
        //获取被缩放的图片
//        bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.nopic);
        bitmap = BitmapFactory.decodeFile(ALBUM_PATH+picture_adress.replace("/show", "newsdetail"));
        //获取位图的宽、高
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        //设置ImageView初始化时显示的图片
        if(picture_adress.equals("")){
            imageView.setImageResource(R.drawable.nopic);
        }else{
            imageView.setImageBitmap(bitmap);
//            imageView.setOnTouchListener(new ShowImgActivity());//sparking OnTouch
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me){
        //将该activity上的碰触事件交由GestureDetector处理
        return detector.onTouchEvent(me);
    }
//抛掷（onFling）： 手指在触摸屏上迅速移动，并松开的动作。
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY){
        velocityX = velocityX > 4000 ? 4000 : velocityX;
        velocityX = velocityX < -4000 ? -4000 : velocityX;
        //根据手势的速度来计算缩放比，如果velocityx>0,则放大图片，否则缩小
        currentScale+= currentScale * velocityX / 4000.0f;
        //保证currentScale不会等于0
        currentScale = currentScale > 0.01 ? currentScale: 0.01f;
        //重置Matrix
        matrix.reset();
        //缩放Matrix
        matrix.setScale(currentScale,currentScale,160,200);
        BitmapDrawable tmp = (BitmapDrawable)imageView.getDrawable();
        //如果图片还未回收，则强制收回图片,会抛出异常
//        if (!tmp.getBitmap().isRecycled()){
//            tmp.getBitmap().recycle();
//        }
        //根据原始位图和Matrix创建图片
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
        //显示新的位图
        imageView.setImageBitmap(bitmap2);
        return true;
    }
//按下（onDown）： 刚刚手指接触到触摸屏的那一刹那，就是触的那一下。
    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }
//按住（onShowPress）： 手指按在触摸屏上，它的时间范围在按下起效，在长按之前。
    @Override
    public void onShowPress(MotionEvent event) {

    }
//抬起（onSingleTapUp）：手指离开触摸屏的那一刹那。
    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return false;
    }
//滚动（onScroll）： 手指在触摸屏上滑动。
    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        return false;
    }
//长按（onLongPress）： 手指按在持续一段时间，并且没有松开。
    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return detector.onTouchEvent(event);
    }
}

/*任何手势动作都会先执行一次按下（onDown）动作。
长按（onLongPress）动作前一定会执行一次按住（onShowPress）动作。
按住（onShowPress）动作和按下（onDown）动作之后都会执行一次抬起（onSingleTapUp）动作。
长按（onLongPress）、滚动（onScroll）和抛掷（onFling）动作之后都不会执行抬起（onSingleTapUp）动作*/