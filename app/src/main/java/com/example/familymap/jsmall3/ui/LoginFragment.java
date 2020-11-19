package com.example.familymap.jsmall3.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familymap.jsmall3.R;
import com.example.familymap.jsmall3.model.AllEvents;
import com.example.familymap.jsmall3.model.AllPeople;
import com.example.familymap.jsmall3.model.DataCache;
import com.example.familymap.jsmall3.net.ServerProxy;
import com.example.familymap.jsmall3.net.requests.LoginOrRegisterRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements LoginAsyncTask.Listener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText serverNameEditText;
    private EditText serverPortEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleButton;
    private RadioButton femaleButton;

    private String newUserSex;

    private boolean  genderSelected = false;

    private Button loginButton;
    private Button registerButton;

    public interface Listener {
        void onError(Error e);
        void onSuccess(boolean loginSuccess, String message);
    }
    private Listener listener;

    public LoginFragment(Listener l) {listener = l;}

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        serverNameEditText = (EditText)v.findViewById(R.id.hostEditText);
        serverNameEditText.addTextChangedListener(loginAndRegisterTextWatcher);

        serverPortEditText = (EditText)v.findViewById(R.id.portEditText);
        serverPortEditText.addTextChangedListener(loginAndRegisterTextWatcher);

        userNameEditText = (EditText)v.findViewById(R.id.usernameEditText);
        userNameEditText.addTextChangedListener(loginAndRegisterTextWatcher);

        passwordEditText = (EditText)v.findViewById(R.id.passwordEditText);
        passwordEditText.addTextChangedListener(loginAndRegisterTextWatcher);

        firstNameEditText = (EditText)v.findViewById(R.id.firstNameEditText);

        lastNameEditText = (EditText)v.findViewById(R.id.lastNameEditText);

        emailEditText = (EditText)v.findViewById(R.id.emailEditText);

        genderRadioGroup = (RadioGroup) v.findViewById(R.id.genderRadioGroup);
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
//                genderSelected = true;
                String serverNameInput = serverNameEditText.getText().toString().trim();
                String serverPortInput = serverPortEditText.getText().toString().trim();
                String usernameInput = userNameEditText.getText().toString().trim();
                String passwordInput = passwordEditText.getText().toString().trim();
                String firstNameInput = firstNameEditText.getText().toString().trim();
                String lastNameInput = lastNameEditText.getText().toString().trim();
                String emailInput = emailEditText.getText().toString().trim();

                registerButton.setEnabled(!serverNameInput.isEmpty() && !serverPortInput.isEmpty()
                        && !usernameInput.isEmpty() && !passwordInput.isEmpty()
                        && !firstNameInput.isEmpty() && !lastNameInput.isEmpty()
                        && !emailInput.isEmpty());
            }
        });

        maleButton = (RadioButton) v.findViewById(R.id.male);

        femaleButton = (RadioButton) v.findViewById(R.id.female);

        loginButton = (Button)v.findViewById(R.id.signInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInButtonClicked();
            }
        });


        registerButton = (Button)v.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButtonClicked();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

//      TODO: How to disable a button
    private TextWatcher loginAndRegisterTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String serverNameInput = serverNameEditText.getText().toString().trim();
            String serverPortInput = serverPortEditText.getText().toString().trim();
            String usernameInput = userNameEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();
            String firstNameInput = firstNameEditText.getText().toString().trim();
            String lastNameInput = lastNameEditText.getText().toString().trim();
            String emailInput = emailEditText.getText().toString().trim();

            registerButton.setEnabled(!serverNameInput.isEmpty() && !serverPortInput.isEmpty()
                    && !usernameInput.isEmpty() && !passwordInput.isEmpty()
                    && !firstNameInput.isEmpty() && !lastNameInput.isEmpty()
                    && !emailInput.isEmpty());

            loginButton.setEnabled(!serverNameInput.isEmpty() && !serverPortInput.isEmpty()
                                    && !usernameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void signInButtonClicked() {
        try {
            Log.d("DEBUG", "SignInButton clicked...");
            LoginAsyncTask task = new LoginAsyncTask(this);
            LoginOrRegisterRequest request = getLoginInfo();
            setServerInfo();
            task.execute(request);
        }
        catch (NumberFormatException e) {
            Toast.makeText(getActivity(),"Invalid Server Port Number",Toast.LENGTH_SHORT).show();
        }
    }
    private void registerButtonClicked() {
//  TODO: make this work for register
        try {
            Log.d("DEBUG", "RegisterButton clicked...");
            LoginAsyncTask task = new LoginAsyncTask(this);
            LoginOrRegisterRequest request = getRequestInfo();
            setServerInfo();
            task.execute(request);
        }
        catch (NumberFormatException e) {
            Toast.makeText(getActivity(),"Invalid Server Port Number",Toast.LENGTH_SHORT).show();
        }

    }



    public LoginOrRegisterRequest getLoginInfo() {
        LoginOrRegisterRequest request = new LoginOrRegisterRequest();

        request.setLogin(true);

        request.setUserName(userNameEditText.getText().toString());
        request.setPassword(passwordEditText.getText().toString());

        return request;
    }

    public LoginOrRegisterRequest getRequestInfo() {
        LoginOrRegisterRequest request = new LoginOrRegisterRequest();

        request.setLogin(false);

        request.setUserName(userNameEditText.getText().toString());
        request.setPassword(passwordEditText.getText().toString());
        request.setEmail(emailEditText.getText().toString());
        request.setFirstName(firstNameEditText.getText().toString());
        request.setLastName(lastNameEditText.getText().toString());
        if(maleButton.isChecked()) {
            request.setGender("m");
        }
        else {
            request.setGender("f");
        }

        return request;
    }

    public void setServerInfo() {
        ServerProxy.serverHostName = serverNameEditText.getText().toString();
        String portNum = serverPortEditText.getText().toString();
        ServerProxy.serverPortNumber = Integer.parseInt(portNum);
    }


    public void signIn() {

    }

//    public void onGenderSelected(View view) {
//        Log.d("DEBUG", "In onGenderSelected...");
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch(view.getId()) {
//            case R.id.male:
//                if(checked)
////                    male was selected
//                    genderSelected = true;
//                    break;
//            case R.id.female:
//                if (checked)
////                    female was selected
//                    genderSelected = true;
//                break;
//        }
//    }

    @Override
    public void onError(Error e) {

    }

    @Override
    public void onLogin() {

    }

    @Override
    public void onSuccess(boolean loginSuccess, String message) {
        listener.onSuccess(loginSuccess, message);
    }

    @Override
    public void populateDataCachePeople(AllPeople allPeople, String userID) {
        DataCache.getInstance().setUserID(userID);
        DataCache.getInstance().addFamilyTree(allPeople);
    }

    @Override
    public void populateDataCacheEvents(AllEvents allEvents) {
        DataCache.getInstance().addAllEvents(allEvents);
    }

    @Override
    public void onRegister() {

    }
}