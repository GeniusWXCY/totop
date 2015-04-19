package com.totop.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryActivity extends Activity {

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
