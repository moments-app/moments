package moments.app.com;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    final ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");
        query.whereEqualTo("sender", user.getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null) {
                    //If the invitation has not been accepted show the waiting screen.
                    if(!object.getBoolean("isAccepted"))
                    {
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
                                    FragmentWelcome fragmentWelcome = new FragmentWelcome();
                                    fragmentTransaction.add(R.id.container, fragmentWelcome);
                                    fragmentTransaction.commit();
                                }
                            }
                            //Null object means that the email id is not in any columns, sender or reciever. In such cases he/she has to invite
                            else {
                                //Log.d("score", "Retrieved the object.");
                                FragmentInvite fragmentInvite = new FragmentInvite();
                                fragmentTransaction.add(R.id.container, fragmentInvite);
                                fragmentTransaction.commit();
                            }
                        }
                    });
                }
            }
        });


        AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("Aayushi Acharya").withEmail("aayu.acharya@gmail.com").withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .build();


        Drawer.Result result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_primary1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_primary2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_primary3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_primary4),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_primary5),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_secondary1),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_secondary2),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_secondary3)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                    }
                })
                .build();





    }

}
