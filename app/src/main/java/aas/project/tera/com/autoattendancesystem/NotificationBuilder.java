package aas.project.tera.com.autoattendancesystem;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.androidquery.AQuery;

import aas.tera.com.autoattendancesystem.R;

/**
 * Created by Administrator on 2015-05-26.
 */
public class NotificationBuilder {

    public static void doEnterNotify(AQuery aQuery, String ticker, String title, String text){
        NotificationManager nm = (NotificationManager) aQuery.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(aQuery.getContext());
        mCompatBuilder.setSmallIcon(R.drawable.heart1);
        mCompatBuilder.setTicker(ticker);
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setContentTitle(title);
        mCompatBuilder.setContentText(text);
        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setAutoCancel(true);

        nm.notify(222, mCompatBuilder.build());
    }

    public static void doExitNotify(AQuery aQuery, String rname, String ticker, String title, String text){
        NotificationManager nm = (NotificationManager) aQuery.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(aQuery.getContext());
        mCompatBuilder.setSmallIcon(R.drawable.heart1);
        mCompatBuilder.setTicker(ticker);
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setContentTitle(title);
        mCompatBuilder.setContentText(text);
        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setAutoCancel(true);

        nm.notify(222, mCompatBuilder.build());
    }
}
