package com.example.esp32_cam_mjpeg_monitor;

import static android.widget.SeekBar.*;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener
{

    private static final String TAG = "MainActivity::";

    private HandlerThread stream_thread,flash_thread,rssi_thread;
    private Handler stream_handler,flash_handler,rssi_handler;
    private Button flash_button;
    private Button up_button;
    private Button down_button;
    private Button right_button;
    private Button left_button;
    private SeekBar servo_button;
    private ImageView monitor;
    private TextView rssi_text;
    private TextView message_txt;
    private EditText ip_text;

    private final int ID_CONNECT = 200;
    private final int ID_FLASH = 201;
    private final int ID_RSSI = 202;
    private final int ID_UP = 203;
    private final int ID_DOWN = 204;
    private final int ID_RIGHT = 205;
    private final int ID_LEFT = 206;
    private final int ID_SERVO = 207;
    private final int ID_STOP = 208;
    int seek_value = 0;
    String data = "";
    private boolean flash_on_off = false;
int x=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.flash).setOnClickListener(this);
        findViewById(R.id.up_btn).setOnClickListener(this);
        findViewById(R.id.down_btn).setOnClickListener(this);
        findViewById(R.id.right_btn).setOnClickListener(this);
        findViewById(R.id.left_btn).setOnClickListener(this);
        findViewById(R.id.seekBar).setOnClickListener(this);
        flash_button = findViewById(R.id.flash);
        up_button = findViewById(R.id.up_btn);
        down_button = findViewById(R.id.down_btn);
        right_button = findViewById(R.id.right_btn);
        left_button = findViewById(R.id.left_btn);
        servo_button = findViewById(R.id.seekBar);
        message_txt = findViewById(R.id.text_message);
        message_txt.invalidate();
        t.start();
        servo_button.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                                                    @Override
                                                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                                                  boolean fromUser) {
                                                        flash_handler.sendEmptyMessage(ID_SERVO);
                                                        // TODO Auto-generated method stub
                                                        seek_value = progress;
                                                    }

        public void onStartTrackingTouch(SeekBar servo_button) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStopTrackingTouch(SeekBar servo_button) {
            // TODO Auto-generated method stub
        } });
        up_button.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    flash_handler.sendEmptyMessage(ID_UP);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    flash_handler.sendEmptyMessage(ID_STOP);
                // TODO Auto-generated method stub
                return false;
            }
        });
        down_button.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    flash_handler.sendEmptyMessage(ID_DOWN);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    flash_handler.sendEmptyMessage(ID_STOP);
                // TODO Auto-generated method stub
                return false;
            }
        });
        right_button.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    flash_handler.sendEmptyMessage(ID_RIGHT);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    flash_handler.sendEmptyMessage(ID_STOP);
                // TODO Auto-generated method stub
                return false;
            }
        });
        left_button.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    flash_handler.sendEmptyMessage(ID_LEFT);
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    flash_handler.sendEmptyMessage(ID_STOP);
                // TODO Auto-generated method stub
                return false;
            }
        });

        monitor = findViewById(R.id.monitor);
        ip_text = findViewById(R.id.ip);
        ip_text.setText("192.168.0.109");


        stream_thread = new HandlerThread("http");
        stream_thread.start();
        stream_handler = new HttpHandler(stream_thread.getLooper());

        flash_thread = new HandlerThread("http");
        flash_thread.start();
        flash_handler = new HttpHandler(flash_thread.getLooper());

        rssi_thread = new HandlerThread("http");
        rssi_thread.start();
        rssi_handler = new HttpHandler(rssi_thread.getLooper());
    }


    Thread t=new Thread(){
        @Override
        public void run(){

            while(!isInterrupted()){

                try {
                    Thread.sleep(10);  //10ms
                    URL url = new URL("http://" + ip_text.getText() + ":82/message");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        data =  line;
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            Log.e("detectie:",data);
                            message_txt.setText(data);
                        }
                    });
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.connect:
                stream_handler.sendEmptyMessage(ID_CONNECT);
                rssi_handler.sendEmptyMessage(ID_RSSI);
                break;
            case R.id.flash:
                flash_handler.sendEmptyMessage(ID_FLASH);
                break;
            case R.id.seekBar:
                flash_handler.sendEmptyMessage(ID_SERVO);
                break;
            default:
                break;
        }
    }

    private class HttpHandler extends Handler
    {
        public HttpHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case ID_CONNECT:
                    VideoStream();
                    break;
                case ID_FLASH:
                    SetFlash();
                    break;
                case ID_RSSI:
                    GetRSSI();
                    break;
                case ID_UP:
                    MoveUp();
                    break;
                case ID_DOWN:
                    MoveDown();
                    break;
                case ID_LEFT:
                    MoveLeft();
                    break;
                case ID_RIGHT:
                    MoveRight();
                    break;
                case ID_SERVO:
                    MoveServo();
                case ID_STOP:
                    MotorsStop();
                    break;
                default:

                    break;
            }
        }
    }

    private void SetFlash()
    {
        flash_on_off ^= true;

        String flash_url;
        if(flash_on_off){
            flash_url = "http://" + ip_text.getText() + ":80/led?var=flash&val=1";
        }
        else {
            flash_url = "http://" + ip_text.getText() + ":80/led?var=flash&val=0";
        }
        try
        {
            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MoveServo()
    {
        String flash_url;

        //ip_text.setText(String.valueOf(flash_on_off));

        flash_url = "http://" + ip_text.getText() + ":80/led?var=servo&val="+seek_value;
        Log.e("Seek value",String.valueOf(seek_value));
        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MoveUp()
    {
        String flash_url;

            //ip_text.setText(String.valueOf(flash_on_off));
            flash_url = "http://" + ip_text.getText() + ":80/led?var=move&val=0";

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MoveDown()
    {
        String flash_url;

        //ip_text.setText(String.valueOf(flash_on_off));
        flash_url = "http://" + ip_text.getText() + ":80/led?var=move&val=1";

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MoveLeft()
    {
        String flash_url;

        //ip_text.setText(String.valueOf(flash_on_off));
        flash_url = "http://" + ip_text.getText() + ":80/led?var=move&val=2";

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MoveRight()
    {
        String flash_url;

        //ip_text.setText(String.valueOf(flash_on_off));
        flash_url = "http://" + ip_text.getText() + ":80/led?var=move&val=3";

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void MotorsStop()
    {
        String flash_url;

        //ip_text.setText(String.valueOf(flash_on_off));
        flash_url = "http://" + ip_text.getText() + ":80/led?var=move&val=5";

        try
        {

            URL url = new URL(flash_url);

            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setConnectTimeout(1000 * 5);
            huc.setReadTimeout(1000 * 5);
            huc.setDoInput(true);
            huc.connect();
            if (huc.getResponseCode() == 200)
            {
                InputStream in = huc.getInputStream();

                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void GetRSSI() {
        rssi_handler.sendEmptyMessageDelayed(ID_RSSI,500);

        String rssi_url = "http://" + ip_text.getText() + ":80/RSSI";

        try {
            URL url = new URL(rssi_url);

            try {

                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
                huc.setConnectTimeout(1000 * 5);
                huc.setReadTimeout(1000 * 5);
                huc.setDoInput(true);
                huc.connect();
                if (huc.getResponseCode() == 200) {
                    InputStream in = huc.getInputStream();

                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);
                    final String data = br.readLine();
                    if (!data.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rssi_text.setText(data);
                            }
                        });
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void VideoStream()
    {
        String stream_url = "http://" + ip_text.getText() + ":81/stream";

        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        try
        {
            URL url = new URL(stream_url);
            try
            {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
                huc.setConnectTimeout(1000 * 5);
                huc.setReadTimeout(1000 * 5);
                huc.setDoInput(true);
                huc.connect();

                if (huc.getResponseCode() == 200)
                {
                    InputStream in = huc.getInputStream();

                    InputStreamReader isr = new InputStreamReader(in);
                    BufferedReader br = new BufferedReader(isr);

                    String data;

                    int len;
                    byte[] buffer;

                    while ((data = br.readLine()) != null)
                    {
                        if (data.contains("Content-Type:"))
                        {
                            data = br.readLine();
                            len = Integer.parseInt(data.split(":")[1].trim());
                            bis = new BufferedInputStream(in);
                            buffer = new byte[len];
                            int t = 0;
                            while (t < len)
                            {
                                t += bis.read(buffer, t, len - t);
                            }
                            Bytes2ImageFile(buffer,
                                    getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");
                            final Bitmap bitmap = BitmapFactory.decodeFile(
                                    getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/0A.jpg");
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    monitor.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (bis != null)
                {
                    bis.close();
                }
                if (fos != null)
                {
                    fos.close();
                }

                stream_handler.sendEmptyMessageDelayed(ID_CONNECT,3000);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    private void Bytes2ImageFile(byte[] bytes, String fileName)
    {
        try
        {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}