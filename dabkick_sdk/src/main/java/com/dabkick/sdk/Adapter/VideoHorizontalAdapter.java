package com.dabkick.sdk.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoDetail;
import com.dabkick.sdk.DabKickVideoAgent.DabKickVideoManagerAgent;
import com.dabkick.sdk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class VideoHorizontalAdapter extends BaseAdapter {
    Context context;
    int resourceId;
    ArrayList list = new ArrayList();
    public ViewHolder holder;
    Typeface tf;

    public VideoHorizontalAdapter(Context context, int resourceId, ArrayList data, boolean isTVShows){
        this.context = context;
        this.resourceId = resourceId;

        if (data != null)
            this.list = data;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = new ViewHolder();

        View retval = LayoutInflater.from(context).inflate(this.resourceId, null);

        holder.thumbnail = (ImageView) retval.findViewById(R.id.userVideoThumbImageView);
        holder.title = (TextView) retval.findViewById(R.id.userVideoTitleTextView);
        holder.play = (ImageView) retval.findViewById(R.id.playThumb);
        holder.innerLayout = (RelativeLayout)retval.findViewById(R.id.inner_layout);

        holder.dabbyLayout = (RelativeLayout)retval.findViewById(R.id.dabby_layout);
        //holder.runningDabby = (ImageView) retval.findViewById(R.id.running_dabby);
        holder.runningDabby = (ProgressBar) retval.findViewById(R.id.running_dabby);

        DabKickVideoManagerAgent dabKickVideoManagerAgent = DabKickVideoManagerAgent.getInstance();

        // GlobalHandler.startRunningDabbyAnimation(holder.runningDabby);

        DabKickVideoDetail dabKickVideoDetail = (DabKickVideoDetail)getItem(position);

        Log.e("deepak", "getView: " + dabKickVideoDetail.videoTitle);
        try {
            Picasso.with(context).load(dabKickVideoDetail.videoImageURL).placeholder(R.drawable.tv_shows_placeholder_image_v70_xhdpi).resize(90, 60).into(holder.thumbnail);
        }catch (IllegalArgumentException ia){
            ia.printStackTrace();
        }

        holder.title.setText(dabKickVideoDetail.videoTitle);

        //Show dabby confined to the tile
        if(((DabKickVideoDetail) getItem(position)).isVideoSelectedToPlay)
        {
            holder.dabbyLayout.setVisibility(View.VISIBLE);
        }
        else{
            holder.dabbyLayout.setVisibility(View.GONE);
        }

        holder.title.setTypeface(tf);

        return retval;
    }

    public static class ViewHolder {
        ImageView thumbnail;
        //ImageView runningDabby;
        ProgressBar runningDabby;
        TextView title;
        public static ImageView play;
        RelativeLayout innerLayout, dabbyLayout;
    }
}
