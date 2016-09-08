package net.tatans.rhea.countdowntimer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.coeus.network.tools.TatansDb;
import net.tatans.coeus.network.tools.TatansToast;
import net.tatans.rhea.countdowntimer.R;
import net.tatans.rhea.countdowntimer.bean.MassageTimeBean;
import net.tatans.rhea.countdowntimer.bean.SelectResultBean;
import net.tatans.rhea.countdowntimer.utils.Const;

import java.util.List;

/**
 * 用于显示按摩统计的适配器
 * Created by cly on 2016/9/2.
 */

public class MonthDayAdapter extends BaseAdapter {
    private List<SelectResultBean> alMassage;
    private Context mContext;
    private int mFlag;
    private TatansDb tdb;

    private class ViewHolder {
        TextView tvTime;
        TextView tvDuration;
        ImageView imgDel;
    }

    public MonthDayAdapter(Context context, List<SelectResultBean> list, int flag) {
        this.alMassage = list;
        this.mContext = context;
        this.mFlag = flag;
        tdb = TatansDb.create(Const.CountDownDB);
    }

    @Override
    public int getCount() {
        return alMassage.size();
    }

    @Override
    public Object getItem(int position) {
        return alMassage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.month_day_item, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.month);
            holder.tvDuration = (TextView) convertView.findViewById(R.id.duration);
            holder.imgDel = (ImageView) convertView.findViewById(R.id.img_del);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int duration = alMassage.get(position).getDuration();
        String time = (duration / 60 == 0 ? "" : duration / 60 + mContext.getString(R.string.hour))
                + (duration % 60 == 0 ? "" : duration % 60 + mContext.getString(R.string.minute));
        holder.tvDuration.setText(time);
        holder.tvTime.setText(alMassage.get(position).getTime());
        if (mFlag == Const.ISDETAIL) {
            holder.imgDel.setImageResource(R.mipmap.btn_delete_time);
            holder.imgDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<MassageTimeBean> bean = tdb.findAllByWhere(MassageTimeBean.class,
                            "id = " + alMassage.get(position).getId());
                    tdb.delete(bean.get(0));
                    alMassage.remove(position);
                    TatansToast.showAndCancel("删除成功");
                    notifyDataSetChanged();
                }
            });
        }
        return convertView;
    }
}
