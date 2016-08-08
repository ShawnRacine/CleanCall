package com.racine.cleancalls.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sunrx on 2016/8/8.
 */
public abstract class BaseFragment extends Fragment {
    protected View mView;
    protected Context mContext;
    protected OnTabSelectedListener mListener;

    protected LayoutInflater inflater;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            mListener = (OnTabSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    "Activity must implement OnArticleSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;

        onCreateView();

        initComponents();

        registListeners();

        loadDatas();

        return mView;
    }

    protected void setContentView(int layout) {
        mView = inflater.inflate(layout, null);
    }

    protected abstract void onCreateView();

    protected abstract void initComponents();

    protected abstract void registListeners();

    protected abstract void loadDatas();

    interface OnTabSelectedListener {
        void onArticleSelected(Object obj, int what);
    }
}
