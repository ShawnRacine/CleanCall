package com.racine.cleancalls.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.racine.cleancalls.R;
import com.racine.cleancalls.adapter.CallBlocerAdapter;
import com.racine.cleancalls.db.CallBlocker;
import com.racine.cleancalls.db.CallBlockerDAO;
import com.racine.cleancalls.net.HttpApi.MapHttpApi;
import com.racine.cleancalls.net.HttpApi.bean.Response;
import com.racine.cleancalls.net.ThreadTask.MapThreadTask;
import com.racine.cleancalls.net.ThreadTask.VoidThreadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shawn Racine.
 */
public class CallBlockerFragment extends BaseFragment {
    private CallBlockerDAO callBlockerDAO;
    private ListView listView;
    private List<CallBlocker> mList;
    private CallBlocerAdapter adapter;
    private static final String[] strs = new String[]{
            "10086", "10086", "10086", "10086", "10086"
    };

    @Override
    protected void onCreateView() {
        setContentView(R.layout.callblocker);
        callBlockerDAO = new CallBlockerDAO(mContext);
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
        new VoidThreadTask(){

            @Override
            protected Void doInBackground(Void aVoid) {
                mList = callBlockerDAO.queryAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter = new CallBlocerAdapter(mContext, mList);
                listView.setAdapter(adapter);
            }
        }.execute();

        new MapThreadTask() {
            @Override
            protected Map<String, String> onPreExecute() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", "13501087144");
                params.put("password", "a123456");
                return params;
            }

            @Override
            protected Response doInBackground(Map<String, String> stringStringMap) {
                return new MapHttpApi("http://agent.house.ifeng.com/api/login/validate", stringStringMap).POST();
            }

            @Override
            protected void onPostExecute(Response response) {
                super.onPostExecute(response);

            }
        };
    }

}
