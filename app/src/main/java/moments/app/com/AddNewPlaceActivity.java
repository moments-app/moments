package moments.app.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.mikepenz.iconics.typeface.FontAwesome;

import butterknife.InjectView;

public class AddNewPlaceActivity extends AppCompatActivity {


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT>=21){
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);


        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbarAdd);
        setSupportActionBar(toolbar);
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


    }

}
