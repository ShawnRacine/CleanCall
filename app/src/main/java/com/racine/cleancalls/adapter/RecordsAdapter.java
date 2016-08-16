package com.racine.cleancalls.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.racine.cleancalls.R;
import com.racine.cleancalls.model.RecordsModel;
import com.racine.cleancalls.utils.StringUtils;

import java.util.List;

/**
 * @author Shawn Racine.
 */
public class RecordsAdapter extends BaseAdapter<RecordsModel> {

    public RecordsAdapter(Context context, List<RecordsModel> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_records, null);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tv_displayname = (TextView) convertView.findViewById(R.id.tv_displayname);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
            holder.iv_outcalls = (ImageView) convertView.findViewById(R.id.iv_outcalls);
            holder.iv_mark = (ImageView) convertView.findViewById(R.id.iv_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RecordsModel model = list.get(position);

        holder.tv_date.setText(model.date.substring(11, 16));

        if (StringUtils.isNullOrEmpty(model.name)) {
            holder.tv_displayname.setText(model.phone);
            holder.tv_phone.setVisibility(View.GONE);
        } else {
            holder.tv_displayname.setText(model.name);
            holder.tv_phone.setVisibility(View.VISIBLE);
            holder.tv_phone.setText(model.phone);
        }

        holder.tv_remark.setText(model.addr);

        if ("2".equals(model.type)) {
            holder.iv_outcalls.setVisibility(View.VISIBLE);
        } else {
            holder.iv_outcalls.setVisibility(View.GONE);
        }

        if ("3".equals(model.type)) {
            holder.tv_displayname.setTextColor(context.getResources().getColor(R.color.RED));
        } else {
            holder.tv_displayname.setTextColor(context.getResources().getColor(R.color.GRAY));
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv_date;
        TextView tv_displayname;
        TextView tv_phone;
        TextView tv_remark;
        ImageView iv_outcalls;
        ImageView iv_mark;
    }
}
