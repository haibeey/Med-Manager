package com.haibeey.android.medmanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haibeey.android.medmanager.R;
import com.haibeey.android.medmanager.adapters.AdapterForHomeFragment;


public class HomeFragment extends Fragment implements SetMedicationFragment.DataInserted{


    private View view;
    private AdapterForHomeFragment adapterForHomeFragment;
    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view!=null)return view;
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_home, container, false);
        //instantiate the adapter
        adapterForHomeFragment=new AdapterForHomeFragment(getContext());
        //finds the recycler view and set it ups
        RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.all_records);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapterForHomeFragment);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void NewDataInserted() {
        if(adapterForHomeFragment!=null){
            adapterForHomeFragment.RefreshData();
        }
    }
}
