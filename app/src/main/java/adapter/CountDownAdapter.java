package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.tatans.rhea.countdowntimer.R;

import java.util.List;

import bean.CountDownBean;

/**
 * Created by Administrator on 2016/3/24.
 */
public class CountDownAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private List<CountDownBean> al_countDown;

    //ViewHolder静态类
    static class ViewHolder {
        public ImageView icon_time;
        public TextView tv_countdown_time;
    }

    public CountDownAdapter(Context context, List<CountDownBean> al_countDown) {
        this.al_countDown = al_countDown;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
        holder.tv_countdown_time.setText(al_countDown.get(position).getCountDownTime());
        return convertView;
    }
}
