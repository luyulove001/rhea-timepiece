package net.tatans.rhea.countdowntimer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.utils.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.CountDownBean;

/**
 * Created by Administrator on 2016/3/29.
 */
public class DeleteActivity extends TatansActivity implements AdapterView.OnItemClickListener {
    ListView lv_del_countdown;
    private CountDownBean bean;
    private TatansDb tdb;
    private List<CountDownBean> al_countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_list);
        tdb = TatansDb.create(Const.CountDown_DB);
        al_countDown = tdb.findAll(CountDownBean.class);
        lv_del_countdown = (ListView) findViewById(R.id.lv_del_countdown);
        lv_del_countdown.setAdapter(new SimpleAdapter(DeleteActivity.this,getData(),R.layout.item_list,
                new String[]{"icon_time","tv_countdown_time"}, new int[]{R.id.icon_time, R.id.tv_countdown_time}));
        lv_del_countdown.setOnItemClickListener(this);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map ;
        for (int i = 0; i < al_countDown.size(); i++) {
            map = new HashMap<>();
            map.put("icon_time", R.mipmap.icon_time);
            map.put("tv_countdown_time", al_countDown.get(i).getCountDownTime() + "分钟");
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bean = al_countDown.get(position);
        tdb.delete(bean);
        al_countDown = tdb.findAll(CountDownBean.class);
        lv_del_countdown.setAdapter(new SimpleAdapter(DeleteActivity.this, getData(), R.layout.item_list,
                new String[]{"icon_time", "tv_countdown_time"}, new int[]{R.id.icon_time, R.id.tv_countdown_time}));
        TatansToast.showShort("删除成功");
    }
}
