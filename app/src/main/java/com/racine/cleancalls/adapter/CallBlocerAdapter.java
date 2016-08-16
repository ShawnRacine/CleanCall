package com.racine.cleancalls.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.racine.cleancalls.R;
import com.racine.cleancalls.db.CallBlocker;
import com.racine.cleancalls.model.BlockType;
import com.racine.cleancalls.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Shawn Racine.
 */
public class CallBlocerAdapter extends BaseAdapter<CallBlocker> {
    private SimpleDateFormat sdf;

    public CallBlocerAdapter(Context context, List<CallBlocker> list) {
        super(context, list);
        sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_callblocker, null);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            holder.tv_remark = (TextView) convertView.findViewById(R.id.tv_remark);
            holder.iv_mark = (ImageView) convertView.findViewById(R.id.iv_mark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CallBlocker model = list.get(position);

        if (!StringUtils.isNullOrEmpty(model.date)) {
            String date = sdf.format(new Date(model.date));
            holder.tv_date.setText(date.substring(11, 16));
        }

        holder.tv_phone.setText(model.phone);

        switch (model.type) {
            case BlockType.OTHER:
                holder.tv_type.setText(R.string.other);
                break;
            case BlockType.PROMOTION:
                holder.tv_type.setText(R.string.promotion);
                break;
            case BlockType.FRAUD:
                holder.tv_type.setText(R.string.fraud);
                break;
            case BlockType.ESTATE_AGENCY:
                holder.tv_type.setText(R.string.estate_agency);
                break;
            case BlockType.PRIVATE_LOAN:
                holder.tv_type.setText(R.string.private_loan);
                break;
            case BlockType.TRAINING_AGENCY:
                holder.tv_type.setText(R.string.training_agency);
                break;
            default:
                break;
        }

        holder.tv_remark.setText(model.remark);

        return convertView;
    }

    class ViewHolder {
        TextView tv_date;
        TextView tv_phone;
        TextView tv_type;
        TextView tv_remark;
        ImageView iv_mark;
    }
}
