package com.dfa.vinatrip.MainFunction.Me;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfa.vinatrip.BuildConfig;
import com.dfa.vinatrip.DataService.DataService;
import com.dfa.vinatrip.Login.SignInActivity_;
import com.dfa.vinatrip.MainFunction.Me.UserDetail.MakeFriend.UserFriend;
import com.dfa.vinatrip.MainFunction.Me.UserDetail.UserProfileDetailActivity_;
import com.dfa.vinatrip.R;
import com.dfa.vinatrip.SplashScreen.SplashScreenActivity_;
import com.dfa.vinatrip.TripGuyUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

@EFragment(R.layout.fragment_me)
public class MeFragment extends Fragment {

    static final int NOTIFY_UPDATE_REQUEST = 1;

    @Bean
    DataService dataService;

    @ViewById(R.id.fragment_me_tv_nickname)
    TextView tvNickname;

    @ViewById(R.id.fragment_me_tv_city)
    TextView tvCity;

    @ViewById(R.id.fragment_me_tv_app_info)
    TextView tvAppInfo;

    @ViewById(R.id.fragment_me_tv_birthday)
    TextView tvBirthday;

    @ViewById(R.id.fragment_me_tv_introduce_your_self)
    TextView tvIntroduceYourSelf;

    @ViewById(R.id.fragment_me_tv_sex)
    TextView tvSex;

    @ViewById(R.id.fragment_me_tv_friend_not_available)
    TextView tvFriendNotAvailable;

    @ViewById(R.id.fragment_me_tv_make_friend)
    TextView tvMakeFriend;

    @ViewById(R.id.fragment_me_tv_email)
    TextView tvEmail;

    @ViewById(R.id.fragment_me_iv_avatar)
    ImageView ivAvatar;

    @ViewById(R.id.fragment_me_iv_blur_avatar)
    ImageView ivBlurAvatar;

    @ViewById(R.id.fragment_me_ll_sign_out)
    LinearLayout llSignOut;

    @ViewById(R.id.fragment_me_ll_info)
    LinearLayout llInfo;

    @ViewById(R.id.fragment_me_ll_my_friends)
    LinearLayout llMyFriends;

    @ViewById(R.id.fragment_me_ll_settings)
    LinearLayout llSettings;

    @ViewById(R.id.fragment_me_ll_update_profile)
    LinearLayout llUpdateProfile;

    @ViewById(R.id.fragment_me_srlReload)
    SwipeRefreshLayout srlReload;

    @ViewById(R.id.fragment_me_ll_login)
    LinearLayout llLogin;

    @ViewById(R.id.fragment_me_ll_not_login)
    LinearLayout llNotLogin;

    @ViewById(R.id.fragment_me_ll_my_friend1)
    LinearLayout llMyFriend1;
    @ViewById(R.id.item_friend_vertical_civ_avatar1)
    CircleImageView civAvatar1;
    @ViewById(R.id.item_friend_vertical_tv_nickname1)
    TextView tvNickname1;
    @ViewById(R.id.item_friend_vertical_tv_email1)
    TextView tvEmail1;

    @ViewById(R.id.fragment_me_ll_my_friend2)
    LinearLayout llMyFriend2;
    @ViewById(R.id.item_friend_vertical_civ_avatar2)
    CircleImageView civAvatar2;
    @ViewById(R.id.item_friend_vertical_tv_nickname2)
    TextView tvNickname2;
    @ViewById(R.id.item_friend_vertical_tv_email2)
    TextView tvEmail2;

    private UserProfile currentUser;
    private List<UserProfile> listUserProfiles;
    private List<UserFriend> listUserFriends;

    @AfterViews
    void onCreateView() {
        currentUser = dataService.getCurrentUser();

        showAppInfo();
        srlReload.setColorSchemeResources(R.color.colorMain);

        if (TripGuyUtils.isNetworkConnected(getActivity())) {
            if (currentUser != null) {
                initView();
            } else {
                llLogin.setVisibility(View.GONE);
                llNotLogin.setVisibility(View.VISIBLE);
            }
        }

        srlReload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (TripGuyUtils.isNetworkConnected(getActivity())) {
                    if (currentUser != null) {
                        initView();
                    }
                    srlReload.setRefreshing(false);
                } else {
                    srlReload.setRefreshing(false);
                }
            }
        });
    }

    public void showAppInfo() {
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;

        tvAppInfo.setText("TripGuy - giúp bạn có chuyến đi trọn vẹn nhất!\n");
        tvAppInfo.append("Version name: " + versionName + "\n");
        tvAppInfo.append("Version code: " + versionCode + "\n");
    }

    public void initView() {
        llLogin.setVisibility(View.VISIBLE);
        llNotLogin.setVisibility(View.GONE);

        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;

        tvAppInfo.setText("TripGuy - giúp bạn có chuyến đi trọng vẹn nhất!\n");
        tvAppInfo.append("Version name: " + versionName + "\n");
        tvAppInfo.append("Version code: " + versionCode + "\n");

        listUserProfiles = new ArrayList<>();
        listUserFriends = new ArrayList<>();

        initLlMyFriends();
        dataService.setOnChangeUserFriendList(new DataService.OnChangeUserFriendList() {
            @Override
            public void onAddItem() {
                initLlMyFriends();
            }

            @Override
            public void onRemoveItem() {
                initLlMyFriends();
            }
        });

        listUserProfiles.addAll(dataService.getUserProfileList());
        if (!currentUser.getNickname().equals("")) {
            tvNickname.setText(currentUser.getNickname());
        }
        if (!currentUser.getCity().equals("")) {
            tvCity.setText(currentUser.getCity());
        }
        if (!currentUser.getAvatar().isEmpty()) {
            Picasso.with(getActivity())
                    .load(currentUser.getAvatar())
                    .into(target);
        }
        tvIntroduceYourSelf.setText(currentUser.getIntroduceYourSelf());
        tvBirthday.setText(currentUser.getBirthday());
        tvEmail.setText(currentUser.getEmail());
        tvSex.setText(currentUser.getSex());

    }

    public void initLlMyFriends() {
        listUserFriends.clear();
        listUserFriends.addAll(TripGuyUtils.filterListFriends(dataService.getUserFriendList()));
        UserFriend userFriend;
        switch (listUserFriends.size()) {
            case 0:
                tvFriendNotAvailable.setVisibility(View.VISIBLE);
                llMyFriend1.setVisibility(View.GONE);
                llMyFriend2.setVisibility(View.GONE);
                break;
            case 1:
                tvFriendNotAvailable.setVisibility(View.GONE);
                userFriend = listUserFriends.get(0);
                llMyFriend1.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(userFriend.getAvatar()).into(civAvatar1);
                tvNickname1.setText(userFriend.getNickname());
                tvEmail1.setText(userFriend.getEmail());
                llMyFriend2.setVisibility(View.GONE);
                break;
            default:
                tvFriendNotAvailable.setVisibility(View.GONE);

                userFriend = listUserFriends.get(0);
                llMyFriend1.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(userFriend.getAvatar()).into(civAvatar1);
                tvNickname1.setText(userFriend.getNickname());
                tvEmail1.setText(userFriend.getEmail());

                userFriend = listUserFriends.get(1);
                llMyFriend2.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(userFriend.getAvatar()).into(civAvatar2);
                tvNickname2.setText(userFriend.getNickname());
                tvEmail2.setText(userFriend.getEmail());

                break;
        }
    }

    @Click(R.id.fragment_me_ll_sign_out)
    void onLlSignOutClick() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Đăng xuất");
        alertDialog.setMessage("Bạn có chắc chắn muốn đăng xuất tài khoản?");
        alertDialog.setIcon(R.drawable.ic_notification);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                dataService.clearData();
                startActivity(new Intent(getActivity(), SplashScreenActivity_.class));
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "KHÔNG", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertDialog.show();
    }

    @Click(R.id.fragment_me_ll_update_profile)
    void onLlUpdateProfileClick() {
        Intent intentUpdate = new Intent(getActivity(), UserProfileDetailActivity_.class);

        // Send notify to inform that llUpdateProfile be clicked
        String fromView = "llUpdateProfile";
        intentUpdate.putExtra("FromView", fromView);

        // Make UserProfileDetailActivity notify when it finish
        startActivityForResult(intentUpdate, NOTIFY_UPDATE_REQUEST);
    }

    @Click(R.id.fragment_me_tv_make_friend)
    void onTvMakeFriendClick() {
        Intent intentUpdate = new Intent(getActivity(), UserProfileDetailActivity_.class);

        // Send notify to inform that tvMakeFriend be clicked
        String fromView = "tvMakeFriend";
        intentUpdate.putExtra("FromView", fromView);

        // Make UserProfileDetailActivity notify when it finish
        startActivityForResult(intentUpdate, NOTIFY_UPDATE_REQUEST);
    }

    @Click(R.id.fragment_me_tv_view_more)
    void onTvViewMoreClick() {
        Intent intentUpdate = new Intent(getActivity(), UserProfileDetailActivity_.class);

        // Send notify to inform that tvViewMore be clicked
        String fromView = "tvViewMore";
        intentUpdate.putExtra("FromView", fromView);
        startActivity(intentUpdate);
    }

    @Click(R.id.fragment_me_btn_sign_in)
    void onBtnSignInClick() {
        Intent intent = new Intent(getActivity(), SignInActivity_.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int currentUserCode, Intent data) {
        super.onActivityResult(requestCode, currentUserCode, data);
        // Reload data
        if (requestCode == NOTIFY_UPDATE_REQUEST) {
            if (TripGuyUtils.isNetworkConnected(getActivity())) {
                initView();
            }
        }
    }

    // Note that to use bitmap, have to create variable target out of .into()
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (isAdded()) {
                Blurry.with(getActivity()).color(Color.argb(70, 80, 80, 80)).radius(10)
                        .from(bitmap).into(ivBlurAvatar);
                ivAvatar.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
}
