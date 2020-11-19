package com.example.familymap.jsmall3.net.requests;

public class LoginOrRegisterRequest {
    private String userName;
    private String password;
    private boolean isLogin;  //true is loginrequest, false is register request
    private String email;
    private String firstName;
    private String lastName;
    private String gender;

    public LoginOrRegisterRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.isLogin = true;
    }

    public LoginOrRegisterRequest(String userName, String password, String email, String firstName, String lastName, String gender) {
        this.userName = userName;
        this.password = password;
        this.isLogin = false;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public LoginOrRegisterRequest() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
