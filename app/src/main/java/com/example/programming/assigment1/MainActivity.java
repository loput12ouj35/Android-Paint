package com.example.programming.assigment1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends Activity {
    DrawingView dv;

    String brushShape = "Pen";
    String brushWidth = "Normal";
    String brushColor = "Blue";
    Button drButton;
    Button delButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dv = (DrawingView) findViewById(R.id.drawingView);

        drButton = (Button) findViewById(R.id.drawing);
        delButton = (Button) findViewById(R.id.delete);

        drButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dv.setDelete(false);
                delButton.setTextColor(Color.BLACK);
                final View tmp = v;
                final Button delButton = (Button) findViewById(R.id.delete);
                dv.setDelete(false);
                delButton.setTextColor(Color.BLACK);
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
                popup.getMenu().findItem(R.id.shape).setTitle("Shape: " + brushShape);
                popup.getMenu().findItem(R.id.width).setTitle("Width: " + brushWidth);
                popup.getMenu().findItem(R.id.color).setTitle("Color: " + brushColor);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.shape:
                                onClickShape(tmp);
                                return true;
                            case R.id.width:
                                onClickWidth(tmp);
                                return true;
                            case R.id.color:
                                onClickColor(tmp);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });

        delButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dv.setDelete(true);
                delButton.setTextColor(Color.RED);
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                Menu menu = popup.getMenu();
                popup.getMenuInflater().inflate(R.menu.menu_delete, menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.small:
                                dv.setDeleteSize(9);
                                return true;
                            case R.id.medium:
                                dv.setDeleteSize(18);
                                return true;
                            case R.id.large:
                                dv.setDeleteSize(36);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load:
                load();
                break;
            case R.id.save:
                save();
                break;
        }

    }

    public void onClickDrawing(View v) {
        dv.setDelete(false);
        delButton.setTextColor(Color.BLACK);
    }

    public void onClickShape(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_shape, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.line:
                        dv.setDrawingMode(DrawingView.DrawingMode.LINE);
                        brushShape = "Line";
                        return true;
                    case R.id.rec:
                        dv.setDrawingMode(DrawingView.DrawingMode.REC);
                        brushShape = "Rectangle";
                        return true;
                    case R.id.ellipse:
                        dv.setDrawingMode(DrawingView.DrawingMode.ELLIPSE);
                        brushShape = "Ellipse";
                        return true;
                    case R.id.pen:
                        dv.setDrawingMode(DrawingView.DrawingMode.PEN);
                        brushShape = "Pen";
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public void onClickWidth(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_width, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.thin:
                        dv.setWidth(6);
                        brushWidth = "Thin";
                        return true;
                    case R.id.normal:
                        dv.setWidth(12);
                        brushWidth = "Normal";
                        return true;
                    case R.id.thick:
                        dv.setWidth(18);
                        brushWidth = "Thick";
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public void onClickColor(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        Menu menu = popup.getMenu();
        popup.getMenuInflater().inflate(R.menu.menu_color, menu);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.red:
                        dv.setColor(Color.RED);
                        brushColor = "Red";
                        return true;
                    case R.id.green:
                        dv.setColor(Color.GREEN);
                        brushColor = "Green";
                        return true;
                    case R.id.blue:
                        dv.setColor(Color.BLUE);
                        brushColor = "Blue";
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }
    public void onClickDelete(View v) {
        dv.setDelete(true);
        delButton.setTextColor(Color.RED);
    }


    final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.txt";
    public void save() {
        Log.d("jSave", dv.getDataString());
        String jsonPaths = dv.getDataString();
        File file = new File(filePath);

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            fos.write(jsonPaths.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Toast toast = Toast.makeText(this, "Save : " + filePath, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    public void load() {
        StringBuffer fileData = new StringBuffer();
        char[] buf = new char[1024];
        int numRead = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            while((numRead = reader.read(buf)) != -1){
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("jLoad", fileData.toString());
        dv.setDateString(fileData.toString());

        final Toast toast = Toast.makeText(this, "Load : " + filePath, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
    }
}

