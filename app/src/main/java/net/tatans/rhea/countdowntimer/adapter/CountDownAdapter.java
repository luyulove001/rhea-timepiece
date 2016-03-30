package net.tatans.rhea.countdowntimer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.rhea.countdowntimer.CountDownTimerActivity;
import net.tatans.rhea.countdowntimer.R;

import java.util.List;

import net.tatans.rhea.countdowntimer.bean.CountDownBean;

/**
 * Created by Administrator on 2016/3/24.
 */
public class CountDownAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<CountDownBean> al_countDown;
    private Context mContext;

    //ViewHolder静态类
    static class ViewHolder {
        public ImageView icon_time;
        public TextView tv_countdown_time;
    }

    public CountDownAdapter(Context context, List<CountDownBean> al_countDown) {
        this.al_countDown = al_countDown;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
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
        holder.tv_countdown_time.setContentDescription(al_countDown.get(position).getCountDownTime() +
                "分钟。播报间隔" + al_countDown.get(position).getIntervalTime() + "分钟。轻点一下开始倒计时");
        holder.tv_countdown_time.setTextColor(mContext.getResources().getColor(R.color.white));
        convertView.setOnClickListener(new OnClickListenerImpl(position));
        return convertView;
    }

    private class OnClickListenerImpl implements View.OnClickListener {
        private int mPosition;

        OnClickListenerImpl(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext,CountDownTimerActivity.class);
            intent.putExtra("countDown_scheme", al_countDown.get(mPosition));
            mContext.startActivity(intent);
        }
    }
}
