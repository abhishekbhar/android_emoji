package com.abhibhr.android_emoji.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {
    public static final String LOG_TAG = Emojifier.class.getSimpleName();

    public static void detectFaces(Context context, Bitmap image){
        FaceDetector detector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame imageFrame = new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = detector.detect(imageFrame);

        Log.d(LOG_TAG, "detectFaces: number of faces = "+ faces.size());
        if (faces.size() == 0) {
            Toast.makeText(context, "No Image Found",Toast.LENGTH_SHORT).show();
        }
        detector.release();
    }
}
