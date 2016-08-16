package com.racine.cleancalls.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.racine.cleancalls.R;
import com.racine.cleancalls.db.CallBlocker;
import com.racine.cleancalls.db.CallBlockerDAO;
import com.racine.cleancalls.net.HttpApi.MapHttpApi;
import com.racine.cleancalls.net.HttpApi.bean.Response;
import com.racine.cleancalls.net.ThreadTask.MapThreadTask;
import com.racine.cleancalls.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Shawn Racine.
 */
public class CallBlockerFragment extends BaseFragment {
    private CallBlockerDAO callBlockerDAO;
    private ListView listView;
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
                List<CallBlocker> list = callBlockerDAO.queryAll();
                if (list != null && list.size() > 0) {
                    if (!StringUtils.isNullOrEmpty(list.get(0).phone)) {
                        strs[0] = list.get(0).phone;
                    }
                }
                listView.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, strs));
            }
        }.execute();
    }

}
