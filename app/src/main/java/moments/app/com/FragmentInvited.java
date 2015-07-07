package moments.app.com;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Kevin Desai on 6/22/2015.
 */
public class FragmentInvited extends Fragment {

    @InjectView(R.id.partnerName)
    EditText partnerName;
    @InjectView(R.id.secretCodeName)
    EditText secretCodeName;
    @InjectView(R.id.startUsingAppButton)
    Button startUsingAppButton;
    @InjectView(R.id.inviteProgress2)
    ProgressBar progressBar;
    final ParseUser user = ParseUser.getCurrentUser();

    @OnClick(R.id.startUsingAppButton)
    void invited(final View v) {

        hideKeyboard(getActivity());
        if (!partnerName.getText().toString().equals("")) {
            if (!secretCodeName.getText().toString().equals("")) {

                final String myName = partnerName.getText().toString().trim();
                final String secretCodeRec = secretCodeName.getText().toString().trim();

                startUsingAppButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");
                query.whereEqualTo("receiver", user.getUsername());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (object != null) {
                            //Log.d("score", "The getFirst request failed.");
                            if (object.getString("secretCode").equals(secretCodeRec)) {

                                final String objId = object.getObjectId();

                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");

                                // Retrieve the object by id
                                query.getInBackground(objId, new GetCallback<ParseObject>() {
                                    public void done(ParseObject obj, ParseException e) {
                                        if (e == null) {
                                            // Now let's update it with some new data. In this case, only cheatMode and score
                                            // will get sent to the Parse Cloud. playerName hasn't changed.
                                            obj.put("isAccepted", true);
                                            user.put("groupID", objId);
                                            user.put("name", myName);
                                            user.saveInBackground();
                                            obj.saveInBackground();

                                            MainActivity forDraw = new MainActivity();
                                            forDraw.createNavDrawer();

                                            FragmentManager fragmentManager2 = getFragmentManager();
                                            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                                            FragmentWelcome fragment2 = new FragmentWelcome();
                                            fragmentTransaction2.replace(R.id.container, fragment2);
                                            fragmentTransaction2.commit();
                                        }
                                    }
                                });

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                startUsingAppButton.setEnabled(true);
                                Snackbar.make(v, "Secret code is incorrect. Please try again", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            startUsingAppButton.setEnabled(true);
                            Snackbar.make(v, "Email ID not found", Snackbar.LENGTH_SHORT).show();//Log.d("score", "Retrieved the object.");
                        }
                    }
                });

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                startUsingAppButton.setEnabled(true);
                Snackbar.make(v, "Secret code cannot be empty", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            startUsingAppButton.setEnabled(true);
            Snackbar.make(v, "Name cannot be empty", Snackbar.LENGTH_SHORT).show();
        }
    }


    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invited, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}
