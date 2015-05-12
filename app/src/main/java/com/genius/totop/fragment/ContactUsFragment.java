package com.genius.totop.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.totop.genius.BuildConfig;
import com.totop.genius.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ContactUsFragment extends Fragment {

    @InjectView(R.id.text_title)TextView titleText;
    @InjectView(R.id.tv_version)TextView versionText;

    private OnHomeFragmentListener mListener;

    public ContactUsFragment() {
    }

    public static ContactUsFragment newInstance() {
        return new ContactUsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.inject(this, view);
        titleText.setText(getString(R.string.menu_contact));
        versionText.setText("版本V" + BuildConfig.VERSION_NAME);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnHomeFragmentListener) activity;
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @OnClick(R.id.imageview_btn_back)
    public void back(){
        mListener.back();
    }
}
