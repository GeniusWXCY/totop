package com.totop.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.totop.activity.R;

public class HelpFragment extends Fragment {

    public HelpFragment() {
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }


}
