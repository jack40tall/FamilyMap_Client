package com.example.familymap.jsmall3.ui;

import android.os.AsyncTask;

import com.example.familymap.jsmall3.model.AllEvents;
import com.example.familymap.jsmall3.model.AllPeople;
import com.example.familymap.jsmall3.net.ServerProxy;
import com.example.familymap.jsmall3.net.requests.LoginOrRegisterRequest;
import com.example.familymap.jsmall3.net.results.LoginOrRegisterResult;


public class LoginAsyncTask extends AsyncTask<LoginOrRegisterRequest, Void, Boolean> {

    ServerProxy serverProxy = new ServerProxy();

    public interface Listener {
        void onError(Error e);
        void onLogin();
        void onSuccess(boolean loginSuccess, String message);
        void onRegister();
        void populateDataCachePeople(AllPeople allPeople, String userID);
        void populateDataCacheEvents(AllEvents allEvents);
    }
    private String message;

    private Listener listener;

    public LoginAsyncTask(Listener l) {listener = l;}


//    TODO: How do I have two different do in backgrounds?
    @Override
    protected Boolean doInBackground(LoginOrRegisterRequest... requests) {
        LoginOrRegisterResult result;

        if(requests[0].isLogin()) {   //Login Request
            result = serverProxy.login(requests[0]);
        }
        else {                        // Register Request
            result = serverProxy.register(requests[0]);
        }

        if (result.isSuccess()) {
            importData(result.getPersonID());
        }
        else {
            message = result.getMessage();
        }
        Boolean loginSuccess = result.isSuccess();

        return loginSuccess;
    }

    private void importData(String userID) {
//        TODO: Get and store authToken
        String authToken = ServerProxy.currAuthToken;
//        Import People
        AllPeople usersTree = serverProxy.getAllPeople();
        listener.populateDataCachePeople(usersTree, userID);

        AllEvents allEvents = serverProxy.getAllEvents();
        listener.populateDataCacheEvents(allEvents);
    }

    protected void onPostExecute(Boolean result) {
        boolean loginSuccess = result;
        listener.onSuccess(loginSuccess, message);
    }
}
