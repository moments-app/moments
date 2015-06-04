package moments.app.com;

/**
 * Created by Kevin Desai on 5/15/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class FragmentInvite extends Fragment {

    @InjectView(R.id.name) EditText nameUser;
    @InjectView(R.id.emailPartner) EditText partnerEmail;
    @InjectView(R.id.secretCode) EditText secretCode;
    @InjectView(R.id.inviteButton) Button inviteButton;

    @OnClick(R.id.inviteButton) void invitePartner(final View v){

        hideKeyboard(getActivity());
        if (!nameUser.getText().toString().equals(""))
        {
            if (isValidEmail(partnerEmail.getText().toString())){
                if (secretCode.getText().toString().length() >= 6)
                {
                    String userName = nameUser.getText().toString().trim();
                    String emailPartner = partnerEmail.getText().toString().trim();
                    String secretCodeP = secretCode.getText().toString().trim();

                    ParseUser user = ParseUser.getCurrentUser();
                    user.put("name",userName);
                    user.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                //Snackbar.make(v,"User name saved",Snackbar.LENGTH_SHORT).show();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                //Snackbar.make(v,"User name not saved",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });


                    ParseObject invite = new ParseObject("Invitation");
                    invite.put("sender",user.getUsername());
                    invite.put("receiver",emailPartner);
                    invite.put("secretCode",secretCodeP);
                    invite.put("isAccepted",false);
                    invite.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Snackbar.make(v,"Details saved",Snackbar.LENGTH_SHORT).show();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                                Snackbar.make(v,"Not saved",Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getActivity(),"Minimum length of secret code is 6.",Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getActivity(),"Please enter a vaild email.",Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(getActivity(),"Please specify a name.",Toast.LENGTH_SHORT).show();
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