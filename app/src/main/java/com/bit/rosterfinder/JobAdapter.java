package com.bit.rosterfinder;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bit.rosterfinder.model.Job;

public class JobAdapter extends ArrayAdapter<Job> {

    private Context context;
    private List<Job> jobList;

    public JobAdapter(Context context, int resource, List<Job> objects) {
        super(context, resource, objects);
        this.context = context;
        this.jobList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_job, parent, false);

        //Display job name in the TextView widget
        Job job = jobList.get(position);

        //TextView tv0 = (TextView) view.findViewById(R.id.hiddenid);
        //tv0.setText(job.getJobid());


        TextView tv1 = (TextView) view.findViewById(R.id.textView1);
        tv1.setText(job.getFname() + " " + job.getLname());


        TextView tv2 = (TextView) view.findViewById(R.id.textView2);
        tv2.setText(job.getMphone() + " " + job.getHphone());


        TextView tv3 = (TextView) view.findViewById(R.id.textView3);
        tv3.setText("Address: " + job.getAddress());

        TextView tv4 = (TextView) view.findViewById(R.id.textView4);
        tv4.setText("Date: " + job.getSch_start_date() + " \nTime: " +  job.getSch_start_time() + " \nJob#" + job.getJobid());


        return view;
    }

}
