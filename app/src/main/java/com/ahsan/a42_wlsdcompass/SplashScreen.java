//https://www.youtube.com/watch?v=ND6a4V-xdjI   How To Make Splash Screen in Android Studio by Humayun Kabir-This worked very well
//https://www.wlsdevelop.com/index.php/en/enblog?option=com_content&view=article&id=37    But need to use Activity instead of AppCOmpatActivity which gave the following error
//You need to use Theme.AppCompat theme (or descendant) with this activity.

package com.ahsan.a42_wlsdcompass;

import android.app.Activity;//NOTE: Activity instead of AppCompatActivity is used to use Handler()
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity{


    int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(SPLASH_TIME_OUT);
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();//Destroy this activity
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();

 /*
// Method by
//================For this method, we need to extends Activity which create problem in theme=====================//

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();//Destroy this activity
            }
        }, SPLASH_TIME_OUT) ;
*/
    }
}
