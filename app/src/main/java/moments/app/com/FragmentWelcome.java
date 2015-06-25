package moments.app.com;

/**
 * Created by Kevin Desai on 5/15/2015.
 */
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.transition.AutoTransition;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class FragmentWelcome extends Fragment {

    @InjectView(R.id.fabBtn)
    FloatingActionButton fab;

    @OnClick(R.id.fabBtn) void startAddPlace(View view){

        //Toast.makeText(getActivity().getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
        if (android.os.Build.VERSION.SDK_INT>=21){
            getActivity().getWindow().setExitTransition(new Fade());
            Intent intent = new Intent(getActivity(), AddNewPlaceActivity.class);
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
        else
        {
            Intent intent = new Intent(getActivity(), AddNewPlaceActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}