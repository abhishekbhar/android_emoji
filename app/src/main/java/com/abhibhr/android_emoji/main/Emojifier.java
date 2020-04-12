package com.abhibhr.android_emoji.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.SparseArray;
import android.widget.Toast;

import com.abhibhr.android_emoji.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import timber.log.Timber;

public class Emojifier {
    private static final float EMOJI_SCALE_FACTOR = .9f;
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOld = .5;

    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap image){
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        Canvas canvas = new Canvas(image);
//        canvas.rotate(90);

        FaceDetector detector = new FaceDetector.Builder(context)
                .setProminentFaceOnly(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Bitmap resultBitmap = image;
        Frame imageFrame = new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = detector.detect(imageFrame);

        Timber.d("detectFaces: number of faces = "+ faces.size());
        if (faces.size() == 0) {
            Toast.makeText(context, "No Image Found",Toast.LENGTH_SHORT).show();
        } else {
            for(int i = 0; i <  faces.size(); i++) {

                Bitmap emojiBitmap;
                Face face = faces.valueAt(i);
                switch (whichEmoji(face)){
                    case SMILE:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.smile);
                        break;
                    case FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.frown);
                        break;

                    case LEFT_WINK:
                        emojiBitmap =BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwink);
                        break;
                    case RIGHT_WINK:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwink);
                        break;
                    case LEFT_WINK_FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwinkfrown);
                        break;
                    case RIGHT_WINK_FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwinkfrown);
                        break;
                    case CLOSED_EYE_SMILE:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_smile);
                        break;
                    case CLOSED_EYE_FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_frown);
                    default:
                        emojiBitmap = null;
                        Toast.makeText(context, "No emoji found", Toast.LENGTH_SHORT).show();
                }

                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);
            }
        }
        detector.release();
        return resultBitmap;
    }

    private static Emoji whichEmoji(Face face){
        Timber.d("getClassifications: smilingProb = " + face.getIsSmilingProbability());
        Timber.d("getClassifications: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Timber.d("getClassifications: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;

        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() <EYE_OPEN_PROB_THRESHOld;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOld;

        Emoji emoji;

        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) emoji = Emoji.LEFT_WINK;
            else if (rightEyeClosed && !leftEyeClosed) emoji = Emoji.RIGHT_WINK;
            else if (leftEyeClosed) emoji = Emoji.CLOSED_EYE_SMILE;
            else emoji = Emoji.SMILE;
        } else {
            if (leftEyeClosed && !rightEyeClosed) emoji = Emoji.LEFT_WINK_FROWN;
            else if (rightEyeClosed && !leftEyeClosed) emoji = Emoji.RIGHT_WINK_FROWN;
            else if (leftEyeClosed) emoji = Emoji.CLOSED_EYE_FROWN;
            else emoji = Emoji.FROWN;
        }

        Timber.d("whichEmoji: " + emoji.name());
        return emoji;
    }


    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face){
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(),
                backgroundBitmap.getConfig());

        float scaleFactor = EMOJI_SCALE_FACTOR;
        int newEmojiWidth = (int)(face.getWidth() * scaleFactor);
        int newEmojiHeight = (int)(emojiBitmap.getHeight() * newEmojiWidth/
                emojiBitmap.getWidth() * scaleFactor);

        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        float emojiPositionX =
                (face.getPosition().x +face.getWidth() /2 ) - emojiBitmap.getWidth() /2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() /2 ) - emojiBitmap.getHeight() /3;

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0,0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }

    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }

}
