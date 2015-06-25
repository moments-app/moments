package moments.app.com;


import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import moments.app.com.RevealTransition;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    final ParseUser user = ParseUser.getCurrentUser();

    @InjectView(R.id.fragmentProgressBar)
    ProgressBar fragmentProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT>=21){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        fragmentProgressBar.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);


        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");
        query.whereEqualTo("sender", user.getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    //If the invitation has not been accepted show the waiting screen.
                    if(!object.getBoolean("isAccepted"))
                    {
                        fragmentProgressBar.setVisibility(View.INVISIBLE);
                        FragmentWaiting fragmentWaiting = new FragmentWaiting();
                        fragmentTransaction.add(R.id.container, fragmentWaiting);
                        fragmentTransaction.commit();
                    }
                    //if the invitation is accepted show the welcome screen.
                    else
                    {
                        //final String objectID = object.getObjectId();
                        //user.put("groupID",objectID);
                        //user.saveInBackground();
                        createNavDrawer();
                        fragmentProgressBar.setVisibility(View.INVISIBLE);
                        FragmentWelcome fragmentWelcome = new FragmentWelcome();
                        fragmentTransaction.add(R.id.container, fragmentWelcome);
                        fragmentTransaction.commit();
                    }
                }
                //If the email is not in the sender column, it can be in the reciever column. or it might not exist at all.
                else {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");
                    query.whereEqualTo("receiver", user.getUsername());
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object != null) {
                                //Log.d("score", "The getFirst request failed.");
                                if(!object.getBoolean("isAccepted"))
                                {
                                    fragmentProgressBar.setVisibility(View.INVISIBLE);
                                    FragmentInvited fragmentInvited = new FragmentInvited();
                                    fragmentTransaction.add(R.id.container, fragmentInvited);
                                    fragmentTransaction.commit();
                                }
                                //if the invitation is accepted show the welcome screen.
                                else
                                {
                                    //final String objectID = object.getObjectId();
                                    //user.put("groupID",objectID);
                                    //user.saveInBackground();
                                    createNavDrawer();
                                    fragmentProgressBar.setVisibility(View.INVISIBLE);
                                    FragmentWelcome fragmentWelcome = new FragmentWelcome();
                                    fragmentTransaction.add(R.id.container, fragmentWelcome);
                                    fragmentTransaction.commit();
                                }
                            }
                            //Null object means that the email id is not in any columns, sender or reciever. In such cases he/she has to invite
                            else {
                                //Log.d("score", "Retrieved the object.");
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentInvite fragmentInvite = new FragmentInvite();
                                fragmentTransaction.add(R.id.container, fragmentInvite);
                                fragmentTransaction.commit();
                            }
                        }
                    });
                }
            }
        });

    }

    public void createNavDrawer(){
        //Put navigation drawer creation code here!


    }

}
