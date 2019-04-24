package com.veterinary.Account;

import java.util.Date;

/**
 * Created by sikanga on 03/03/2019.
 */

public class ApproveList extends postID {
    private String user_ID,mycounty,fullname;
    private Date timeStamp;

    public ApproveList() {

    }

    public ApproveList(String user_ID, String mycounty, String fullname, Date timeStamp) {
        this.user_ID = user_ID;
        this.mycounty = mycounty;
        this.fullname = fullname;
        this.timeStamp = timeStamp;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getMycounty() {
        return mycounty;
    }

    public void setMycounty(String mycounty) {
        this.mycounty = mycounty;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
