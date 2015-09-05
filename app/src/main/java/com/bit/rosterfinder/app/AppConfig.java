package com.bit.rosterfinder.app;

/**
 * Created by Arvind on 1/06/2015.
 */
public class AppConfig {
    //php login script

    //localhost :
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String LOGIN_URL = "http://xxx.xxx.x.x:8080/webservice/<filename>.php";

    //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/<filename>.php";

    // Server user login url
    public static String URL_LOGIN = "http://192.168.0.3:8080/webservice/login.php";
    //public static String URL_LOGIN = "http://172.19.29.203:8080/webservice/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://192.168.0.3:8080/webservice/register.php";
    //public static String URL_REGISTER = "http://172.19.29.203:8080/webservice/register.php";

    // Server user jobs url
    public static String URL_JOBS = "http://192.168.0.3:8080/bitWS_localhost/get_all_jobs.php";
    //public static String URL_JOBS = "http://172.19.29.203:8080/bitWS_localhost/get_all_jobs.php";

    // single job url
    public static final String URL_JOB_DETAILS = "http://192.168.0.3:8080/bitWS_localhost/get_job_details.php";
    //public static final String URL_JOB_DETAILS = "http://172.19.29.203:8080/bitWS_localhost/get_job_details.php";

    // url to update job
    public static final String URL_UPDATE_JOB = "http://192.168.0.3:8080/bitWS_localhost/update_job.php";
    //public static final String URL_UPDATE_JOB = "http://172.19.29.203:8080/bitWS_localhost/update_job.php";

    // url to delete job
    public static final String URL_DELETE_JOB = "http://192.168.0.3:8080/bitWS_localhost/delete_job.php";
    //public static final String URL_DELETE_JOB = "http://172.19.29.203:8080/bitWS_localhost/delete_job.php";

}
