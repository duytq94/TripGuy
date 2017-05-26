package com.dfa.vinatrip.domains.main.plan.make_plan;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dfa.vinatrip.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_choose_background_plan)
public class ChooseBackgroundPlanActivity extends AppCompatActivity {

    @ViewById(R.id.activity_choose_background_plan_rv_item)
    RecyclerView rvItem;

    private ChooseBackgroundPlanAdapter adapter;
    private int[] listBackground;
    private boolean[] listIsPhotoChoose;
    private int idBackgroundChosen;

    @AfterViews
    void onCreate() {
        listBackground = new int[]{R.drawable.bg_test1, R.drawable.bg_test2, R.drawable.bg_test3,
                                   R.drawable.bg_test4, R.drawable.bg_test5, R.drawable.bg_test6};
        listIsPhotoChoose = new boolean[]{false, false, true, false, false, false};
        idBackgroundChosen = R.drawable.bg_test3;

        adapter = new ChooseBackgroundPlanAdapter(this, listBackground, listIsPhotoChoose);
        adapter.setOnItemClick(new ChooseBackgroundPlanAdapter.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                for (int i = 0; i < listIsPhotoChoose.length; i++) {
                    if (i != position) {
                        listIsPhotoChoose[i] = false;
                    } else {
                        listIsPhotoChoose[i] = true;
                    }
                } idBackgroundChosen = listBackground[position];
                adapter.notifyDataSetChanged();
            }
        });

        rvItem.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvItem.setLayoutManager(manager);

        changeColorStatusBar();
    }

    public void changeColorStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
    }

    @Click(R.id.activity_choose_background_plan_tv_cancel)
    void onTvCancelClick() {
        super.onBackPressed();
    }

    @Click(R.id.activity_choose_background_plan_tv_choose)
    void onTvChooseClick() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("idBackground", idBackgroundChosen);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
