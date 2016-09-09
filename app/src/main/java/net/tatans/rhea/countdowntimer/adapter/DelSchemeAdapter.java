package net.tatans.rhea.countdowntimer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.bean.CountDownBean;
import net.tatans.rhea.countdowntimer.utils.Const;
import net.tatans.rhea.countdowntimer.utils.Util;

import java.util.List;

/**
 * Created by Administrator on 2016/3/30.
 */
public class DelSchemeAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<CountDownBean> al_countDown;
    private Context mContext;
    private CountDownBean bean;
    private TatansDb tdb;

    //ViewHolder静态类
    static class ViewHolder {
        public ImageView icon_time;
        public TextView tv_countdown_time;
    }

    public DelSchemeAdapter(Context context, List<CountDownBean> al_countDown) {
        this.al_countDown = al_countDown;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        tdb = TatansDb.create(Const.CountDownDB);
    }

    @Override
    public int getCount() {
        return al_countDown.size();
    }

    @Override
    public Object getItem(int position) {
        return al_countDown.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //如果缓存convertView为空，则需要创建View
        if (convertView == null) {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.item_list, null);
            holder.icon_time = (ImageView) convertView.findViewById(R.id.icon_time);
            holder.tv_countdown_time = (TextView) convertView.findViewById(R.id.tv_countdown_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.icon_time.setBackgroundResource(R.mipmap.icon_time);
        holder.tv_countdown_time.setText(al_countDown.get(position).getCountDownTime() + "分钟");
        View.OnHoverListener onHoverListener = new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                        Util.interrupt();
                        TatansToast.showAndCancel(al_countDown.get(position).getCountDownTime() +
                                "分钟。播报间隔" + al_countDown.get(position).getIntervalTime() + "分钟。轻点一下来删除");
                }
                return false;
            }
        };
        holder.icon_time.setOnHoverListener(onHoverListener);
        holder.tv_countdown_time.setOnHoverListener(onHoverListener);
        holder.tv_countdown_time.setTextColor(mContext.getResources().getColor(R.color.white));
        return convertView;
    }
}
