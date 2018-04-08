package com.haibeey.android.medmanager.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.haibeey.android.medmanager.R;
import com.haibeey.android.medmanager.Utils;
import com.haibeey.android.medmanager.model.DbContract;
import com.haibeey.android.medmanager.model.DbHandleHelper;

import java.util.Calendar;


public class SetMedicationFragment extends Fragment {
    //Listens for new data
    private DataInserted Listener;

    private static int END_DATE_OR_START_DATE=0;//can only hold to values CHOOSE_START or  CHOOSE_END
    private final static int CHOOSE_START=0;
    private final static int CHOOSE_END=1;

    //class variables to hold start date
    private static int startHour=-1;
    private static int startDayOfMonth=-1;
    private static int startMinute=-1;
    private static int startMonth=-1;
    private static int startYear=-1;

    //class variables to holder end date
    private static int endHour=-1;
    private static int endDayOfMonth=-1;
    private static int endMinute=1;
    private static int endMonth=-1;
    private static int endYear=-1;

    private View view;

    //preset to one hour
    private int interval=1;

    private AutoCompleteTextView AutoCompleteTextViewForName;
    private AutoCompleteTextView AutoCompleteTextViewForDescription;

    public void setListener(DataInserted listener) {
        Listener = listener;
    }

    View.OnClickListener ListenerForFinishSettingOfDates=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text_name=AutoCompleteTextViewForName.getText().toString();
            String text_description=AutoCompleteTextViewForDescription.getText().toString();

            if(TextUtils.isEmpty(text_name) || TextUtils.isEmpty(text_description) || !AllDataSet()){
                Snackbar.make(view,getString(R.string.Empty_Data),Snackbar.LENGTH_LONG).show();
                return;
            }

            DbHandleHelper mDbHandleHelper=new DbHandleHelper(getContext().getApplicationContext());
            //since i am inserting a new data the id would be count of the database plus onr
            long id =mDbHandleHelper.countDb(DbContract.MedicalEntry.TABLE_NAME)+1;

            mDbHandleHelper.insertEntry(startYear,startMonth,startDayOfMonth,startHour,startMinute,endYear,endMonth,
                    endDayOfMonth,endHour,endMinute,text_name,text_description,interval);

            Bundle bundle=getBundle(id,text_name,text_description);

            //schedules the notification using firebase job scheduler
            Utils.ScheduleNotification(bundle, getContext());

            if(Listener!=null)Listener.NewDataInserted();

            Toast.makeText(getContext(),getString(R.string.data_inserted),Toast.LENGTH_LONG).show();
        }
    };

    private Bundle getBundle(long id,String text_name,String text_description){
        Bundle bundle=new Bundle();

        bundle.putLong("id",id);
        bundle.putInt("start_year",startYear);
        bundle.putInt("start_month",startMonth);
        bundle.putInt("start_day",startDayOfMonth);
        bundle.putInt("start_hour",startHour);
        bundle.putInt("start_minute",startMinute);

        bundle.putInt("end_year",endYear);
        bundle.putInt("end_month",endMonth);
        bundle.putInt("end_day",startDayOfMonth);
        bundle.putInt("end_hour",endHour);
        bundle.putInt("end_minute",endMinute);

        bundle.putString("name",text_name);
        bundle.putString("description",text_description);

        bundle.putInt("interval",interval);

        return bundle;
    }

    View.OnClickListener ListenerForAddStartDate=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            END_DATE_OR_START_DATE=CHOOSE_START;
            new TimePicker().show(getFragmentManager(),"time");
        }
    };

    View.OnClickListener ListenerForAddEndDate=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            END_DATE_OR_START_DATE=CHOOSE_END;
            new TimePicker().show(getFragmentManager(),"time");
        }
    };

    public SetMedicationFragment() {
        // Required empty public constructor
    }

    public static SetMedicationFragment newInstance() {
        SetMedicationFragment fragment = new SetMedicationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and make a view object
        view=inflater.inflate(R.layout.fragment_set_medication, container, false);

        AutoCompleteTextViewForDescription=(AutoCompleteTextView)view.findViewById(R.id.description);
        AutoCompleteTextViewForName=(AutoCompleteTextView)view.findViewById(R.id.title);

        //set the spinner
        Spinner spinner=(Spinner)view.findViewById(R.id.interval);
        setSpinner(spinner,R.array.hours_as_intervals);

        //This button start a time picker dialogue fragment to set when to start a medication
        Button buttonForStartPicker=(Button)view.findViewById(R.id.start_pickers);
        buttonForStartPicker.setOnClickListener(ListenerForAddStartDate);
        //This button start a time picker dialogue fragment to set when to end a medication
        Button buttonForEndPicker=(Button)view.findViewById(R.id.end_pickers);
        buttonForEndPicker.setOnClickListener(ListenerForAddEndDate);

        //finish setting of medical
        ImageView imageView=(ImageView)view.findViewById(R.id.add_finish);
        imageView.setOnClickListener(ListenerForFinishSettingOfDates);

        return  view;
    }


    public static  class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
            switch (END_DATE_OR_START_DATE){
                case CHOOSE_START:{
                    setStartHour(hourOfDay);
                    setStartMinute(minute);
                }
                case CHOOSE_END:{
                    setEndHour(hourOfDay);
                    setEndMinute(minute);
                }
                default:
                    setStartHour(hourOfDay);
                    setStartMinute(minute);
            }
            DatePicker datePicker=new DatePicker();
            datePicker.show(getFragmentManager(),"date");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
    }

    public static class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener  {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            switch (END_DATE_OR_START_DATE){
                case CHOOSE_START:{
                    setStartYear(year);
                    setStartMonth(month);
                    setStartDayOfMonth(dayOfMonth);
                }
                case CHOOSE_END:{
                    setEndYear(year);
                    setEndMonth(month);
                    setEndDayOfMonth(dayOfMonth);
                }
                default:
                    setStartYear(year);
                    setStartMonth(month);
                    setStartDayOfMonth(dayOfMonth);
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
    }

    private boolean AllDataSet(){
        return startDayOfMonth!=-1 && endDayOfMonth!=-1 &&
                startHour!=-1 && endHour!=-1 &&
                startYear!=-1 && endYear!=-1 &&
                startMonth!=-1 && endMonth!=-1 &&
                startMinute!=-1 && endMinute!=-1;
    }
    public static void setEndHour(int endHour) {
        SetMedicationFragment.endHour = endHour;
    }

    public static void setStartDayOfMonth(int startDayOfMonth) {
        SetMedicationFragment.startDayOfMonth = startDayOfMonth;
    }

    public static void setEndMinute(int endMinute) {
        SetMedicationFragment.endMinute = endMinute;
    }

    public static void setStartHour(int startHour) {
        SetMedicationFragment.startHour = startHour;
    }

    public static void setStartYear(int startYear) {
        SetMedicationFragment.startYear = startYear;
    }

    public static void setEndYear(int endYear) {
        SetMedicationFragment.endYear = endYear;
    }

    public static void setStartMonth(int startMonth) {
        SetMedicationFragment.startMonth = startMonth;
    }

    public static void setEndDayOfMonth(int endDayOfMonth) {
        SetMedicationFragment.endDayOfMonth = endDayOfMonth;
    }

    public static void setEndMonth(int endMonth) {
        SetMedicationFragment.endMonth = endMonth;
    }

    public static void setStartMinute(int startMinute) {
        SetMedicationFragment.startMinute = startMinute;
    }


    private void setSpinner(Spinner spinner, int stringResource){

        final ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getContext(),stringResource,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] List=adapter.getItem(position).toString().split(" ");
                interval=Integer.valueOf(List[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public interface DataInserted{
        void NewDataInserted();
    }
}
