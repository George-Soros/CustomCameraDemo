package com.cameratest.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by 17041427 on 2017/8/14.
 *
 * SurfaceHolder.Callback的surfaceCreated方法，打开相机，设置相机参数，开启预览，拍照
 */
public class Camera1SurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private static final String TAG = "CameraSurfaceView";

    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;

    private int mScreenWidth;
    private int mScreenHeight;
    private Activity mActivity;
    public String FILE_PATH;//图片路径
    private String mFlag = "1";

    public Camera1SurfaceView(Context context) {
        this(context, null);
    }

    public Camera1SurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Camera1SurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getScreenMetrix(context);

        initView();
    }


    //拿到手机屏幕大小
    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;

    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);//hodler回调
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            //开启相机
            mCamera = Camera.open();
            try {
                //摄像头画面显示在Surface上
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();//停止预览
        mCamera.release();//释放相机资源
        mCamera = null;
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success=" + success);
            System.out.println(success);
        }
    }

    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG, "setCameraParams  width=" + width + "  height=" + height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width, picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height * (h / w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }

        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     * h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }

    public void takePicture(Activity activity, String flag) {
        mActivity = activity;
        mFlag = flag;
        //设置参数,并拍照
        setCameraParams(mCamera, mScreenWidth, mScreenHeight);
        // 当调用camera.takePiture方法后，camera关闭了预览，这时需要调用startPreview()来重新开启预览
        mCamera.takePicture(null, null, pictureCallback);
    }


    // 拍照瞬间调用
    private Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.i(TAG, "shutter");
        }
    };

    // 获得没有压缩过的图片数据
    private Camera.PictureCallback raw = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {
            Log.i(TAG, "raw");
        }
    };

    //创建jpeg图片回调数据对象
    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        private Bitmap bitmap;

        @Override
        public void onPictureTaken(byte[] data, Camera Camera) {

            int viewWidth = 0;
            int viewHeight = 0;
            int rectLeft = 0;
            int rectTop = 0;
            int rectRight = 0;
            int rectBottom = 0;

//            if ("1".equals(mFlag)) {
//                OneCameraTopRectView topView = new OneCameraTopRectView(mContext, null);
//                viewWidth = topView.getViewWidth();
//                viewHeight = topView.getViewHeight();
//                rectLeft = topView.getRectLeft();
//                rectTop = topView.getRectTop();
//                rectRight = topView.getRectRight();
//                rectBottom = topView.getRectBottom();
//                topView.draw(new Canvas());
//            } else if ("2".equals(mFlag)) {
//                TwoCameraTopRectView topView = new TwoCameraTopRectView(mContext, null);
//                viewWidth = topView.getViewWidth();
//                viewHeight = topView.getViewHeight();
//                rectLeft = topView.getRectLeft();
//                rectTop = topView.getRectTop();
//                rectRight = topView.getRectRight();
//                rectBottom = topView.getRectBottom();
//                topView.draw(new Canvas());
//            } else if ("3".equals(mFlag)) {
//                ThreeCameraTopRectView topView = new ThreeCameraTopRectView(mContext, null);
//                viewWidth = topView.getViewWidth();
//                viewHeight = topView.getViewHeight();
//                rectLeft = topView.getRectLeft();
//                rectTop = topView.getRectTop();
//                rectRight = topView.getRectRight();
//                rectBottom = topView.getRectBottom();
//                topView.draw(new Canvas());
//            }


            BufferedOutputStream bos = null;
            Bitmap bm = null;
            if (data != null) {
            }

            try {
                // 获得图片
                bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    FILE_PATH = "/sdcard/dyk" + System.currentTimeMillis() + ".JPEG";//照片保存路径

//                    //图片存储前旋转
                    Matrix matrix = new Matrix();
                    int height = bm.getHeight();
                    int width = bm.getWidth();
                    if ("3".equals(mFlag))
                    {
                        matrix.setRotate(90);
                    }
                    //旋转后的图片
                    bitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);

                    File file = new File(FILE_PATH);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    bos = new BufferedOutputStream(new FileOutputStream(file));

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
//                            data.length);
                    Bitmap sizeBitmap = Bitmap.createScaledBitmap(bitmap,
                            viewWidth, viewHeight, true);
                    bm = Bitmap.createBitmap(sizeBitmap, rectLeft,
                            rectTop,
                            rectRight - rectLeft,
                            rectBottom - rectTop);// 截取

                    bm.compress(Bitmap.CompressFormat.JPEG, 60, bos);//将图片压缩到流中
                } else {
                    Toast.makeText(mContext, "没有检测到内存卡", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.flush();//输出
                    bos.close();//关闭
                    bm.recycle();// 回收bitmap空间
                    mCamera.stopPreview();// 关闭预览
//                    Intent intent = new Intent(mActivity, ShowPicActivity.class);
//                    intent.putExtra("FILE_PATH", FILE_PATH);
//                    intent.putExtra("flag", mFlag);
//                    mActivity.startActivity(intent);
//                    mActivity.finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
