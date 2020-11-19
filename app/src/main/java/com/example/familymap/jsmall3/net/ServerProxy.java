package com.example.familymap.jsmall3.net;

import android.util.Log;

import com.example.familymap.jsmall3.model.AllEvents;
import com.example.familymap.jsmall3.model.AllPeople;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.net.requests.LoginOrRegisterRequest;
import com.example.familymap.jsmall3.net.results.LoginOrRegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

public class ServerProxy {

    public static String serverHostName;   // IP address. these both come from UI
    public static int serverPortNumber;
    public static String currAuthToken;

    public LoginOrRegisterResult login(LoginOrRegisterRequest request) {
//        Serialize request as JSON string
//        Make HTTP request to server
        LoginOrRegisterResult result = null;

        try {
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/user/login");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);	// There is a request body

//            // Add an auth token to the request in the HTTP "Authorization" header
//            http.addRequestProperty("Authorization", "afj232hj2332");

            // Connect to the server and send the HTTP request
            http.connect();

//            Convert request object to JSON
            String reqData = serialize(http, request);

            // Get the output stream containing the HTTP request body
            OutputStream reqBody = http.getOutputStream();
            // Write the JSON data to the request body
            writeString(reqData, reqBody);
            // Close the request body output stream, indicating that the
            // request is complete
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Log.d("SUCCESS", "Login Result Received...");
            }
            else {
//                Log.d("ERROR", "Login Result Error");
            }
            result = deserialize(http, LoginOrRegisterResult.class);
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            result = new LoginOrRegisterResult("Invalid Credentials, Please try again", false);
        }
        finally {
            if (result.isSuccess()) {
                currAuthToken = result.getAuthToken();
                DataCache.loginSuccess = true;
            } else {
                DataCache.loginSuccess = false;
            }
            return result;
        }
    }


    public LoginOrRegisterResult register(LoginOrRegisterRequest request) {
        LoginOrRegisterResult result = null;

        try {
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);	// There is a request body

            http.connect();

            String reqData = serialize(http, request);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);

            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Log.d("SUCCESS", "Register Result Received...");
            }
            else {
//                Log.d("ERROR", "Register Result Error");
            }
            result = deserialize(http, LoginOrRegisterResult.class);
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            result = new LoginOrRegisterResult("Username already taken", false);
        }
        finally {
            if (result.isSuccess()) {
                currAuthToken = result.getAuthToken();
                DataCache.loginSuccess = true;
            } else {
                DataCache.loginSuccess = false;
            }
            return result;
        }
    }

    public AllPeople getAllPeople() {

        AllPeople familyPersons = null;

        try {
            URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);	// There is not a request body

            http.addRequestProperty("Authorization", currAuthToken);

            http.connect();

//            http.getInputStream().close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Log.d("SUCCESS", "All People Received...");
            }
            else {
//                Log.d("ERROR", "GetAllPeople Error");
            }
            familyPersons = deserialize(http, AllPeople.class);
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
        }
        return familyPersons;
    }


//    GetAllEvents
public AllEvents getAllEvents() {

    AllEvents allEvents = null;

    try {
        URL url = new URL("http://" + serverHostName + ":" + serverPortNumber + "/event");

        HttpURLConnection http = (HttpURLConnection)url.openConnection();

        http.setRequestMethod("GET");
        http.setDoOutput(false);	// There is not a request body

        http.addRequestProperty("Authorization", currAuthToken);

//        TODO: Do I need this connect?
        http.connect();

//        http.getInputStream().close();

        if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
//            Log.d("SUCCESS", "All Events Received...");
        }
        else {
//            Log.d("ERROR", "GetAllEvents Error");
        }
        allEvents = deserialize(http, AllEvents.class);
    }
    catch (IOException e) {
        // An exception was thrown, so display the exception's stack trace
        e.printStackTrace();
    }
    return allEvents;
}


    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

//    From Object to JSON
    public String serialize(HttpURLConnection http, Object object) throws IOException {

        Gson gson = new Gson();

        String respData = gson.toJson(object);

        return respData;
    }

//    From JSON to Object
    public <T> T deserialize(HttpURLConnection http, Class<T> returnType) throws IOException {
        InputStream reqBody = http.getInputStream();
        // Read JSON string from the input stream
//                Gson gson = new Gson();
        String reqData = readString(reqBody);
//        System.out.println(reqData);
        return (new Gson()).fromJson(reqData, returnType);
    }

}
