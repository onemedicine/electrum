package org.haobtc.wallet.activities.base;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.greenrobot.eventbus.EventBus;
import org.haobtc.wallet.MyEventBusIndex;
import org.haobtc.wallet.utils.Global;

import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;

public class MyApplication extends Application {
    private static volatile MyApplication mInstance;
    private static final String PRIMARY_SERVICE =      "00000001-0000-1000-8000-00805f9b34fb";
    private static final String WRITE_CHARACTERISTIC = "00000002-0000-1000-8000-00805f9b34fb";
    private static final String READ_CHARACTERISTIC =  "00000003-0000-1000-8000-00805f9b34fb";


    @Override
    public void onCreate() {
        super.onCreate();
        // add application lifecycle observer
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
        // EventBus optimize
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        mInstance = this;
        initBle();
        initChaquo();
    }
    public static MyApplication getInstance() {
        if (mInstance == null) {
            synchronized (MyApplication.class) {
                if (mInstance == null) {
                    mInstance = new MyApplication();
                }
            }
        }
        return mInstance;
    }
    // init ble
    private void initBle() {
        //Set whether to print Bluetooth log
        Ble.options().setLogBleEnable(false)
                /* Set whether to throw Bluetooth exception */
                .setThrowBleException(true)
                //Set global Bluetooth operation log TAG
                .setLogTAG("AndroidBLE")
                //Set whether to connect automatically
                .setAutoConnect(false)
                //Set whether to filter the founded devices
                .setFilterScan(true)
                //Set whether to filter the founded devices
                .setConnectFailedRetryCount(3)
                // Set the connection timeout
                .setConnectTimeout(10 * 1000)
                // Set the Scanning period
                .setScanPeriod(12 * 1000)
                .setServiceBindFailedRetryCount(3)
                .setUuidService(UUID.fromString(PRIMARY_SERVICE))
                .setUuidWriteCha(UUID.fromString(WRITE_CHARACTERISTIC))
                .setUuidNotify(UUID.fromString(READ_CHARACTERISTIC))
                .setUuidReadCha(UUID.fromString(READ_CHARACTERISTIC))
                .create(mInstance);
    }
    private void initChaquo() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(mInstance));
        }
        Global.py = Python.getInstance();
    }
}
