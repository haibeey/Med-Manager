package com.haibeey.android.medmanager.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haibeey.android.medmanager.R;
import com.haibeey.android.medmanager.Utils;
import com.haibeey.android.medmanager.fragments.SetMedicationFragment;
import com.haibeey.android.medmanager.model.DbContract;
import com.haibeey.android.medmanager.model.DbHandleHelper;
import com.haibeey.android.medmanager.model.MedRecord;

import java.util.ArrayList;

/**
 * Created by haibeey on 3/30/2018.
 */

public class AdapterForHomeFragment extends RecyclerView.Adapter<AdapterForHomeFragment.ViewHolder>{

    private ArrayList<MedRecord> medRecords;
    private Context context;
    private int currentMonth=-1;
    private DbHandleHelper dbHandleHelper;

    public  AdapterForHomeFragment(Context context){
        this.context=context;
        dbHandleHelper=new DbHandleHelper(context.getApplicationContext());
        RefreshData();

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.recycler_view_layout_start,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return medRecords.get(position).getID();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //gets the current holder
        synchronized (this){
            MedRecord medRecord=medRecords.get(position);
            if(currentMonth==-1 || position==0){
                currentMonth=medRecord.getSTART_MONTH();
                holder.bindOnNewMonth(medRecord);
            }else if(medRecord.getSTART_MONTH()!=medRecords.get(position-1).getSTART_MONTH()){
                currentMonth=medRecord.getSTART_MONTH();
                holder.bindOnNewMonth(medRecord);
            }else{
                currentMonth=medRecord.getSTART_MONTH();
                holder.bindOnOldMonth(medRecord);
            }
        }
    }

    public  void RefreshData(){
        //fetching of data might take some time  in large database so run on another thread
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                //gets the data from the database
                medRecords=dbHandleHelper.queryEntryAll();
                return null;

            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.execute();
    }

    private void sortMedRecords(){
        if(medRecords==null)return;
        try{
            medRecords=Utils.SortList(medRecords);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        context=null;
    }


    @Override
    public int getItemCount() {
        if(medRecords!=null){
            return medRecords.size();
        }
        return 0;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewForMonth;
        private TextView title;//text view for title
        private ImageView delete;//Image view delete view
        private TextView description;//text view for description
        private TextView date;//date text View

        public ViewHolder(View itemView) {
            super(itemView);
            date=(TextView)itemView.findViewById(R.id.date);
            title=(TextView)itemView.findViewById(R.id.title);
            description=(TextView)itemView.findViewById(R.id.description);
            delete=(ImageView)itemView.findViewById(R.id.delete);
            textViewForMonth=(TextView)itemView.findViewById(R.id.setMonth);

        }

        public void bindOnNewMonth(final MedRecord medRecord){
            textViewForMonth.setVisibility(View.VISIBLE);
            textViewForMonth.setText(Utils.NumberToMonth(medRecord.getSTART_MONTH()));

            title.setText(medRecord.getNAME());
            description.setText(medRecord.getDESCRIPTION());
            date.setText("Ends on "+medRecord.getEndDate());

            //delete the records if it ends date has passed
            if(deleteRecordOnExpiration(medRecord))return;

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the record and cancel the job
                    dbHandleHelper.deleteFromTable(DbContract.MedicalEntry.TABLE_NAME,DbContract.MedicalEntry._ID,String.valueOf(medRecord.getID()));
                    //cancel the  job
                    Utils.CancelJob(medRecord.getID(),context);
                    //remove from med records
                    medRecords.remove(medRecord);
                    notifyDataSetChanged();
                }
            });
        }

        public void bindOnOldMonth(final MedRecord medRecord){
            title.setText(medRecord.getNAME());
            description.setText(medRecord.getDESCRIPTION());
            textViewForMonth.setVisibility(View.GONE);
            date.setText("Ends on "+medRecord.getEndDate());

            //delete the records if it ends date has passed
            if(deleteRecordOnExpiration(medRecord))return;

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete the record and cancel the job
                    dbHandleHelper.deleteFromTable(DbContract.MedicalEntry.TABLE_NAME,DbContract.MedicalEntry._ID,String.valueOf(medRecord.getID()));
                    //cancel the  job
                    Utils.CancelJob(medRecord.getID(),context);
                    //remove from med records
                    medRecords.remove(medRecord);
                    notifyDataSetChanged();
                }
            });
        }

        private boolean  deleteRecordOnExpiration(MedRecord medRecord){
            long timeOfRecord=Utils.GetSecsFromDate(medRecord.getEND_YEAR(),medRecord.getEND_MONTH(),
                    medRecord.getEND_DAY(),medRecord.getEND_HOUR(),medRecord.getSTART_MINUTE());

            if(System.currentTimeMillis()>=timeOfRecord){
                dbHandleHelper.deleteFromTable(DbContract.MedicalEntry.TABLE_NAME,DbContract.MedicalEntry._ID,String.valueOf(medRecord.getID()));
                //notifies used about medical expiration
                Utils.notifyOnWhenMedExpires(context,medRecord.getNAME());
                //remove from med records
                try{
                    //when scrolling  an exception does occur when trying to update the record
                    medRecords.remove(medRecord);
                    notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }
}
