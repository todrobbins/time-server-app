package org.publicntp.timeserver.ui.time;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.publicntp.timeserver.R;
import org.publicntp.timeserver.databinding.FragmentTimeBinding;
import org.publicntp.timeserver.helper.DateFormatter;
import org.publicntp.timeserver.helper.preferences.LocationCoordinateTypeStore;
import org.publicntp.timeserver.helper.preferences.TimezoneStore;
import org.publicntp.timeserver.repository.location.LocationStorageConsumer;
import org.publicntp.timeserver.repository.location.converters.CoordinateConverter;
import org.publicntp.timeserver.repository.time.TimeStorageConsumer;
import org.publicntp.timeserver.ui.BaseFragment;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TimeFragment extends BaseFragment {

    FragmentTimeBinding viewBinding;
    Timer invalidationTimer;
    final int invalidationFrequency = 1;

    @BindView(R.id.time_text_time_display) TextView TimeTextDisplay;
    @BindView(R.id.time_image_logo) GifImageView spinningLogo;
    @BindView(R.id.time_text_time_zone) TextView timezoneDisplay;
    @BindView(R.id.time_logo_text) TextView timeLogoText;

    private GifDrawable spinningDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_time, container, false);
        viewBinding.setTimestorage(new TimeStorageConsumer());
        viewBinding.setLocationstorage(new LocationStorageConsumer(new LocationCoordinateTypeStore().getConverter(getContext())));
        ButterKnife.bind(this, viewBinding.getRoot());

        DateFormatter.setTimezonePreference(new TimezoneStore().get(getContext()));
        initializeSpinningLogo();

        return viewBinding.getRoot();
    }

    @OnClick(R.id.time_layout_logo)
    public void timeLayoutLogoOnClick() {
        playLogoOnce();
    }

    @OnClick(R.id.time_options)
    public void timeOptionsOnClick() {
        OptionsDialogFragment optionsDialogFragment = new OptionsDialogFragment();
        optionsDialogFragment.setOnOptionPicked(new OptionsDialogFragment.OnOptionPicked() {
            @Override
            public void onTimezonePicked(String timezone) {
                timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext(), timezone));
            }

            @Override
            public void onLocationPicked(String units) {
                viewBinding.setLocationstorage(new LocationStorageConsumer(CoordinateConverter.byName(units)));
            }
        });
        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null) {
            optionsDialogFragment.show(fragmentManager, "OptionsFragment");
        }
    }

    private void initializeSpinningLogo() {
        spinningDrawable = (GifDrawable) spinningLogo.getDrawable();
        playLogoOnce();
    }

    private void playLogoOnce() {
        spinningDrawable.stop();
        spinningDrawable.setLoopCount(1);
        spinningDrawable.seekTo(400);
        spinningDrawable.start();
    }

    @Override
    public void onResume() {
        invalidationTimer = new Timer();
        invalidationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                viewBinding.invalidateAll();
            }
        }, invalidationFrequency, invalidationFrequency);

        timezoneDisplay.setText(new TimezoneStore().getTimeZoneShortName(getContext()));

        super.onResume();
    }

    @Override
    public void onPause() {
        invalidationTimer.cancel();
        super.onPause();
    }

    public static TimeFragment newInstance() {
        return new TimeFragment();
    }
}
