package com.example.link_flutter_java;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobiiot.androidqapi.api.CsPrinter;
import com.mobiiot.androidqapi.api.MobiiotAPI;
import com.mobiiot.androidqapi.api.Utils.BitmapUtils;
import com.mobiiot.androidqapi.api.Utils.PrinterServiceUtil;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "my_java_linker";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if(call.method.equals("myJavaFunc")){
                               int returnedValue=  mp3Print(call.argument("data"));
                               if(returnedValue<0){
                                   result.success("printing done good");
                               }else{
                                   result.error("unavailable","somthing went wrong",null);
                               }
                            }else{
                                result.notImplemented();
                            }


                        }
                );
    }


    @Override
    protected void onResume() {
        super.onResume();

        //initialize mp3 printer
        new MobiiotAPI(this);
    }



        private int mp3Print(byte[] data) {

        int result = 0;

        System.out.println("before csprinter");

        CsPrinter.printText("text test ");

        System.out.println("after csprinter");


        try {
            if (data != null) {

                Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                int mwidth = mBitmap.getWidth();

                int deffInWidth = 384 - mwidth;


                printBitmap(
                        getResizedBitmap(mBitmap, mBitmap.getWidth() + deffInWidth, mBitmap.getHeight() + deffInWidth),
                        Bitmap.Config.ARGB_8888
                );

                result = -1;
            }

        }catch (Exception ex){
            System.out.println("something wrong on mobiiot");
            result = 0;

        }

        return result;

    }



    public static Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
        Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
        Canvas canvas          = new Canvas(convertedBitmap);
        Paint paint           = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return convertedBitmap;
    }

    public void printBitmap(Bitmap bitmap,Bitmap.Config config){
        Bitmap bit=convert(bitmap,config);
//        CsPrinter.printText("-----------------------------");
//        CsPrinter.printText("width   = "+bit.getWidth());
//        CsPrinter.printText("width/8 = "+(float)bit.getWidth()/8);
//        CsPrinter.printText("bit     = "+bit.getConfig());
        CsPrinter.printBitmap(bit);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }





}
