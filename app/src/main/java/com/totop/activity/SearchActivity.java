package com.totop.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.totop.fragment.SearchFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SearchActivity extends FragmentActivity {

    @InjectView(R.id.searchText) TextView mSearchText;
    @InjectView(R.id.searchInput) EditText mSearchInputEditText;

    private SearchFragment mSearchFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);

        ButterKnife.inject(this);

        mSearchFragment = new SearchFragment();

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.fragment, mSearchFragment).hide(mSearchFragment).commit();
    }

    @OnClick(R.id.searchText)
    public void search(View view) {
        mFragmentManager.beginTransaction().show(mSearchFragment).commit();
    }
}
