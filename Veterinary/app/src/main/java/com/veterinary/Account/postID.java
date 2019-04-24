package com.veterinary.Account;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class postID {

    @Exclude
    public String postID;
    public <T extends postID> T withId(@NonNull final String id){
        this.postID=id;
        return (T)this;
    }
}
