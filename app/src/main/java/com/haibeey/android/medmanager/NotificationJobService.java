package com.haibeey.android.medmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by haibeey on 3/29/2018.
 */

public class NotificationJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        //gets the bundle parameters
        Bundle bundle=job.getExtras();
        //create a notification manager
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // creates a pending intent for starting the activity
        Intent intentForNotification = new Intent(getBaseContext(), MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getBaseContext());
        taskStackBuilder.addNextIntentWithParentStack(intentForNotification);
        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        //builds the notification
        Notification notification= new NotificationCompat.
                Builder(getBaseContext()).
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).
                setContentIntent(resultPendingIntent).
                setContentTitle(bundle.getString("name")).
                setContentText(bundle.getString("description")).
                setSmallIcon(R.drawable.common_full_open_on_phone).
                build();

        //notifies the system
        notificationManager.notify((int)bundle.getLong("id"),notification);

        return true;//we are done
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }
}
