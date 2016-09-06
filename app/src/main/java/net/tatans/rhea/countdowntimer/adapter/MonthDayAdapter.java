package net.tatans.rhea.countdowntimer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.bean.SelectResultBean;

import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */

public class MonthDayAdapter extends BaseAdapter {
    private List<SelectResultBean> al_massage;
    private Context mContext;
    private int duration;//某段时间按摩时间的总和 如7月按摩时间

    static class ViewHolder {
       public TextView tvTime;
       public TextView tvDuration;
    }

    public MonthDayAdapter(Context context, List<SelectResultBean> list) {
        this.al_massage = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return al_massage.size();
    }

    @Override
    public Object getItem(int position) {
        return al_massage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.month_day_item, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.month);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.duration);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        duration = al_massage.get(position).getDuration();
        String time = (duration / 60 == 0 ? "" : duration / 60 + mContext.getString(R.string.hour))
                + (duration % 60 == 0 ? "" : duration % 60 + mContext.getString(R.string.minute));
        holder.tvDuration.setText(time);
        holder.tvTime.setText(al_massage.get(position).getTime());
        return convertView;
    }
}
