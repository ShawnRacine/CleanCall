package com.racine.cleancalls.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.racine.cleancalls.R;
import com.racine.cleancalls.net.HttpApi.JsonHttpApi;
import com.racine.cleancalls.net.HttpApi.bean.Response;
import com.racine.cleancalls.net.ThreadTask.JsonThreadTask;

/**
 * @author Shawn Racine.
 */
public class CallRecordsFragment extends BaseFragment {
    private ListView listView;
    private static final String[] strs = new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    };

    @Override
    protected void onCreateView() {
        setContentView(R.layout.callblocker);
    }

    @Override
    protected void initComponents() {
        listView = (ListView) mView.findViewById(R.id.listView);
    }

    @Override
    protected void registListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    protected void loadDatas() {
        new JsonThreadTask() {
            @Override
            protected String onPreExecute() {
                return super.onPreExecute();
            }

            @Override
            protected Response doInBackground(String s) {
                return new JsonHttpApi("", "").POST();
            }

            @Override
            protected void onPostExecute(Response response) {
                super.onPostExecute(response);
                listView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, strs));
            }
        }.execute();
    }
}
