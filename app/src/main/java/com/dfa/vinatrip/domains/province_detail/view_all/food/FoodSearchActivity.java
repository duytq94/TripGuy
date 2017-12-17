package com.dfa.vinatrip.domains.province_detail.view_all.food;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dfa.vinatrip.MainApplication;
import com.dfa.vinatrip.R;
import com.dfa.vinatrip.base.BaseActivity;
import com.dfa.vinatrip.infrastructures.ActivityModule;
import com.dfa.vinatrip.models.response.Province;
import com.dfa.vinatrip.models.response.food.FoodResponse;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;

import java.util.List;

import javax.inject.Inject;

@SuppressLint("Registered")
@EActivity(R.layout.activity_food_search)
public class FoodSearchActivity extends BaseActivity<FoodSearchView, FoodSearchPresenter>
        implements FoodSearchView {
    @App
    protected MainApplication mainApplication;
    @Inject
    protected FoodSearchPresenter presenter;
    
    @Extra
    protected Province province;
    
    private static final int page = 0;
    private static final int per_page = 0;
    
    @AfterInject
    void initInject() {
        DaggerFoodSearchComponent.builder()
                .applicationComponent(mainApplication.getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build().inject(this);
    }
    
    @AfterViews
    void init() {
        presenter.getFoods(province.getId(), page, per_page);
    }
    
    @NonNull
    @Override
    public FoodSearchPresenter createPresenter() {
        return presenter;
    }
    
    @Override
    public void showLoading() {
        
    }
    
    @Override
    public void hideLoading() {
        
    }
    
    @Override
    public void apiError(Throwable throwable) {
        
    }
    
    @Override
    public void getFoodsSuccess(List<FoodResponse> foodResponses) {
        Log.e("test", "ok");
    }
}