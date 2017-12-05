package com.dfa.vinatrip.domains.main.fragment.province.province_detail.view_all.hotel.hotel_detail;

import android.support.annotation.NonNull;

import com.dfa.vinatrip.MainApplication;
import com.dfa.vinatrip.R;
import com.dfa.vinatrip.base.BaseActivity;
import com.dfa.vinatrip.custom_view.NToolbar;
import com.dfa.vinatrip.infrastructures.ActivityModule;
import com.dfa.vinatrip.models.response.hotel.HotelResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by duonghd on 10/7/2017.
 */

@EActivity(R.layout.activity_province_hotel_detail)
public class HotelDetailActivity extends BaseActivity<HotelDetailView, HotelDetailPresenter>
        implements HotelDetailView {
    @App
    protected MainApplication mainApplication;
    @Inject
    protected HotelDetailPresenter presenter;
    
    @ViewById(R.id.activity_province_hotel_detail_tb_toolbar)
    protected NToolbar nToolbar;
    
    @Extra
    protected HotelResponse hotel;
    
    @AfterInject
    void initInject() {
        DaggerHotelDetailComponent.builder()
                .applicationComponent(mainApplication.getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build().inject(this);
    }
    
    @AfterViews
    void init() {
        nToolbar.setup(this, "TripGuy");
        nToolbar.showLeftIcon();
        nToolbar.showToolbarColor();
        nToolbar.setOnLeftClickListener(v -> {
            onBackPressed();
        });
    }
    
    @Override
    public void showLoading() {
        showHUD();
    }
    
    @Override
    public void hideLoading() {
        hideHUD();
    }
    
    @Override
    public void apiError(Throwable throwable) {
        
    }
    
    @NonNull
    @Override
    public HotelDetailPresenter createPresenter() {
        return presenter;
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
}