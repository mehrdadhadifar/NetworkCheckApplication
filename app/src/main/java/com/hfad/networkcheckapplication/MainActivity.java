package com.hfad.networkcheckapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.hfad.networkcheckapplication.utils.NetworkConnection;

public class MainActivity extends AppCompatActivity {
    NetworkConnection mNetworkConnection;
    TextView networkStateTextView;
    TextView palPhoneStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkStateTextView = findViewById(R.id.state_text_view_network);
        palPhoneStateTextView = findViewById(R.id.state_text_view_pal_phone);

        mNetworkConnection = new NetworkConnection(getApplicationContext());
        mNetworkConnection.onActive();

        mNetworkConnection.getNetworkState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean)
                    networkStateTextView.setText("PalPhone has no internet access!!!");
                else
                    networkStateTextView.setText("Internet is Ok!!");
            }
        });

        mNetworkConnection.getPalPhoneServerLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    palPhoneStateTextView.setText("Pal Phone server is ready");
                else
                    palPhoneStateTextView.setText("Pal Phone server in not available at the moment");
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNetworkConnection.ping();
                handler.postDelayed(this, 1000);
            }
        }, 1000);  //the time is in miliseconds
    }

}