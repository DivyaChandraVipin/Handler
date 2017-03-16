package assignment.android.acadgild.handler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    Button btnImage;
    EditText editTextImage;
    private Handler handler;
    ImageView imageView;
    Bitmap bitMapImg;
    File filename;
    private ProgressBar progressBar;
    OutputStream output;
    ByteArrayOutputStream bytearrayoutputstream;
    File file;
    String url;
    FileOutputStream fileoutputstream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnImage = (Button) findViewById(R.id.btnDownload);
        bytearrayoutputstream = new ByteArrayOutputStream();
        editTextImage = (EditText) findViewById(R.id.imageUrl);
        editTextImage.setText("https://www.planetnatural.com/wp-content/uploads/2014/02/rose-garden-1.jpg");
        imageView = (ImageView) findViewById(R.id.imageView);
        handler = new Handler();
         url=editTextImage.getText().toString();
        progressBar = (ProgressBar) findViewById(R.id.imageProgress);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();

                if (!checkPermission()) {

                    requestPermission();


                }

                new Thread(new Task()).start();
                Picasso.with(MainActivity.this).load(url)
                        .into(target);

            }
        });


    }
    private boolean checkPermission()
    {

        int result = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        return result == PackageManager.PERMISSION_GRANTED;
    }
    private boolean requestPermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},   //request specific permission from user
                10);
        return true;
    }
    private Target target=new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File filepath=Environment.getExternalStorageDirectory();
                    File dir = new File(filepath.getAbsolutePath()
                            + "/ABC/");
                    dir.mkdirs();

                    File file = new File(dir, "MyImage.jpeg");

                    try {

                        checkPermission();

                        if (!checkPermission()) {

                            requestPermission();


                        }
                        file.createNewFile();

                        final Bitmap bitmapImage=bitmap;

                       bitmapImage.compress(Bitmap.CompressFormat.JPEG,100, bytearrayoutputstream);
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                        fileoutputstream = new FileOutputStream(file);

                        fileoutputstream.write(bytearrayoutputstream.toByteArray());

                        fileoutputstream.close();

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    class Task implements Runnable {

        @Override
        public void run() {
            //Loading Image from URL

            for (int i = 0; i <= 10; i++) {
                final int value = i;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(value);

                    }
                });
            }
        }
    }
}