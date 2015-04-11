package com.totop.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totop.activity.R;

public class ContactUsFragment extends Fragment {

    public ContactUsFragment() {
    }

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_contact_us, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text_title);
        view.findViewById(R.id.imageview_btn_back).setVisibility(View.GONE);
        textView.setText(getString(R.string.menu_contact));
        return view;
    }
}
