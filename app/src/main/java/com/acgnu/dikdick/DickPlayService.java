package com.acgnu.dikdick;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

public class DickPlayService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;
    private SoundPoolUtil mSoundPoolInstance;
    private int delay = 0;
    private boolean isRunning = false;

    public DickPlayService() {
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            try {
                while (true) {
                    if(delay == 0) {
                        break;
                    }
                    mSoundPoolInstance.play();
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                stopSelf(msg.arg1);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
            Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
        mSoundPoolInstance = SoundPoolUtil.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        delay = intent.getIntExtra("speed", AddNewActivity.MIN_DELAY);
        if(! isRunning) {
            Message msg = serviceHandler.obtainMessage();
            msg.arg1 = startId;
            serviceHandler.sendMessage(msg);
            isRunning = true;
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        delay = 0;
        stopSelf();
    }
}
