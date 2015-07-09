package moments.app.com;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
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

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    final ParseUser user = ParseUser.getCurrentUser();
    private Drawer result = null; //save our header or result

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
                        FragmentWelcome fragmentWelcome = new FragmentWelcome();
                        fragmentTransaction.add(R.id.container, fragmentWelcome);
                        fragmentTransaction.commit();

                        createNavDrawer();
                        fragmentProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
                //If the email is not in the sender column, it can be in the receiver column. or it might not exist at all.
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
                                    FragmentWelcome fragmentWelcome = new FragmentWelcome();
                                    fragmentTransaction.add(R.id.container, fragmentWelcome);
                                    fragmentTransaction.commit();

                                    createNavDrawer();
                                    fragmentProgressBar.setVisibility(View.INVISIBLE);
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
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getString("name")).withEmail(user.getUsername()).withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .build();


        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_homeprimary).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_timeline).withIcon(FontAwesome.Icon.faw_history),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_expenses).withIcon(FontAwesome.Icon.faw_inr),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_statistics).withIcon(FontAwesome.Icon.faw_line_chart),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon.faw_map_marker),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_gallery).withIcon(FontAwesome.Icon.faw_picture_o),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settingsprimary).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_signout).withIcon(FontAwesome.Icon.faw_sign_out)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        fragmentManager = getFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        switch (position) {
                            case 0:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentHome fragmentHome = new FragmentHome();
                                fragmentTransaction.replace(R.id.container, fragmentHome).commit();
                                break;
                            case 1:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentTimeline fragmentTimeline = new FragmentTimeline();
                                fragmentTransaction.replace(R.id.container, fragmentTimeline).commit();
                                break;
                            case 2:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentExpenses fragmentExpenses = new FragmentExpenses();
                                fragmentTransaction.replace(R.id.container, fragmentExpenses).commit();
                                break;
                            case 3:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentStatistics fragmentStatistics = new FragmentStatistics();
                                fragmentTransaction.replace(R.id.container, fragmentStatistics).commit();
                                break;
                            case 4:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentMap fragmentMap = new FragmentMap();
                                fragmentTransaction.replace(R.id.container, fragmentMap).commit();
                                break;
                            case 5:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentGallery fragmentGallery = new FragmentGallery();
                                fragmentTransaction.replace(R.id.container, fragmentGallery).commit();
                                break;
                            case 7:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentProfile fragmentProfile = new FragmentProfile();
                                fragmentTransaction.replace(R.id.container, fragmentProfile).commit();
                                break;
                            case 8:
                                fragmentProgressBar.setVisibility(View.INVISIBLE);
                                FragmentSettings fragmentSettings = new FragmentSettings();
                                fragmentTransaction.replace(R.id.container, fragmentSettings).commit();
                                break;

                        }
                        return false;
                    }
                })
                .build();

    } //createNavDrawer ends

    @Override
        public void onBackPressed() {
            //handle the back press :D close the drawer first and if the drawer is closed close the activity
            if (result != null && result.isDrawerOpen()) {
                result.closeDrawer();
            } else {
                super.onBackPressed();
            }
        }



}//class ends
