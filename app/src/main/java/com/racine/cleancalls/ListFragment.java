package com.racine.cleancalls;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sunrx on 2016/8/8.
 */
public class ListFragment extends Fragment {
    private Context mContext;
    protected OnTabSelectedListener mListener;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = new TextView(mContext);
        textView.setText("");
        return textView;
    }

    interface OnTabSelectedListener {
        public void onArticleSelected(Object obj, int what);
    }
}
