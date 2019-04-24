package com.veterinary.Account;

import java.util.Date;

/**
 * Created by sikanga on 11/03/2019.
 */

public class IssueList extends postID{
    private String imageUrl,myuser_id,issue,reply,vet_id;
    private Date timeStamp;

    public IssueList() {
    }


    public IssueList(String imageUrl, String myuser_id, String issue, String reply, String vet_id, Date timeStamp) {
        this.imageUrl = imageUrl;
        this.myuser_id = myuser_id;
        this.issue = issue;
        this.reply = reply;
        this.vet_id = vet_id;
        this.timeStamp = timeStamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMyuser_id() {
        return myuser_id;
    }

    public void setMyuser_id(String myuser_id) {
        this.myuser_id = myuser_id;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getVet_id() {
        return vet_id;
    }

    public void setVet_id(String vet_id) {
        this.vet_id = vet_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
