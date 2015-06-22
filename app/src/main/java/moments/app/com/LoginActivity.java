package moments.app.com;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends AppCompatActivity {


    @InjectView(R.id.emailEditText) EditText email;
    @InjectView(R.id.passwordEditText) EditText password;
    @InjectView(R.id.loginButton) Button loginButton;
    @InjectView(R.id.emailLabel) TextInputLayout emailLabel;
    @InjectView(R.id.passwordLabel) TextInputLayout passwordLabel;
    @InjectView(R.id.signUpProgress) ProgressBar progressBar;
    @InjectView(R.id.showPassword) CheckBox showPassword;

    @OnClick(R.id.loginButton) void login(View view){

        hideKeyboard();

        if (isNetworkAvailable()) {
            if (isValidEmail(email.getText().toString()))
            {
                if (password.getText().toString().length() >= 8)
                {
                    //make errors go away
                    emailLabel.setError("");
                    passwordLabel.setError("");

                    //get edit text content into variables
                    String emailText = email.getText().toString().trim();
                    String passText = password.getText().toString().trim();

                    //Progress bar here
                    progressBar.setVisibility(View.VISIBLE);

                    ParseUser.logInInBackground(emailText, passText, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                // Hooray! The user is logged in.
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Wohoo, you are now logged in!", Toast.LENGTH_SHORT).show();
                                //Start MainActivity
                                startNewActivity();
                            } else {
                                // Signup failed. Look at the ParseException to see what happened.
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Email or password is not entered correctly.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    passwordLabel.setError("Minimum length of password is 8!");
                }
            }
            else
            {
                emailLabel.setError("Email address in invalid format!");
            }
        }
        else
        {
            Snackbar.make(view,"Not connected to internet",Snackbar.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.signUpButton) void signUp(View view){

        hideKeyboard();

        if(isNetworkAvailable()) {
            if (!email.getText().toString().equalsIgnoreCase(null) && isValidEmail(email.getText().toString())) {
                if (password.getText().toString().length() >= 8) {
                    //make errors go away
                    emailLabel.setError("");
                    passwordLabel.setError("");

                    //get edit text content into variables
                    String emailText = email.getText().toString().trim();
                    String passText = password.getText().toString().trim();

                    //Progress bar here
                    progressBar.setVisibility(View.VISIBLE);

                    //set sign up parameters
                    ParseUser user = new ParseUser();
                    user.setUsername(emailText);
                    user.setPassword(passText);

                    //Sign up
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Wohoo, you can now use the app!", Toast.LENGTH_SHORT).show();

                                //Start MainActivity
                                startNewActivity();

                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    passwordLabel.setErrorEnabled(true);
                    passwordLabel.setError("Minimum length of password is 8!");
                }
            } else {
                emailLabel.setErrorEnabled(true);
                emailLabel.setError("Email address in invalid format!");
            }
        }
        else
        {
            Snackbar.make(view, "Not connected to internet", Snackbar.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.showPassword) void togglePass(){
        int start,end;

        if (!showPassword.isChecked())
        {
            start=password.getSelectionStart();
            end=password.getSelectionEnd();
            password.setTransformationMethod(new PasswordTransformationMethod());
            password.setSelection(start,end);
        }
        else
        {
            start=password.getSelectionStart();
            end=password.getSelectionEnd();
            password.setTransformationMethod(null);
            password.setSelection(start,end);
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startNewActivity () {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
            setContentView(R.layout.activity_login);
            ButterKnife.inject(this);

    }

}
