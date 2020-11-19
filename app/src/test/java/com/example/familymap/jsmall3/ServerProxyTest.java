package com.example.familymap.jsmall3;

import android.util.Log;

import com.example.familymap.jsmall3.model.AllEvents;
import com.example.familymap.jsmall3.model.AllPeople;
import com.example.familymap.jsmall3.net.ServerProxy;
import com.example.familymap.jsmall3.net.requests.LoginOrRegisterRequest;
import com.example.familymap.jsmall3.net.results.LoginOrRegisterResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ServerProxyTest {

    LoginOrRegisterRequest loginRequest;
    LoginOrRegisterResult loginResult;
    LoginOrRegisterRequest failLoginRequest;
    LoginOrRegisterRequest registerRequest;
    LoginOrRegisterResult registerResult;
    ServerProxy serverProxy;
    String randomUsername;

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    @Before
    public void setUp() throws Exception {
        serverProxy = new ServerProxy();
        serverProxy.serverHostName = "localhost";
        serverProxy.serverPortNumber = 8081;
        loginRequest = new LoginOrRegisterRequest("sheila", "parker");
        randomUsername = getSaltString();
        registerRequest = new LoginOrRegisterRequest(randomUsername, "123",
                "a@gmail.com", "bill", "higgins", "m");
    }

    @Test
    public void loginPass() {
        loginResult = serverProxy.login(loginRequest);
        assertTrue(loginResult.getAuthToken() != null);
        assertTrue(loginResult.getUserName().equals("sheila"));
        assertTrue(loginResult.getPersonID().equals("Sheila_Parker"));
        assertTrue(loginResult.isSuccess());
    }

    @Test
    public void loginFail() {
        failLoginRequest = new LoginOrRegisterRequest("Invalid", "Credentials");
        loginResult = serverProxy.login(failLoginRequest);
        assertNull(loginResult.getAuthToken());
        assertNull(loginResult.getUserName());
        assertNull(loginResult.getPersonID());
        assertFalse(loginResult.isSuccess());
    }

    @Test
    public void registerPass() {
        registerResult = serverProxy.register(registerRequest);
        assertTrue(registerResult.getAuthToken() != null);
        assertTrue(registerResult.getPersonID() != null);
        assertTrue(registerResult.getUserName().equals(randomUsername));
        assertTrue(registerResult.isSuccess());
    }

    @Test
    public void registerFail() {
        LoginOrRegisterRequest incompleteRegisterRequest = new LoginOrRegisterRequest("Invalid", "Parameters");
        LoginOrRegisterResult incompleteRegisterResult = serverProxy.register(incompleteRegisterRequest);
        assertNull(incompleteRegisterResult.getAuthToken());
        assertNull(incompleteRegisterResult.getUserName());
        assertNull(incompleteRegisterResult.getPersonID());
        assertEquals("Username already taken", incompleteRegisterResult.getMessage());
        assertFalse(incompleteRegisterResult.isSuccess());
    }

    @Test
    public void getAllPeoplePass() {
        registerResult = serverProxy.register(registerRequest);
//        Class uses correct AuthToken
        AllPeople returnedFamilyTree = serverProxy.getAllPeople();
        assertTrue(returnedFamilyTree.getData().length == 31);
    }
    @Test
    public void getAllPeopleFail() {
        registerResult = serverProxy.register(registerRequest);
//        Class uses incorrect AuthToken
        serverProxy.currAuthToken = "INVALID_AUTHTOKEN";
        AllPeople returnedFamilyTree = serverProxy.getAllPeople();
        assertNull(returnedFamilyTree);
    }

    @Test
    public void getAllEventsPass() {
        registerResult = serverProxy.register(registerRequest);
//        Class uses correct AuthToken
        AllEvents returnedEvents = serverProxy.getAllEvents();
        assertTrue(returnedEvents.getData().length == 91);
    }
    @Test
    public void getAllEventsFail() {
        registerResult = serverProxy.register(registerRequest);
//        Class uses incorrect AuthToken
        serverProxy.currAuthToken = "INVALID_AUTHTOKEN";
        AllEvents returnedEvents = serverProxy.getAllEvents();
        assertNull(returnedEvents);
    }
}