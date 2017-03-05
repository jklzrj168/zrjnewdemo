package com.amap.location.demo;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class AlarmService extends Service {
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();

    @Override
    public void onCreate() {

        locationClient = new AMapLocationClient(this.getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void startLocation() {
        //根据控件的选择，重新设置定位参数
        resetOption();
        // 设置定位参数
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(locationListener);
        // 启动定位
        locationClient.startLocation();
    }
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                String result = Utils.getLocationStr(loc);
              //  tvResult.setText(result);
                System.out.println("定位时间：" + new Date().toString());//获取日期时间
                System.out.println("定位结果：" + result);//获取结果
            } else {
             //   tvResult.setText("定位失败，loc is null");
            }
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {//可以在该线程中做需要处理的事
                System.out.println("当前时间：" + new Date().toString());//获取日期时间
                startLocation();
                stopSelf();//关闭服务
            }
        }).start();
        AlarmManager manger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);//广播接收
        //PendingIntent pendingIntent=PendingIntent.getActivity(MainActivity2_Text.this, 0, intent, 0);//意图为开启活动
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);//意图为开启广播
        long triggerAtTime = SystemClock.elapsedRealtime();//开机至今的时间毫秒数
        triggerAtTime = triggerAtTime + 10 * 1000;//比开机至今的时间增长10秒
        manger.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);//设置为开机至今的模式，时间，PendingIntent
        return super.onStartCommand(intent, flags, startId);
    }

    //复制代码
//说明：以上在子线程中只是打印了一条当前时间，你也可以去执行你想要做的事
//
//另外需要注意的是，从 Android 4.4 版本开始，Alarm 任务的触发时间将会变得不准确，
//有可能会延迟一段时间后任务才能得到执行。这并不是个 bug，而是系统在耗电性方面进行
//的优化。系统会自动检测目前有多少 Alarm 任务存在，然后将触发时间将近的几个任务放在
//一起执行，这就可以大幅度地减少 CPU 被唤醒的次数，从而有效延长电池的使用时间。
//当然，如果你要求 Alarm 任务的执行时间必须准备无误，Android 仍然提供了解决方案。
//使用 AlarmManager 的 setExact()方法来替代 set()方法，就可以保证任务准时执行了。
// 根据控件的选择，重新设置定位参数
    private void resetOption() {
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        //locationOption.setGpsFirst(cbGpsFirst.isChecked(存));
        // 设置是否开启缓
        locationOption.setLocationCacheEnable(true);
        // 设置是否单次定位
        locationOption.setOnceLocation(true);


        locationOption.setHttpTimeOut(20000);

    }
}