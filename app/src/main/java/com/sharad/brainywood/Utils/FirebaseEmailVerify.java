package com.sharad.brainywood.Utils;

public class FirebaseEmailVerify {

    String user_id, user_verify;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_verify() {
        return user_verify;
    }

    public void setUser_verify(String user_verify) {
        this.user_verify = user_verify;
    }

    public FirebaseEmailVerify() {
    }

    public FirebaseEmailVerify(String user_id, String user_verify) {
        this.user_id = user_id;
        this.user_verify = user_verify;
    }
}
