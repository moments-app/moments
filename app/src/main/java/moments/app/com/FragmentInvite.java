package moments.app.com;

/**
 * Created by Kevin Desai on 5/15/2015.
 */

import android.app.Activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.parse.CountCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class FragmentInvite extends Fragment {

    @InjectView(R.id.name)
    EditText nameUser;
    @InjectView(R.id.emailPartner)
    EditText partnerEmail;
    @InjectView(R.id.secretCode)
    EditText secretCode;
    @InjectView(R.id.inviteButton)
    Button inviteButton;
    @InjectView(R.id.inviteProgress)
    ProgressBar progressBar;
    final ParseUser user = ParseUser.getCurrentUser();

    @OnClick(R.id.inviteButton)
    void invitePartner(final View v) {

        hideKeyboard(getActivity());
        if (!nameUser.getText().toString().equals("")) {
            if (isValidEmail(partnerEmail.getText().toString()) && !user.getUsername().equals(partnerEmail.getText().toString())) {
                if (secretCode.getText().toString().length() >= 6) {
                    final String userName = nameUser.getText().toString().trim();
                    final String emailPartner = partnerEmail.getText().toString().trim();
                    final String secretCodeP = secretCode.getText().toString().trim();

                    inviteButton.setEnabled(false);

                    progressBar.setVisibility(View.VISIBLE);


                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");
                    query.whereEqualTo("receiver", emailPartner);
                    query.countInBackground(new CountCallback() {
                        public void done(int count, ParseException e) {
                            if (e == null) {
                                //Snackbar.make(v,"Email exists: " + count,Snackbar.LENGTH_SHORT).show();

                                if (count == 0) {
                                    ParseObject invite = new ParseObject("Invitation");
                                    invite.put("sender", user.getUsername());
                                    invite.put("receiver", emailPartner);
                                    invite.put("secretCode", secretCodeP);
                                    invite.put("isAccepted", false);
                                    invite.saveInBackground(new SaveCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                //Snackbar.make(v,"Details saved",Snackbar.LENGTH_SHORT).show();
                                                HashMap<String, Object> params = new HashMap<String, Object>();
                                                params.put("secretCode", secretCodeP);
                                                params.put("emailR", emailPartner);
                                                params.put("emailS", user.getUsername());
                                                params.put("nameSender", userName);

                                                //CloudCode
                                                ParseCloud.callFunctionInBackground("sendEmail", params, new FunctionCallback<String>() {
                                                    public void done(String res, ParseException e) {
                                                        if (e == null) {

                                                            Toast.makeText(getActivity(), "" + res, Toast.LENGTH_SHORT).show();

                                                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Invitation");
                                                            query.whereEqualTo("sender", user.getUsername());
                                                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                                public void done(ParseObject object, ParseException e) {
                                                                    if (object != null) {
                                                                        //Log.d("score", "The getFirst request failed.");
                                                                        final String objId = object.getObjectId();
                                                                        user.put("groupID", objId);
                                                                        user.put("name", userName);
                                                                        user.saveInBackground();

                                                                        progressBar.setVisibility(View.INVISIBLE);


                                                                        FragmentManager fragmentManager2 = getFragmentManager();
                                                                        FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                                                                        FragmentWaiting fragment2 = new FragmentWaiting();
                                                                        fragmentTransaction2.replace(R.id.container, fragment2);
                                                                        fragmentTransaction2.commit();

                                                                    } else {
                                                                        //Log.d("score", "Retrieved the object.");
                                                                        Snackbar.make(v, "Email ID not found", Snackbar.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });


                                                        } else {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            inviteButton.setEnabled(true);
                                                            Toast.makeText(getActivity(), "" + res, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Sign up didn't succeed. Look at the ParseException
                                                // to figure out what went wrong
                                                progressBar.setVisibility(View.INVISIBLE);
                                                inviteButton.setEnabled(true);
                                                Snackbar.make(v, "Not saved", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    inviteButton.setEnabled(true);
                                    Snackbar.make(v, "Email already exists", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                inviteButton.setEnabled(true);
                                Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                // The request failed
                            }
                        }
                    });


                } else {
                    Toast.makeText(getActivity(), "Minimum length of secret code is 6.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please enter a vaild email.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getActivity(), "Please specify a name.", Toast.LENGTH_SHORT).show();
        }

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
        View view = inflater.inflate(R.layout.fragment_invite, container, false);

        ButterKnife.inject(this, view);

        return view;
    }
}