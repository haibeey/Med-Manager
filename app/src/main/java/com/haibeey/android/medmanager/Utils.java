package com.haibeey.android.medmanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.TimeUtils;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.haibeey.android.medmanager.model.DbContract;
import com.haibeey.android.medmanager.model.MedRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;


/**
 * Util class for various task
 */


public class Utils {

    public static final String sharedPreferenceName ="profile";
    public static final String profileUserName ="username";
    public static final String profileFirstName ="firstname";
    public static final String profileLastName ="lastname";
    public static  final  String profileEmail="email";
    public  static  final String profileUrl="profileUrl";

    public static void ScheduleNotification(Bundle bundle,Context context){
        FirebaseJobDispatcher dispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(context));

        int Interval=bundle.getInt("interval");
        String id=String.valueOf(bundle.getLong("id"));

        int intervalInSecs= (int) TimeUnit.HOURS.toSeconds(Interval);
        //interval for which job should be execute three minute at most
        int intervalForJobExecution=120;

        Job job=dispatcher.newJobBuilder().
                //should be a recurring job
                setRecurring(true).
                //update the current  job
                setReplaceCurrent(true).
                setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL).
                setTag(id).
                setExtras(bundle).
                setService(NotificationJobService.class).
                setTrigger(Trigger.executionWindow(intervalForJobExecution,intervalForJobExecution+intervalForJobExecution)).
                setLifetime(Lifetime.FOREVER).build();

        dispatcher.mustSchedule(job);
    }

    public  static void CancelJob(int id,Context context){
        String tag=String.valueOf(id);

        FirebaseJobDispatcher dispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(tag);
    }

    public static void notifyOnWhenMedExpires(Context context,String name){
        //create a notification manager
        NotificationManager notificationManager= (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);


        //builds the notification
        Notification notification= new NotificationCompat.
                Builder(context).
                setContentTitle(context.getString(R.string.end_med_title)+name).
                setSmallIcon(R.drawable.common_full_open_on_phone).
                build();

        //notifies the system
        notificationManager.notify(1,notification);
    }

    public  static ArrayList<MedRecord> SortList(ArrayList<MedRecord> medRecords) throws ClassCastException{
        //sorts the records by month for categorisation
        Object[] medRecord=medRecords.toArray();

        Arrays.sort(medRecord, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                MedRecord medRecord1=(MedRecord)o1;
                MedRecord medRecord2=(MedRecord)o2;
                if(medRecord1.getEND_MONTH()>medRecord2.getEND_MONTH())
                    return 1;
                else if(medRecord1.getEND_MONTH()<medRecord2.getEND_MONTH())
                    return -1;
                else
                    return 0;
            }
        });

        ArrayList<MedRecord> result=new ArrayList<>();
        for(Object med_record:medRecord){
            result.add((MedRecord)med_record);
        }

        return  result;
    }

    public static String NumberToMonth(int number){
        switch (number){
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static boolean checkConnectivity(Context context){
        ConnectivityManager check= (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        if(check!=null){
            NetworkInfo[] info=check.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()== NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public  static  long GetSecsFromDate(int year,int month,int day,int hour,int minute){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH,month);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,minute);

        return calendar.getTimeInMillis();
    }

    public static void SetProfileToPreferences(Context context,String username,String firstName,
                                               String lastName,String email,String photoUrl){
        SharedPreferences.Editor editor=context.getSharedPreferences(sharedPreferenceName,Context.MODE_PRIVATE).edit();
        editor.putString(profileUserName,username);
        editor.putString(profileFirstName,firstName);
        editor.putString(profileLastName,lastName);
        editor.putString(profileEmail,email);
        editor.putString(profileUrl,photoUrl);
        editor.commit();
    }

    public  static String getSharedPreferenceValue(Context context,String key){
        SharedPreferences sharedPreferences=context.getSharedPreferences(sharedPreferenceName,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }
}
