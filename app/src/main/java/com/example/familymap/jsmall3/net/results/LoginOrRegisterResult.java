package com.example.familymap.jsmall3.net.results;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class LoginOrRegisterResult {
    String authToken;
    String userName;
    String personID;
    String message;
    boolean success;

    public LoginOrRegisterResult(String authToken, String userName, String personID, boolean success) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
        this.success = success;
    }

    public LoginOrRegisterResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public LoginOrRegisterResult(String errorMessage) {
        message = errorMessage;
        this.success = false;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginOrRegisterResult that = (LoginOrRegisterResult) o;
        return success == that.success &&
                Objects.equals(authToken, that.authToken) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(personID, that.personID) &&
                Objects.equals(message, that.message);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(authToken, userName, personID, message, success);
    }
}
