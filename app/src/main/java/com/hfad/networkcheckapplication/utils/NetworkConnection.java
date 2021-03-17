package com.hfad.networkcheckapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hfad.networkcheckapplication.data.remote.retrofit.RetrofitInstance;
import com.hfad.networkcheckapplication.data.remote.retrofit.TestAPI;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkConnection {
    private static final String TAG = "NetworkConnection";
    private MutableLiveData<Boolean> mNetworkStateLiveData;
    private MutableLiveData<Boolean> mPalPhoneServerLiveData;
    private ConnectivityManager mConnectivityManager;
    private TestAPI mTestApi = RetrofitInstance.getInstance().create(TestAPI.class);


    public NetworkConnection(Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        mNetworkCallback=new ConnectivityManager.NetworkCallback();
        mNetworkStateLiveData = new MutableLiveData<>();
        mPalPhoneServerLiveData = new MutableLiveData<>();
        mNetworkStateLiveData.setValue(false);
        mPalPhoneServerLiveData.setValue(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActive() {
        updateConnection();
        connectivityManagerCallback();
        ping();
//        mContext.registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
/*
    public void onInactive() {
        mContext.unregisterReceiver(networkReceiver);
    }*/

    public LiveData<Boolean> getNetworkState() {
        return mNetworkStateLiveData;
    }

    public MutableLiveData<Boolean> getPalPhoneServerLiveData() {
        return mPalPhoneServerLiveData;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void connectivityManagerCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mConnectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    mNetworkStateLiveData.postValue(true);
//                    ping();
                }

                @Override
                public void onLost(Network network) {
                    mNetworkStateLiveData.postValue(false);
//                    ping();
                }
            });
        }
    }

    public void updateConnection() {
        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null)
            mNetworkStateLiveData.postValue(activeNetworkInfo.isConnected());
    }
/*
    public BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateConnection();
        }
    };*/

    public boolean isNetworkConnected() {
        if (mNetworkStateLiveData.getValue() == null || !mNetworkStateLiveData.getValue())
            return false;
        return true;
    }

    public boolean isPalPhoneConnected() {
        if (mPalPhoneServerLiveData.getValue() == null || !getPalPhoneServerLiveData().getValue())
            return false;
        return true;
    }

    public void ping() {
        mTestApi.ping().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    mPalPhoneServerLiveData.postValue(true);
                    Log.d(TAG, "ping response massage: " + response.message());
                    Log.d(TAG, "ping body: " + response.body());
                } else
                    mPalPhoneServerLiveData.postValue(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "ping: " + t.getMessage());
                mPalPhoneServerLiveData.postValue(false);
            }
        });
    }

}