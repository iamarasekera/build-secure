package core.buildsecure.ishara.lk.libproject;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import lk.ishara.buildsecure.core.BuildSecure;

public class MainActivity extends AppCompatActivity {
    @Override
    @BuildSecure
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Camera c = getCameraInstance();
       //
      //   Log                 .e("log test", "log message");
    }
    @BuildSecure
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); {
            }
        } catch (Exception e){

        }
        return c;
    }
}
