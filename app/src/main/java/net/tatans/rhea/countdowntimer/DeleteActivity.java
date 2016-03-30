package net.tatans.rhea.countdowntimer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.rhea.countdowntimer.adapter.DelSchemeAdapter;
import net.tatans.rhea.countdowntimer.bean.CountDownBean;
import net.tatans.rhea.countdowntimer.utils.Const;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        lv_del_countdown.setAdapter(new DelSchemeAdapter(DeleteActivity.this, al_countDown));
//        lv_del_countdown.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e("antony", "删除成功");
        bean = al_countDown.get(position);
        tdb.delete(bean);
        al_countDown = tdb.findAll(CountDownBean.class);
        TatansToast.showShort("删除成功");
        lv_del_countdown.setAdapter(new DelSchemeAdapter(DeleteActivity.this, al_countDown));
    }
}
