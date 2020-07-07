package com.mmc.lipsyncconnect.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mmc.lipsyncconnect.R;

/******************************************************************************
 Copyright (c) 2020. MakersMakingChange.com (info@makersmakingchange.com)
 Developed by : Milad Hajihassan (milador)
 ******************************************************************************/

public class MappingAdapter extends ArrayAdapter<String> {

    String[] spinnerMainTitles;
    String[] spinnerSecondaryTitles;
    int[] spinnerImages;
    Context mContext;

    public MappingAdapter(@NonNull Context context, String[] mainTitles, String[] secondaryTitles, int[] images) {
        super(context, R.layout.mapping_spinner_row);
        this.spinnerMainTitles = mainTitles;
        this.spinnerSecondaryTitles = secondaryTitles;
        this.spinnerImages = images;
        this.mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return spinnerMainTitles.length;
    }
    @Override
    public String getItem(int i) {
        return spinnerMainTitles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.mapping_spinner_row, parent, false);
            mViewHolder.mImage = (ImageView) convertView.findViewById(R.id.mappingSpinnerRowImageView);
            mViewHolder.mMainTitle = (TextView) convertView.findViewById(R.id.mappingSpinnerRowMainTitleTextView);
            mViewHolder.mSecondaryTitle = (TextView) convertView.findViewById(R.id.mappingSpinnerRowSecondaryTitleTextView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mImage.setImageResource(spinnerImages[position]);
        mViewHolder.mMainTitle.setText(spinnerMainTitles[position]);
        mViewHolder.mSecondaryTitle.setText(spinnerSecondaryTitles[position]);

        return convertView;
    }

    private static class ViewHolder {
        ImageView mImage;
        TextView mMainTitle;
        TextView mSecondaryTitle;
    }
}
