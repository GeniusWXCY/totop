package com.genius.totop.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.genius.totop.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.inject(this);
        TextView textView = (TextView) findViewById(R.id.text_title);
        textView.setText(getString(R.string.str_history));
    }

    @OnClick(R.id.imageview_btn_back)
    public void back(){
        finish();
    }

}
