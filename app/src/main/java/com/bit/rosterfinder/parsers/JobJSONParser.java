package com.bit.rosterfinder.parsers;

        import java.text.DateFormat;
        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;

        import com.bit.rosterfinder.model.Job;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;
/**
 * Created by Arvind on 4/06/2015.
 */
public class JobJSONParser {

    public static String DateFormatter(String dateInString, SimpleDateFormat formatter) {

        try {
            Date date = formatter.parse(dateInString);
            return formatter.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String TimeFormatter(String timeInString, SimpleDateFormat formatter) {

        try {
            DateFormat timeformatter;
            timeformatter = DateFormat.getTimeInstance();        // time only

            return timeformatter.format(formatter.parse(timeInString));

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Job> parseFeed(String content) {

        try {
            JSONArray ar = new JSONArray(content);

            List<Job> jobList = new ArrayList<>();

            for (int i = 0; i < ar.length(); i++) {

                JSONObject obj = ar.getJSONObject(i);


                Job job = new Job();

                job.setJobid(obj.getString("jobid"));
                job.setFname(obj.getString("fname"));
                job.setLname(obj.getString("lname"));
                job.setMphone(obj.getString("mphone"));
                job.setHphone(obj.getString("hphone"));
                job.setAddress(obj.getString("address"));

                SimpleDateFormat formatter;
                String datetimeInString;

                //Read and Set date
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                datetimeInString = obj.getString("sch_start_date");
                job.setSch_start_date(DateFormatter(datetimeInString, formatter));


                //Read and Set time
                formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                datetimeInString = obj.getString("sch_start_time");
                job.setSch_start_time(TimeFormatter(datetimeInString, formatter));

                jobList.add(job);
            }

            return jobList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}



