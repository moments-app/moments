package moments.app.com;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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


    @OnClick(R.id.loginButton) void login(View view){

        if (!email.getText().toString().equalsIgnoreCase(null) && isValidEmail(email.getText().toString()))
        {
            if(password.getText().toString().length()>=8)
            {
                emailLabel.setError("");
                passwordLabel.setError("");

            }
            else
            {
                passwordLabel.setError("Minimum length of password is 6!");
            }
        }
        else
        {
            emailLabel.setError("Email address in invalid format!");
        }

    }

    @OnClick(R.id.signUpButton) void signUp(){

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

                    //set signup paramters
                    ParseUser user = new ParseUser();
                    user.setUsername(emailText);
                    user.setPassword(passText);

                    //Signup
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Wohoo, you can now use the app!", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    passwordLabel.setError("Minimum length of password is 6!");
                }
            } else {
                emailLabel.setError("Email address in invalid format!");
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Not connected to the internet", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

    }

}
