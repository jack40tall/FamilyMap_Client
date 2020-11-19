package com.example.familymap.jsmall3.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.FragmentManager;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


import com.example.familymap.jsmall3.R;
import com.example.familymap.jsmall3.model.DataCache;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener{

    private final int REQ_CODE_ORDER_INFO = 1;
    public static boolean LOGGED_IN = false;

    private LoginFragment loginFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = this.getSupportFragmentManager();
        loginFragment = (LoginFragment) fm.findFragmentById(R.id.fragmentContainer);
        if (LOGGED_IN == false) {
            loginFragment = new LoginFragment(this);
//            Bundle args = new Bundle();
////            args.putString();

            fm.beginTransaction()
                    .add(R.id.fragmentContainer, loginFragment)
                    .commit();
        }

//        Initialize font awesome
        Iconify.with(new FontAwesomeModule());
    }


    @Override
    public void onError(Error e) {

    }

    @Override
    public void onSuccess(boolean loginSuccess, String message) {
        if(loginSuccess) {
            LOGGED_IN = true;
            String firstName = DataCache.getInstance().getUser().getFirstName();
//            String lastName = DataCache.getInstance().getUser().getLastName();
//            String tempMessage = "First Name: " + firstName + "\nLast Name: " + lastName;
            String tempMessage = "Welcome " + firstName;
            Toast toast = Toast.makeText(getApplicationContext(),
                    tempMessage,
                    Toast.LENGTH_LONG);

            toast.show();
//        TODO: Use fragment manager to switch out with map
            FragmentManager fm = this.getSupportFragmentManager();

            mapFragment = new MapFragment();

            fm.beginTransaction()
                    .replace(R.id.fragmentContainer, mapFragment)
                    .commit();
            invalidateOptionsMenu();

        }
        else {
//            String tempMessage = "Login Failed: Please try again";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);

            toast.show();
        }
    }
}