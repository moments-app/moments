package moments.app.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.transition.Explode;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DataFormatException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import nl.changer.polypicker.utils.ImageInternalFetcher;

public class AddNewPlaceActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        DatePickerDialog.OnDateSetListener {


    private Toolbar toolbar;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private TextView mPlaceName;

    private TextView mPlaceLocation;

    ParseUser user = ParseUser.getCurrentUser();
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private static final int INTENT_REQUEST_GET_N_IMAGES = 14;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    private Context mContext;
    private ViewGroup mSelectedImagesNone;
    HashSet<Uri> mMedia = new HashSet<Uri>();

    @InjectView(R.id.datePicker)
    TextView pickADate;
    @InjectView(R.id.name1)
    CheckBox firstName;
    @InjectView(R.id.name2)
    CheckBox secondName;
    @InjectView(R.id.editNameText1)
    EditText firstEdit;
    @InjectView(R.id.editNameText2)
    EditText secondEdit;
    @InjectView(R.id.makeANote)
    TextView makeNote;


    //Called after user adds a note.
    private void randomFunction()
    {
       //Toast.makeText(this,"It works!",Toast.LENGTH_SHORT).show();
       hideKeyboard();
    }



    //Dialog box for adding new note
    @OnClick(R.id.makeANote)
    void openDialogBox(View v) {
        new MaterialDialog.Builder(this)
                .title(R.string.noteOrMemory)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel)
                .input(R.string.input, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        makeNote.setText(input.toString());
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        randomFunction();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        randomFunction();
                    }
                })
                .show();
    }

    //Checkbox for name 1
    @OnCheckedChanged(R.id.name1)
    void enableEditText1(boolean checked){
        if (checked) {
            firstEdit.setEnabled(true);
            firstEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            firstEdit.setFocusable(true);
            firstEdit.requestFocus();
            firstEdit.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(firstEdit, InputMethodManager.SHOW_IMPLICIT);
        } else {
            firstEdit.setEnabled(false);
            //firstEdit.setFocusable(false);
        }
    }

    //Checkbox for name 2
    @OnCheckedChanged(R.id.name2)
    void enableEditText2(boolean checked) {
        if (checked) {
            secondEdit.setEnabled(true);
            secondEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            secondEdit.setFocusable(true);
            secondEdit.requestFocus();
            secondEdit.setFocusableInTouchMode(true);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(secondEdit, InputMethodManager.SHOW_IMPLICIT);
        } else {
            secondEdit.setEnabled(false);
            //secondEdit.setFocusable(false);
        }
    }

    //Date picker
    @OnClick(R.id.datePicker)
    void pickDate(final View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddNewPlaceActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT>=21){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
        }
        super.onCreate(savedInstanceState);

        mContext = AddNewPlaceActivity.this;


    //Initialize
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        setContentView(R.layout.activity_add_new_place);
        ButterKnife.inject(this);

    //Toolbar Stuff
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbarAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        if (android.os.Build.VERSION.SDK_INT>=21){
            toolbar.setNavigationIcon(R.drawable.keyboard_backspace);
        }
        else
        {
            toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_white_24dp);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //Who paid how much
        final String groupIDUser = user.getString("groupID");
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("groupID", groupIDUser);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userNames, ParseException e) {
                if (e == null) {
                    //Log.d("score", "Retrieved " + userNames.size() + " scores");
                    String name1 = userNames.get(0).getString("name");
                    String name2 = userNames.get(1).getString("name");
                    firstName.setText(name1);
                    secondName.setText(name2);

                } else {
                    //Log.d("score", "Error: " + e.getMessage());
                    //Handle this case
                }
            }
        });


    //Google Places Autocomplete stuff


        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        //mPlaceName = (TextView) findViewById(R.id.place_name);
        //mPlaceLocation = (TextView) findViewById(R.id.place_latLong);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        mAutocompleteView.setAdapter(mAdapter);


    }


    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            hideKeyboard();
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            //Log.i(TAG, "Autocomplete item selected: " + item.description);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + item.description,
                    Toast.LENGTH_SHORT).show();
            //Log.i(TAG, "Called getPlaceById to get Place details for " + item.placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                //Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            final String placeName = place.getName().toString();
            // Format details of the place for display and show it in a TextView.
            //mPlaceName.setText("The place is: " + place.getName());
            final LatLng location = place.getLatLng();
            final double latt = location.latitude;
            final double longt = location.longitude;
            //mPlaceLocation.setText("Location is: " + latt + ", " + longt + ". Type: " + place.getPlaceTypes().toString());

            /*
            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }
            */
            //Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    /*
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        //Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
        //        websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }
*/

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        //Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
         //       + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    //Hide Keyboard
    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    //On selecting the date from the date time picker. This function is called.
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear=monthOfYear+1;
        String date = dayOfMonth+"-"+ monthOfYear +"-"+year;
        pickADate.setText(date);
    }


}
