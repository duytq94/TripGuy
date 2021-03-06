package com.dfa.vinatrip.domains.main.fragment.plan.make_plan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beesightsoft.caf.exceptions.ApiThrowable;
import com.dfa.vinatrip.MainApplication;
import com.dfa.vinatrip.R;
import com.dfa.vinatrip.base.BaseActivity;
import com.dfa.vinatrip.domains.main.fragment.plan.Plan;
import com.dfa.vinatrip.domains.main.fragment.plan.UserInPlan;
import com.dfa.vinatrip.domains.main.fragment.plan.make_plan.choose_background.ChooseBackgroundPlanActivity_;
import com.dfa.vinatrip.infrastructures.ActivityModule;
import com.dfa.vinatrip.models.response.user.User;
import com.dfa.vinatrip.utils.AppUtil;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.nhancv.nutc.NUtc;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.dfa.vinatrip.utils.AppUtil.REQUEST_PLACE_AUTO_COMPLETE;
import static com.dfa.vinatrip.utils.Constants.FORMAT_DAY_VN;
import static com.dfa.vinatrip.utils.Constants.KEY_NEW_PLAN;
import static com.dfa.vinatrip.utils.Constants.MILLISECOND_IN_DAY;
import static com.dfa.vinatrip.utils.Constants.REQUEST_BACKGROUND;

@EActivity(R.layout.activity_make_plan)
public class MakePlanActivity extends BaseActivity<MakePlanView, MakePlanPresenter>
        implements MakePlanView, Validator.ValidationListener {

    @Extra
    protected boolean isUpdatePlan;
    @Extra
    protected Plan plan;

    @ViewById(R.id.activity_make_plan_rv_list_friend)
    protected RecyclerView rvListFriend;
    @ViewById(R.id.activity_make_plan_nsv_root)
    protected NestedScrollView nsvRoot;
    @Length(max = 40)
    @NotEmpty
    @ViewById(R.id.activity_make_plan_et_trip_name)
    protected EditText etTripName;
    @NotEmpty
    @ViewById(R.id.activity_make_plan_tv_destination)
    protected TextView tvDestination;
    @ViewById(R.id.activity_make_plan_tv_date_go)
    protected TextView tvDateGo;
    @ViewById(R.id.activity_make_plan_tv_date_back)
    protected TextView tvDateBack;
    @ViewById(R.id.activity_make_plan_tv_friend_not_available)
    protected TextView tvFriendNotAvailable;
    @ViewById(R.id.activity_make_plan_ll_schedule)
    protected LinearLayout llSchedule;
    @ViewById(R.id.activity_make_plan_ll_background)
    protected LinearLayout llBackground;
    @ViewById(R.id.activity_make_plan_civ_background)
    protected CircleImageView civBackground;

    private List<User> friendList;
    private InviteFriendAdapter inviteFriendAdapter;
    private Calendar calendar;
    private DatePickerDialog dpdDateGo, dpdDateBack;
    private List<UserInPlan> invitedFriendList;
    private Validator validator;
    private List<PlanSchedule> planScheduleList;
    private int idBackground;
    private User currentUser;

    private String dateGo = "";
    private String dateBack = "";
    private long timestampGo;
    private long countDaySchedule;

    @App
    protected MainApplication application;
    @Inject
    protected MakePlanPresenter presenter;

    @AfterInject
    protected void initInject() {
        DaggerMakePlanComponent.builder()
                .applicationComponent(application.getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build().inject(this);
    }

    @NonNull
    @Override
    public MakePlanPresenter createPresenter() {
        return presenter;
    }

    @AfterViews
    public void init() {
        currentUser = presenter.getCurrentUser();
        planScheduleList = new ArrayList<>();

        validator = new Validator(this);
        validator.setValidationListener(this);

        calendar = Calendar.getInstance();
        invitedFriendList = new ArrayList<>();

        if (isUpdatePlan) {
            presenter.getPlanDetail(plan.getId());
        } else {
            initViewForNewPlan();
            setCurrentDayForView();
        }
    }

    public void initViewForUpdatePlan() {
        etTripName.setText(plan.getName());
        tvDestination.setText(plan.getDestination());
        civBackground.setImageResource(plan.getIdBackground());
        tvDateGo.setText(plan.getDateGo());
        tvDateBack.setText(plan.getDateBack());
        planScheduleList.addAll(plan.getPlanScheduleList());

        dateGo = plan.getDateGo();
        dateBack = plan.getDateBack();
        updateViewListSchedule();
    }

    public void initViewForNewPlan() {
        idBackground = R.drawable.bg_test5;
        civBackground.setImageResource(idBackground);

        countDaySchedule = 1;

        presenter.getListFriend(1, Integer.MAX_VALUE);
    }

    public void setCurrentDayForView() {
        // Set current day for tvBirthday
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat(FORMAT_DAY_VN, Locale.getDefault());
        String strDate = simpleDateFormat.format(calendar.getTime());
        tvDateGo.setText(strDate);
        tvDateBack.setText(strDate);
        dateGo = strDate;
        dateBack = strDate;
        initViewListSchedule();
    }

    public void initViewListSchedule() {
        try {
            planScheduleList.clear();
            for (int i = 0; i < countDaySchedule; i++) {
                String title = ((EditText) llSchedule.getChildAt(i).findViewById(R.id.item_add_schedule_et_title)).getText().toString();
                String content = ((EditText) llSchedule.getChildAt(i).findViewById(R.id.item_add_schedule_et_content)).getText().toString();
                if (!title.isEmpty() || !content.isEmpty()) {
                    planScheduleList.add(new PlanSchedule(content, title, timestampGo + MILLISECOND_IN_DAY * i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        timestampGo = AppUtil.stringDateToTimestamp(dateGo);
        countDaySchedule = AppUtil.stringDateToTimestamp(dateBack) - AppUtil.stringDateToTimestamp(dateGo);
        countDaySchedule = countDaySchedule / MILLISECOND_IN_DAY + 1;

        llSchedule.removeAllViews();
        for (int i = 0; i < countDaySchedule; i++) {
            LinearLayout llRoot = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_schedule, null);
            LinearLayout llMain = (LinearLayout) llRoot.getChildAt(1);
            EditText etTitle = (EditText) llMain.findViewById(R.id.item_add_schedule_et_title);
            EditText etContent = (EditText) llMain.findViewById(R.id.item_add_schedule_et_content);
            TextView tvDate = (TextView) llMain.findViewById(R.id.item_add_schedule_tv_date);

            try {
                etTitle.setText(planScheduleList.get(i).getTitle());
                etContent.setText(planScheduleList.get(i).getContent());
            } catch (Exception e) {
                etTitle.setHint(String.format("Ngày %s...", i + 1));
                etContent.setHint(String.format("Lịch trình ngày %s...", i + 1));
            }
            tvDate.setText(AppUtil.formatTime(FORMAT_DAY_VN, timestampGo + MILLISECOND_IN_DAY * i));

            llSchedule.addView(llRoot);
        }
    }

    public void updateViewListSchedule() {
        timestampGo = AppUtil.stringDateToTimestamp(dateGo);
        countDaySchedule = AppUtil.stringDateToTimestamp(dateBack) - AppUtil.stringDateToTimestamp(dateGo);
        countDaySchedule = countDaySchedule / MILLISECOND_IN_DAY + 1;

        llSchedule.removeAllViews();
        for (int i = 0; i < countDaySchedule; i++) {
            LinearLayout llRoot = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_schedule, null);
            LinearLayout llMain = (LinearLayout) llRoot.getChildAt(1);
            EditText etTitle = (EditText) llMain.findViewById(R.id.item_add_schedule_et_title);
            EditText etContent = (EditText) llMain.findViewById(R.id.item_add_schedule_et_content);
            TextView tvDate = (TextView) llMain.findViewById(R.id.item_add_schedule_tv_date);

            etTitle.setText(plan.getPlanScheduleList().get(i).getTitle());
            etContent.setText(plan.getPlanScheduleList().get(i).getContent());
            tvDate.setText(AppUtil.formatTime(FORMAT_DAY_VN, timestampGo + MILLISECOND_IN_DAY * i));
            llSchedule.addView(llRoot);
        }
    }

    @Click(R.id.activity_make_plan_btn_cancel)
    public void onBtnCancelClick() {
        super.onBackPressed();
    }

    @Click(R.id.activity_make_plan_btn_done)
    public void onBtnDoneClick() {
        validator.validate();
    }

    @Click(R.id.activity_make_plan_ll_date_go)
    public void onLlDateGoClick() {
        // When date be set
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dateGo = dayOfMonth + "/" + (month + 1) + "/" + year;
            dateBack = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvDateGo.setText(dateGo);
            tvDateBack.setText(dateGo);
            calendar.set(year, month, dayOfMonth);

            initViewListSchedule();
        };

        // Set current position time when start dialog
        String strDate[] = tvDateGo.getText().toString().split("/");
        int day = Integer.parseInt(strDate[0]);
        int month = Integer.parseInt(strDate[1]) - 1;
        int year = Integer.parseInt(strDate[2]);
        dpdDateGo = new DatePickerDialog(this, listener, year, month, day);
        dpdDateGo.getDatePicker().setMinDate(System.currentTimeMillis());
        dpdDateGo.setTitle("Chọn ngày đi");
        dpdDateGo.show();
    }

    @Click(R.id.activity_make_plan_ll_date_back)
    public void onLlDateBackClick() {
        // When date be set
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            dateBack = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvDateBack.setText(dateBack);
            calendar.set(year, month, dayOfMonth);

            initViewListSchedule();
        };

        // Set current position time when start dialog
        String strDate[] = tvDateBack.getText().toString().split("/");
        int day = Integer.parseInt(strDate[0]);
        int month = Integer.parseInt(strDate[1]) - 1;
        int year = Integer.parseInt(strDate[2]);
        dpdDateBack = new DatePickerDialog(this, listener, year, month, day);
        dpdDateBack.getDatePicker().setMinDate(AppUtil.stringDateToTimestamp(dateGo));
        dpdDateBack.setTitle("Chọn ngày về");
        dpdDateBack.show();
    }

    @Click(R.id.activity_make_plan_ll_background)
    public void onLlBackgroundClick() {
        ChooseBackgroundPlanActivity_.intent(this).startForResult(REQUEST_BACKGROUND);
    }

    @Click(R.id.activity_make_plan_ll_destination)
    public void onLlDestinationClick() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("VN").build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter).build(this);
            startActivityForResult(intent, REQUEST_PLACE_AUTO_COMPLETE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @OnActivityResult(REQUEST_BACKGROUND)
    public void onResultBackground(int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            idBackground = data.getIntExtra("idBackground", R.drawable.bg_test3);
            civBackground.setImageResource(idBackground);
        }
    }

    @OnActivityResult(REQUEST_PLACE_AUTO_COMPLETE)
    public void onResultPlace(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Place place = PlaceAutocomplete.getPlace(this, data);
            tvDestination.setText(place.getAddress());
        } else if (data == null) {
            Toast.makeText(this, "Không chọn được địa điểm, bạn hãy thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onValidationSucceeded() {
        nsvRoot.scrollTo(0, nsvRoot.getBottom());
        planScheduleList.clear();
        for (int i = 0; i < countDaySchedule; i++) {
            String title = ((EditText) llSchedule.getChildAt(i).findViewById(R.id.item_add_schedule_et_title)).getText().toString();
            String content = ((EditText) llSchedule.getChildAt(i).findViewById(R.id.item_add_schedule_et_content)).getText().toString();
            planScheduleList.add(new PlanSchedule(content, title, timestampGo + MILLISECOND_IN_DAY * i));
        }
        if (isUpdatePlan) {
            plan.setName(etTripName.getText().toString().trim());
            plan.setDestination(tvDestination.getText().toString().trim());
            plan.setDateGo(dateGo);
            plan.setTimestampDateGo(AppUtil.stringDateToTimestamp(dateGo));
            plan.setDateBack(dateBack);
            plan.setTimestampDateBack(AppUtil.stringDateToTimestamp(dateBack) + 86400000);
            plan.setIdBackground(idBackground);
            plan.setPlanScheduleList(planScheduleList);
            plan.setInvitedFriendList(invitedFriendList);

            presenter.updatePlan(plan);
        } else {
            plan = new Plan(NUtc.getUtcNow(), etTripName.getText().toString().trim(), tvDestination.getText().toString(),
                    dateGo, AppUtil.stringDateToTimestamp(dateGo), dateBack, AppUtil.stringDateToTimestamp(dateBack) + 86400000, idBackground, planScheduleList, currentUser.getId(),
                    currentUser.getEmail(), currentUser.getAvatar(), currentUser.getUsername(), invitedFriendList);
            presenter.createPlan(plan);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            switch (message) {
                case "Invalid length":
                    message = "Chiều dài quá 40 ký tự";
                    break;
                case "This field is required":
                    message = "Không được để trống";
                    break;
            }
            if (view instanceof TextView) {
                ((TextView) view).setError(message);
            } else {
                ((EditText) view).setError(message);
            }
        }
        etTripName.requestFocus();
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
        ApiThrowable apiThrowable = (ApiThrowable) throwable;
        Toast.makeText(this, apiThrowable.firstErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getListFriendSuccess(List<User> friendList) {
        if (friendList.size() > 0) {
            tvFriendNotAvailable.setVisibility(View.GONE);
            this.friendList = new ArrayList<>();
            this.friendList.addAll(friendList);

            inviteFriendAdapter = new InviteFriendAdapter(this, this.friendList, invitedFriendList);
            rvListFriend.setAdapter(inviteFriendAdapter);
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvListFriend.setLayoutManager(manager);
        } else {
            tvFriendNotAvailable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void createPlanSuccess(String message) {
        Toast.makeText(this, "Kế hoạch của bạn được tạo thành công", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra(KEY_NEW_PLAN, plan);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void updatePlanSuccess(String message) {
        Toast.makeText(this, "Kế hoạch của bạn được cập nhật thành công", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void getDetailPlanSuccess(List<PlanSchedule> planScheduleList, List<UserInPlan> invitedFriendList) {
        plan.setPlanScheduleList(planScheduleList);
        plan.setInvitedFriendList(invitedFriendList);
        this.invitedFriendList = new ArrayList<>();
        this.invitedFriendList.addAll(plan.getInvitedFriendList());
        presenter.getListFriend(1, Integer.MAX_VALUE);

        initViewForUpdatePlan();
    }

}
