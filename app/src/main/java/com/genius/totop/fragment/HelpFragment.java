package com.genius.totop.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genius.totop.R;
import com.genius.totop.manager.CacheDataManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpFragment extends Fragment {

    private OnHomeFragmentListener mListener;

    public HelpFragment() {
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  =  inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.inject(this,view);
        TextView titleText = (TextView) view.findViewById(R.id.text_title);
        TextView helpText = (TextView) view.findViewById(R.id.tv_help_text);
        helpText.setText(Html.fromHtml(CacheDataManager.mCacheData.content));
        titleText.setText(getString(R.string.menu_help));
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
