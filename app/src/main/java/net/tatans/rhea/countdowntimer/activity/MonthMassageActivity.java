package net.tatans.rhea.countdowntimer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.tatans.coeus.db.sqlite.CursorUtils;
import net.tatans.coeus.db.sqlite.DbModel;
import net.tatans.coeus.network.tools.BaseActivity;
import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.adapter.MonthDayAdapter;
import net.tatans.rhea.countdowntimer.bean.MassageTimeBean;
import net.tatans.rhea.countdowntimer.bean.SelectResultBean;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.network.view.ContentView;
import net.tatans.rhea.network.view.ViewIoc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 按摩统计界面
 * Created by cly on 2016/9/2.
 */
@ContentView(R.layout.month_day)
public class MonthMassageActivity extends BaseActivity {
    @ViewIoc(R.id.month_day)
    private ListView massageTimeList;
    @ViewIoc(R.id.empty_massage)
    private TextView tvEmpty;
    private TatansDb tdb;
    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tdb = TatansDb.create(Const.CountDownDB);
        c = Calendar.getInstance();
        tvEmpty.setContentDescription("空白区域");
    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (getIntent().getIntExtra(Const.ToMassageActivity, 0)) {
            case Const.ISMONTH:
                initMonthView();
                break;
            case Const.ISDAILY:
                initDailyView();
                break;
            case Const.ISDETAIL:
                initDetailView();
                break;
            default:
                initMonthView();
                Log.d("antony", "default");
                break;
        }
    }

    /**
     * 每日详情列表
     */
    private void initDetailView() {
        setTitle("每日详情列表");
        List<DbModel> lsDb = tdb.findDbModelListBySQL("select * from MassageTime where month = " +
                (getIntent().getIntExtra("month", 0)) + " and day = " + (getIntent().getIntExtra("day", 0)));
        final List<MassageTimeBean> lsMtb = new ArrayList<>();
        for (int i = 0; i < lsDb.size(); i++) {
            lsMtb.add((MassageTimeBean) CursorUtils.dbModel2Entity(lsDb.get(i), MassageTimeBean.class));
        }
        List<SelectResultBean> lsSrb = new ArrayList<>();
        SelectResultBean srb;
        for (int i = 0; i < lsMtb.size(); i++) {
            srb = new SelectResultBean();
            srb.setId(lsMtb.get(i).getId());
            srb.setTime(lsMtb.get(i).getStartTime());
            srb.setDuration(lsMtb.get(i).getDuration());
            lsSrb.add(srb);
        }
        massageTimeList.setAdapter(new MonthDayAdapter(MonthMassageActivity.this, lsSrb, Const.ISDETAIL));
    }

    /**
     * 每日统计列表
     */
    private void initDailyView() {
        setTitle("每日统计列表");
        List<DbModel> lsDb = tdb.findDbModelListBySQL("select id, year, month, day, startTime," +
                " sum(duration) as duration from MassageTime where month = " +
                (c.get(Calendar.MONTH) + 1 - getIntent().getIntExtra("position", 0)) +
                " group by year, month, day order by day DESC");
        final List<MassageTimeBean> lsMtb = new ArrayList<>();
        for (int i = 0; i < lsDb.size(); i++) {
            lsMtb.add((MassageTimeBean) CursorUtils.dbModel2Entity(lsDb.get(i), MassageTimeBean.class));
        }
        List<SelectResultBean> lsSrb = new ArrayList<>();
        SelectResultBean srb;
        for (int i = 0; i < lsMtb.size(); i++) {
            srb = new SelectResultBean();
            srb.setId(lsMtb.get(i).getId());
            srb.setTime(lsMtb.get(i).getMonth() + "月" + lsMtb.get(i).getDay() + "日");
            srb.setDuration(lsMtb.get(i).getDuration());
            lsSrb.add(srb);
        }
        massageTimeList.setAdapter(new MonthDayAdapter(MonthMassageActivity.this, lsSrb, Const.ISDAILY));
        massageTimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MonthMassageActivity.this, MonthMassageActivity.class);
                i.putExtra(Const.ToMassageActivity, Const.ISDETAIL);
                i.putExtra("month", lsMtb.get(position).getMonth());
                i.putExtra("day", lsMtb.get(position).getDay());
                startActivity(i);
            }
        });
    }

    /**
     * 每月统计列表，查询数据库并将数据放入list
     */
    private void initMonthView() {
        setTitle("月份列表");
        final List<DbModel> lsDb = tdb.findDbModelListBySQL("select id, year, month, day, startTime, sum(duration)" +
                " as duration from MassageTime group by year, month order by month DESC");
        List<MassageTimeBean> lsMtb = new ArrayList<>();
        for (int i = 0; i < lsDb.size(); i++) {
            lsMtb.add((MassageTimeBean) CursorUtils.dbModel2Entity(lsDb.get(i), MassageTimeBean.class));
        }

        final List<SelectResultBean> lsSrb = new ArrayList<>();
        SelectResultBean srb;
        for (int i = 0; i < lsMtb.size(); i++) {
            if (c.get(Calendar.YEAR) == lsMtb.get(i).getYear()) {
                if ((c.get(Calendar.MONTH) + 1) == lsMtb.get(i).getMonth()) {
                    srb = new SelectResultBean();
                    srb.setId(lsMtb.get(i).getId());
                    srb.setTime("本月");
                    srb.setDuration(lsMtb.get(i).getDuration());
                } else if (c.get(Calendar.MONTH) == lsMtb.get(i).getMonth()) {
                    srb = new SelectResultBean();
                    srb.setTime("上月");
                    srb.setDuration(lsMtb.get(i).getDuration());
                } else {
                    srb = new SelectResultBean();
                    srb.setTime(lsMtb.get(i).getMonth() + "月");
                    srb.setDuration(lsMtb.get(i).getDuration());
                }
                lsSrb.add(srb);
            }
        }
        massageTimeList.setAdapter(new MonthDayAdapter(MonthMassageActivity.this, lsSrb, Const.ISMONTH));
        massageTimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MonthMassageActivity.this, MonthMassageActivity.class);
                i.putExtra(Const.ToMassageActivity, Const.ISDAILY);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
    }
}
