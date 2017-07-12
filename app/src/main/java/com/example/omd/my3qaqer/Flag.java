package com.example.omd.my3qaqer;

/**
 * Created by Delta on 17/06/2017.
 */

public class Flag {

    public static boolean flag              = false;
    public static boolean flag_notification = false;
    public static boolean user_notf_readed  = false;
    public static boolean notfReaded        = false;
    public static boolean locflag           = false;
    public static boolean delete_allposts   = false;
    public static boolean delete_post       = false;

    public static boolean isDelete_post() {
        return delete_post;
    }

    public static void setDelete_post(boolean delete_post) {
        Flag.delete_post = delete_post;
    }

    public static boolean isDelete_allposts() {
        return delete_allposts;
    }

    public static void setDelete_allposts(boolean delete_allposts) {
        Flag.delete_allposts = delete_allposts;
    }



    public static boolean isLocflag() {
        return locflag;
    }
    public static void setLocflag(boolean locflag) {
        Flag.locflag = locflag;
    }

    public static void setUser_notf_readed(boolean user_notf_readed) {
        Flag.user_notf_readed = user_notf_readed;
    }
    public static boolean isUser_notf_readed()
    {
        return user_notf_readed;
    }



    public static boolean isNotfReaded() {
        return notfReaded;
    }
    public static void setNotfReaded(boolean notfReaded)
    {
        Flag.notfReaded = notfReaded;
    }


    public static boolean isFlag_notification() {
        return flag_notification;
    }
    public static void setFlag_notification(boolean flag_notification) {
        Flag.flag_notification = flag_notification;
    }

    public static boolean isFlag() {
        return flag;
    }
    public static void setFlag(boolean flag) {
        Flag.flag = flag;
    }

}
