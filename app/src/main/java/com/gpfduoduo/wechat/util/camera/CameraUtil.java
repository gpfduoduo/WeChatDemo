package com.gpfduoduo.wechat.util.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Surface;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtil {
    //降序
    private CameraDropSizeComparator dropSizeComparator
            = new CameraDropSizeComparator();
    //升序
    private CameraAscendSizeComparator ascendSizeComparator
            = new CameraAscendSizeComparator();
    private static CameraUtil myCamPara = null;


    public static CameraUtil getInstance() {
        if (myCamPara == null) {
            myCamPara = new CameraUtil();
        }
        return myCamPara;
    }


    public int getCameraDisplayOrientation(Activity context, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = context.getWindowManager()
                              .getDefaultDisplay()
                              .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }


    /**
     * 保证预览方向正确
     */
    public void setCameraDisplayOrientation(Activity context, int cameraId, Camera camera) {
        int result = getCameraDisplayOrientation(context, cameraId, camera);
        camera.setDisplayOrientation(result);
    }


    public Bitmap setTakePictreOrientation(int id, Bitmap bitmap) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(id, info);
        bitmap = rotateImageView(id, info.orientation, bitmap);
        return bitmap;
    }


    /**
     * 把相机拍照返回照片转正
     *
     * @param angle 旋转角度
     * @return bitmap 图片
     */
    public Bitmap rotateImageView(int id, int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //加入翻转 把相机拍照返回照片转正
        if (id == 1) {
            matrix.postScale(-1, 1);
        }
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }


    /**
     * 获取所有支持的预览尺寸
     */
    public Size getPropPreviewSize(List<Size> list, int minWidth) {
        Collections.sort(list, ascendSizeComparator);

        int i = 0;
        for (Size s : list) {
            if ((s.width >= minWidth)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }


    /**
     * 获取所有支持的返回图片尺寸
     */
    public Size getPropPictureSize(List<Size> list, int minWidth) {
        Collections.sort(list, ascendSizeComparator);

        int i = 0;
        for (Size s : list) {
            if ((s.width >= minWidth)) {
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }


    public boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        }
        else {
            return false;
        }
    }


    //降序
    public class CameraDropSizeComparator implements Comparator<Size> {
        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            }
            else if (lhs.width < rhs.width) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }

    //升序
    public class CameraAscendSizeComparator implements Comparator<Size> {
        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            }
            else if (lhs.width > rhs.width) {
                return 1;
            }
            else {
                return -1;
            }
        }
    }


    /**
     * 打印支持的previewSizes
     */
    public void printSupportPreviewSize(Camera.Parameters params) {
        List<Size> previewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Size size = previewSizes.get(i);
        }
    }


    /**
     * 打印支持的pictureSizes
     */
    public void printSupportPictureSize(Camera.Parameters params) {
        List<Size> pictureSizes = params.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Size size = pictureSizes.get(i);
        }
    }


    /**
     * 打印支持的聚焦模式
     */
    public void printSupportFocusMode(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
        }
    }


    /**
     * 打开闪关灯
     */
    public void turnLightOn(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
            else {
            }
        }
    }


    /**
     * 自动模式闪光灯
     */
    public void turnLightAuto(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            // Use the screen as a flashlight (next best thing)
            return;
        }
        String flashMode = parameters.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)) {
            // Turn on the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
            else {
            }
        }
    }


    /**
     * 关闭闪光灯
     */
    public void turnLightOff(Camera mCamera) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        List<String> flashModes = parameters.getSupportedFlashModes();
        String flashMode = parameters.getFlashMode();
        // Check if camera flash exists
        if (flashModes == null) {
            return;
        }
        if (!Camera.Parameters.FLASH_MODE_OFF.equals(flashMode)) {
            // Turn off the flash
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
            else {
            }
        }
    }
}
