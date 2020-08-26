package com.lab104.translatebraill;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static String[] REQUEST_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.FLASHLIGHT",
            "android.permission.INTERNET"
    };
    public static File file;
    public static ImageView frame;
    public static ViewGroup.LayoutParams params, paramsFrame;
    private SensorManager sensorManager;
    private Sensor sensorLinAccel, gravity;
    private ViewGroup flashlightMenu;
    private FloatingActionButton fabFlashlight;
    private TextureView textureView;
    private DisplayMetrics dm = new DisplayMetrics();
    private Switch netStat;
    private TextView text;
    private ImageCapture imageCapture;
    private ConstraintLayout constraintLayout;
    private float[] valuesLinAccel = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        Init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorLinAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, gravity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                        //Log.i("LinAccel", String.valueOf(valuesLinAccel[1]));
//                        Log.i("LinAccel", String.valueOf(valuesLinAccel[0]));
                    }
                    int degrees = 0;
                        if (valuesLinAccel[0] >= 9f || (valuesLinAccel[2] <= 9f && valuesLinAccel[1] <= 0f && valuesLinAccel[0] >= 7f))
                        {

                            fabFlashlight.setRotation(90f);
                            flashlightMenu.setRotation(90f);
                            if (degrees != 90){
                                degrees = 90;
                                constraintLayout = findViewById(R.id.layoutparent);
                                ConstraintSet constraintSet = new ConstraintSet();
                                constraintSet.clone(constraintLayout);
                                constraintSet.clear(R.id.flashlightMenu,ConstraintSet.LEFT);
                                constraintSet.clear(R.id.flashlightMenu,ConstraintSet.TOP);
                                constraintSet.connect(R.id.flashlightMenu,ConstraintSet.TOP,R.id.toolbarTop,ConstraintSet.BOTTOM);
                                constraintSet.connect(R.id.flashlightMenu,ConstraintSet.LEFT,R.id.textureView,ConstraintSet.LEFT,100);
                                constraintSet.setMargin(R.id.flashlightMenu,ConstraintSet.LEFT,100);
                                TransitionManager.beginDelayedTransition(constraintLayout);
                                constraintSet.applyTo(constraintLayout);
                            }

                        }
                        else if (valuesLinAccel[0] <= 1f && valuesLinAccel[0] >= -1f && valuesLinAccel[1] >= 4f)
                        {
                            fabFlashlight.setRotation(0f);
                            flashlightMenu.setRotation(0f);
                        }
                        else if (valuesLinAccel[0] <= -9f || (valuesLinAccel[2] <= 9f && valuesLinAccel[1] <= 0f && valuesLinAccel[0] <= -7f))
                        {
                            Log.i("LinAccel", valuesLinAccel[0] + "- X " + valuesLinAccel[1] + "- Y " + valuesLinAccel[2] + "- Z ");

                            fabFlashlight.setRotation(-90f);
                            flashlightMenu.setRotation(-90f);
                        }

                    break;
            }

        }

    };

    private void Init()
    {
        textureView = findViewById(R.id.textureView);
        fabFlashlight = findViewById(R.id.fabFlashlight);
        flashlightMenu = findViewById(R.id.flashlightMenu);
        netStat = findViewById(R.id.netStat);
        frame = findViewById(R.id.frame);
        paramsFrame = frame.getLayoutParams();
        params = textureView.getLayoutParams();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        params.width = dm.widthPixels;
        params.height = (dm.widthPixels*16)/9;
        Toast.makeText(this, Integer.toString(dm.heightPixels), Toast.LENGTH_SHORT).show();
        textureView.setLayoutParams(params);
        paramsFrame.height = (int)(dm.widthPixels * Math.sqrt(2));
        frame.setLayoutParams(paramsFrame);
        if (PermissionGranted())
        {
            StartCamera();
        }
        else
        {
            ActivityCompat.requestPermissions(this,REQUEST_PERMISSIONS,REQUEST_CODE_PERMISSIONS);
        }
        ButtonHandler();

    }

    private void ButtonHandler()
    {
        netStat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked())
                {
                    if (!itsOnline(getApplicationContext()))
                    {
                        compoundButton.setChecked(false);
                    }
                    else
                    {
                        netStat.setText("Online");
                    }
                }
                else
                {
                    netStat.setText("Offline");
                }
            }
        });
        findViewById(R.id.fabFlashlight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashlightMenu.getVisibility() == View.GONE)
                {
                    flashlightMenu.setVisibility(View.VISIBLE);
                }
                else
                {
                    flashlightMenu.setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.btnFlashAUTO).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabFlashlight.setImageResource(R.drawable.flashauto);
                imageCapture.setFlashMode(FlashMode.AUTO);
                flashlightMenu.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.btnFlashOFF).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabFlashlight.setImageResource(R.drawable.flashoff);
                imageCapture.setFlashMode(FlashMode.OFF);
                flashlightMenu.setVisibility(View.GONE);

            }
        });
        findViewById(R.id.btnFlashON).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabFlashlight.setImageResource(R.drawable.flashon);
                imageCapture.setFlashMode(FlashMode.ON);
                flashlightMenu.setVisibility(View.GONE);

            }
        });
    }

    private void StartCamera()
    {
        CameraX.unbindAll();
        Rational ratio = new Rational(textureView.getWidth(),textureView.getHeight());
        Size screen = new Size(dm.widthPixels,dm.heightPixels);
        PreviewConfig previewConfig = new PreviewConfig.Builder().setTargetAspectRatio(ratio).setTargetResolution(screen).build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView,0);
                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        switch (displayRotation) {
                            case Surface.ROTATION_0:
                                break;
                            case Surface.ROTATION_90:
                                //textureView.setRotation(-90f);
                                break;
                            case Surface.ROTATION_270:
                                //textureView.setRotation(90f);
                                break;
                            default:
                                throw new UnsupportedOperationException(
                                        "Unsupported display rotation: " + displayRotation);
                        }

                    }
                }
        );
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().
                setCaptureMode(ImageCapture.CaptureMode.MAX_QUALITY).build();
        imageCapture = new ImageCapture(imageCaptureConfig);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final File filedirs = new File(getFilesDir() + "/TranslateBraille/");
                filedirs.mkdirs();
                file = new File(filedirs, "photo.jpg");
                //Toast.makeText(MainActivity.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String msg = "Picture saved at " + file.getAbsolutePath();
                        Toast.makeText(CameraActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.d("path", msg);
                        Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
                        intent.putExtra("imageUri", Uri.parse(file.getAbsolutePath()));
                        startActivity(intent);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Picture was not captured: " + message;
                        Toast.makeText(CameraActivity.this, msg, Toast.LENGTH_SHORT).show();
                        if (cause != null)
                        {
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });
        CameraX.bindToLifecycle(this, preview, imageCapture);


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS && PermissionGranted())
        {
            StartCamera();
        }
        else
        {
            Toast.makeText(this, "Permission was denied by the user!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean PermissionGranted() {
        for (String permission : REQUEST_PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }
        return true;
    }

    public static boolean itsOnline(Context context) {
        try {
            Log.d("COMPOUND","зашел");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

            int timeoutMs = 2000;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();
            Log.i("CONNECTION STATUS:", "connected");

            return true;
        } catch (IOException ioException) {
            Log.i("CONNECTION STATUS:", "disconnected");
            return false;
        }
    }

}
