package com.example.programming.assigment1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DrawingView extends View {
    public enum DrawingMode {PEN, LINE, REC, ELLIPSE}

    class Point {
        float x, y;

        Point(float x, float y){
            this.x = x;
            this.y = y;
        }
    }

    class SavedPoint{
        ArrayList<Point> points;
        DrawingMode mode;
        int color;
        float size;

        SavedPoint(ArrayList<Point> input, DrawingMode mode, int color, float size) {
            this.points = input;
            this.mode = mode;
            this.color = color;
            this.size = size;
        }
    }

    private int deleteSize = 18;

    private Paint mPaint;
    private Paint mImagePaint;
    private Bitmap mDrawing;
    private Bitmap mDrawing2;
    private Canvas mCanvas;     //Draw paths on mCanvas and mDrawing with mPaint
    private Canvas mCanvas2;
    private Path mPath;     //Draw a line along a path
    private ArrayList<SavedPath> paths;      //To remember the paths of the drawn lines
    private ArrayList<SavedPoint> savedPoints;
    private DrawingMode drawingMode = DrawingMode.PEN;
    private boolean delete = false;

    public DrawingView(Context c) {
        this(c, null, 0);
    }

    public DrawingView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public DrawingView(Context c, AttributeSet attrs, int defstyle) {
        super(c, attrs, defstyle);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(12);
        mImagePaint = new Paint(Paint.DITHER_FLAG);
        mPath = new Path();
        paths = new ArrayList<>();
        savedPoints = new ArrayList<>();

        mDrawing = Bitmap.createBitmap(1920, 1920, Bitmap.Config.ARGB_8888);
        mDrawing2 = Bitmap.createBitmap(1920, 1920, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mDrawing);
        mCanvas2 = new Canvas(mDrawing2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mDrawing, 0, 0, mImagePaint); //Show the lines that you drew.
        canvas.drawBitmap(mDrawing2, 0, 0, mImagePaint); //Show the shapes when you drawing.
    }

    private float mX, mY;       //initial or previous X, and Y
    private ArrayList<Point> mPoints = new ArrayList<>();

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        mPoints.add(new Point(x, y));
    }
    private void touch_move(float x, float y) {
        mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
        mX = x;
        mY = y;
        mCanvas.drawPath(mPath, mPaint);     //Show the line that you are drawing currently.
        mPoints.add(new Point(x, y));
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        paths.add(new SavedPath(new Path(mPath), new Paint(mPaint)));     //Add path into the arraylist
        mCanvas.drawPath(mPath, mPaint);        //Commit
        mPath.reset();

        savedPoints.add(new SavedPoint(mPoints, drawingMode, mPaint.getColor(), mPaint.getStrokeWidth()));
        mPoints = new ArrayList<>();
    }
    private void shape_touch_start(float x, float y) {
        mPath.reset();
        mX = x;
        mY = y;
        mPoints.add(new Point(x, y));
    }
    private void shape_touch_move(float x, float y) {
        mDrawing2.eraseColor(Color.TRANSPARENT);
        switch (drawingMode) {
            case ELLIPSE:
                mPath.addOval(mX, mY, x , y, Path.Direction.CW);
                break;
            case REC:
                mPath.addRect(mX, mY, x, y, Path.Direction.CCW);
                break;
            case LINE:
                mPath.moveTo(mX, mY);
                mPath.lineTo(x, y);
                break;
        }
        mCanvas2.drawPath(mPath, mPaint);
        mPath.reset();
    }
    private void shape_touch_up(float x, float y) {
        mDrawing2.eraseColor(Color.TRANSPARENT);
        switch (drawingMode) {
            case ELLIPSE:
                mPath.addOval(mX, mY, x , y, Path.Direction.CW);
                break;
            case REC:
                mPath.addRect(mX, mY, x, y, Path.Direction.CCW);
                break;
            case LINE:
                mPath.moveTo(mX, mY);
                mPath.lineTo(x, y);
                break;
        }

        paths.add(new SavedPath(new Path(mPath), new Paint(mPaint)));     //Add path into the arraylist
        mCanvas.drawPath(mPath, mPaint);        //Commit
        mPath.reset();

        mPoints.add(new Point(x, y));
        savedPoints.add(new SavedPoint(mPoints, drawingMode, mPaint.getColor(), mPaint.getStrokeWidth()));
        mPoints = new ArrayList<>();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(!delete) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch (drawingMode) {
                        case PEN:
                            touch_start(x, y);
                            break;
                        case LINE:
                        case ELLIPSE:
                        case REC:
                            shape_touch_start(x, y);
                            break;
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    switch (drawingMode) {
                        case PEN:
                            touch_move(x, y);
                            break;
                        case LINE:
                        case ELLIPSE:
                        case REC:
                            shape_touch_move(x, y);
                            break;
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    switch (drawingMode) {
                        case PEN:
                            touch_up();
                            break;
                        case LINE:
                        case ELLIPSE:
                        case REC:
                            shape_touch_up(x, y);
                            break;

                    }
                    invalidate();
                    break;
            }
        } else {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    delete(x, y);
                    mCanvas2.drawRect(x - deleteSize, y - deleteSize, x + deleteSize, y + deleteSize,mPaint);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    delete(x, y);
                    mCanvas2.drawRect(x - deleteSize, y - deleteSize, x + deleteSize, y + deleteSize,mPaint);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDrawing2.eraseColor(Color.TRANSPARENT);
                            invalidate();
                        }
                    }, 500);
                    break;
            }
        }
        return true;
    }

    public void delete(float x, float y) {
        Bitmap tmpBitmap = mDrawing2;
        Canvas tmpCanvas = new Canvas(tmpBitmap);
        tmpBitmap.eraseColor(Color.TRANSPARENT);
        boolean[] result = new boolean[paths.size()];

        for(int i = 0; i < paths.size(); ++i){
            tmpCanvas.drawPath(paths.get(i).path, paths.get(i).paint);

            boolean detected = false;

            for(int j = Math.max(0, (int) x - deleteSize); j < Math.min(tmpBitmap.getWidth(), (int) x + deleteSize); ++j){
                for(int k = Math.max(0, (int) y - deleteSize); k < Math.min(tmpBitmap.getHeight(), (int) y + deleteSize); ++k){
                    int color = tmpBitmap.getPixel(j, k);
                    detected = detected || color != Color.TRANSPARENT;
                }
            }
            result[i] = detected;
            tmpBitmap.eraseColor(Color.TRANSPARENT);
        }

        for(int i = paths.size() - 1; i >= 0; --i){
            if(result[i]) {
                paths.remove(i);
                savedPoints.remove(i);
            }
        }

        reDraw();
    }

    public void reDraw() {
        mDrawing.eraseColor(Color.TRANSPARENT);
        for(int i=0; i < paths.size(); ++i){
            mCanvas.drawPath(paths.get(i).path, paths.get(i).paint);     //Draw all of the previous lines again.
            invalidate();
        }
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setWidth(int size) {
        mPaint.setStrokeWidth(size);
    }

    public void setDeleteSize(int size) {
        deleteSize = size;
    }

    public void setDrawingMode(DrawingMode mode) {
        drawingMode = mode;
    }

    public void setDelete(boolean input) {
        delete = input;
    }

    public String getDataString() {
        Gson gson = new Gson();
        return gson.toJson(savedPoints);
    }

    public void setDateString(String input) {
        Type type = new TypeToken<ArrayList<SavedPoint>>(){}.getType();
        Gson gson = new Gson();
        savedPoints = gson.fromJson(input, type);
        drawWithPoints();
    }

    public void drawWithPoints() {
        Path mPath = new Path();
        paths.clear();
        mDrawing.eraseColor(Color.TRANSPARENT);
        for(int i=0; i < savedPoints.size(); ++i){
            SavedPoint tmp = savedPoints.get(i);
            float x = tmp.points.get(0).x;
            float y = tmp.points.get(0).y;
            float dx = tmp.points.get(1).x;
            float dy = tmp.points.get(1).y;

            switch (tmp.mode){
                case ELLIPSE:
                    mPath.addOval(x, y, dx, dy, Path.Direction.CCW);
                    break;
                case REC:
                    mPath.addRect(x, y, dx, dy, Path.Direction.CCW);
                    break;
                case LINE:
                    mPath.moveTo(x, y);
                    mPath.lineTo(dx, dy);
                    break;
                case PEN:
                    mPath.moveTo(x, y);
                    for(int j=0; j < tmp.points.size() - 1; ++j){
                        x = tmp.points.get(j).x;
                        y = tmp.points.get(j).y;
                        dx = tmp.points.get(j + 1).x;
                        dy = tmp.points.get(j + 1).y;
                        mPath.quadTo(x, y, (x + dx) / 2, (y + dy) / 2);
                    }
                    break;
            }
            Paint tmpPaint = new Paint();
            tmpPaint.setColor(tmp.color);
            tmpPaint.setStrokeWidth(tmp.size);
            tmpPaint.setAntiAlias(true);
            tmpPaint.setDither(true);
            tmpPaint.setStyle(Paint.Style.STROKE);
            tmpPaint.setStrokeJoin(Paint.Join.ROUND);
            tmpPaint.setStrokeCap(Paint.Cap.ROUND);
            paths.add(new SavedPath(new Path(mPath), tmpPaint));
            mCanvas.drawPath(mPath, tmpPaint);
            mPath.reset();
        }
        invalidate();
    }
}