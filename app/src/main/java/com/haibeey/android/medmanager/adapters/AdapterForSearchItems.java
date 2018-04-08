package com.haibeey.android.medmanager.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haibeey.android.medmanager.R;
import com.haibeey.android.medmanager.Utils;
import com.haibeey.android.medmanager.model.DbContract;
import com.haibeey.android.medmanager.model.DbHandleHelper;
import com.haibeey.android.medmanager.model.MedRecord;

import java.util.ArrayList;

/**
 * Created by haibeey on 4/5/2018.
 */

public class AdapterForSearchItems extends RecyclerView.Adapter<AdapterForSearchItems.ViewHolder>{

    private ArrayList<MedRecord> medRecords;
    private Context context;
    private DbHandleHelper dbHandleHelper;
    private String QueryString;

    public AdapterForSearchItems(Context context,String queryString){
        this.context=context;
        this.QueryString=queryString;
        dbHandleHelper=new DbHandleHelper(context.getApplicationContext());
        RefreshData();

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(medRecords.get(position));
    }

    public  void RefreshData(){
        //fetching of data might take some time so run on another thread
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                //gets the data from the database
                medRecords=dbHandleHelper.Search(QueryString);
                if(medRecords.size()==0){
                    medRecords=new ArrayList<MedRecord>();
                    MedRecord medRecord=new MedRecord();
                    medRecord.setNAME("No Data Found");
                    medRecords.add(medRecord);
                }
                return null;

            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public long getItemId(int position) {
        return medRecords.get(position).getID();
    }

    @Override
    public int getItemCount() {
        if(medRecords!=null){
            return medRecords.size();
        }
        return 0;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;//text view for title
        private ImageView delete;//Image view delete view
        private TextView description;//text view for description
        private TextView date;
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView=itemView;
            title=(TextView)itemView.findViewById(R.id.title);
            description=(TextView)itemView.findViewById(R.id.description);
            date=(TextView)itemView.findViewById(R.id.date);

        }

        public void bind(final MedRecord medRecord){
            title.setText(medRecord.getNAME());
            description.setText(medRecord.getDESCRIPTION());
            if(medRecord.getEND_YEAR()!=0)
                date.setText("Ends on "+medRecord.getEndDate());

        }
    }
}
