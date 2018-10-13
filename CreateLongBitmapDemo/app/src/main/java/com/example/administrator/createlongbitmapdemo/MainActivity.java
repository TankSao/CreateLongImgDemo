package com.example.administrator.createlongbitmapdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static ImageView img;
    private List bitmaps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
        bitmaps = new ArrayList();
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner1));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner2));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner3));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner1));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner2));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner3));
        bitmaps.add(BitmapFactory.decodeResource(getResources(),R.mipmap.banner1));
        merge(bitmaps);
    }
    private void merge(final List bitmaps) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap newBmp = (Bitmap) bitmaps.get(0);
                    for(int i = 1;i<bitmaps.size();i++) {
                        Bitmap bmp = (Bitmap) bitmaps.get(i);
                        newBmp = newBitmap(newBmp,bmp);
                    }
                    File rstFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "resultImg.jpg");
                    if (!rstFile.exists())
                        rstFile.createNewFile();
                    save(newBmp, rstFile, Bitmap.CompressFormat.JPEG, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static Bitmap newBitmap(Bitmap bmp1, Bitmap bmp2) {
        Bitmap retBmp;
        int width =bmp1.getWidth() ;
        if (bmp2.getWidth() != width) {
            //以第一张图片的宽度为标准，对第二张图片进行缩放。

            int h2 = bmp2.getHeight() * width / bmp2.getWidth();
            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            Bitmap newSizeBmp2 = resizeBitmap(bmp2, width, h2);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(newSizeBmp2, 0, bmp1.getHeight(), null);
        } else {
            //两张图片宽度相等，则直接拼接。

            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + bmp2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        }
        img.setImageBitmap(retBmp);

        return retBmp;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bmpScale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmpScale;
    }

    /**
     * 保存图片到文件File。
     *
     * @param src     源图片
     * @param file    要保存到的文件
     * @param format  格式
     * @param recycle 是否回收
     * @return true 成功 false 失败
     */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src))
            return false;

        OutputStream os;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled())
                src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}
