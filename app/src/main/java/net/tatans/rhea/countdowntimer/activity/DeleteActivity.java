package net.tatans.rhea.countdowntimer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.tatans.coeus.network.tools.TatansActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.coeus.network.view.ViewInject;
import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.adapter.DelSchemeAdapter;
import net.tatans.rhea.countdowntimer.bean.CountDownBean;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Util;

import java.util.List;

/**
 * Created by Administrator on 2016/3/29.
 */
public class DeleteActivity extends TatansActivity implements AdapterView.OnItemClickListener, OnClickListener {
    ListView lv_del_countdown;
    @ViewInject(id = R.id.empty_del, click = "onClick")
    TextView tv_empty;
    private CountDownBean bean;
    private TatansDb tdb;
    private List<CountDownBean> al_countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.del_list);
        tdb = TatansDb.create(Const.CountDownDB);
        al_countDown = tdb.findAll(CountDownBean.class);
        lv_del_countdown = (ListView) findViewById(R.id.lv_del_countdown);
        lv_del_countdown.setAdapter(new DelSchemeAdapter(DeleteActivity.this, al_countDown));
        if (al_countDown.size() == 0) tv_empty.setText("没有倒计时");
        lv_del_countdown.setOnItemClickListener(this);
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
        Util.interrupt();
        TatansToast.showAndCancel("删除成功");
        lv_del_countdown.setAdapter(new DelSchemeAdapter(DeleteActivity.this, al_countDown));
        if (al_countDown.size() == 0) tv_empty.setText("没有倒计时");
        else tv_empty.setText("");
    }

    @Override
    public void onClick(View v) {
        if (al_countDown.size() == 0) TatansToast.showAndCancel("没有倒计时");
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
