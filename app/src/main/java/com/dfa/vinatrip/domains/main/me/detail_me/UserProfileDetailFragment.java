package com.dfa.vinatrip.domains.main.me.detail_me;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dfa.vinatrip.R;
import com.dfa.vinatrip.domains.main.me.UserProfile;
import com.dfa.vinatrip.domains.main.me.detail_me.make_friend.MakeFriendFragment;
import com.dfa.vinatrip.domains.main.me.detail_me.make_friend.MakeFriendFragment_;
import com.dfa.vinatrip.domains.main.me.detail_me.my_friend.MyFriendFragment;
import com.dfa.vinatrip.domains.main.me.detail_me.my_friend.MyFriendFragment_;
import com.dfa.vinatrip.domains.main.me.detail_me.my_rating.MyRatingFragment;
import com.dfa.vinatrip.domains.main.me.detail_me.my_rating.MyRatingFragment_;
import com.dfa.vinatrip.domains.main.me.detail_me.update_profile.UpdateUserProfileFragment;
import com.dfa.vinatrip.domains.main.me.detail_me.update_profile.UpdateUserProfileFragment_;
import com.dfa.vinatrip.services.DataService;
import com.dfa.vinatrip.utils.ViewPagerSwipeFragmentAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_user_profile_detail)
public class UserProfileDetailFragment extends Fragment {

    @Bean
    DataService dataService;

    @ViewById(R.id.fragment_user_profile_detail_vp_user_profile_detail)
    ViewPager vpMeDetail;

    @ViewById(R.id.fragment_user_profile_detail_tl_menu)
    TabLayout tlMenu;

    private String fromView;

    private UpdateUserProfileFragment updateUserProfileFragment;
    private MakeFriendFragment makeFriendFragment;
    private MyFriendFragment myFriendFragment;
    private MyRatingFragment myRatingFragment;

    @AfterViews
    void onCreateView() {
        // Get the FromView from UserProfileDetailActivity
        fromView = getArguments().getBundle("bdFromView").getString("FromView");

        setupViewPager(vpMeDetail, dataService.getCurrentUser());

        tlMenu.setupWithViewPager(vpMeDetail);

        switch (fromView) {
            case "llUpdateProfile":
                vpMeDetail.setCurrentItem(0);
                break;
            case "tvMakeFriend":
                vpMeDetail.setCurrentItem(1);
                break;
            case "tvViewMoreMyFriend":
                vpMeDetail.setCurrentItem(2);
                break;
            case "tvViewMoreMyRating":
                vpMeDetail.setCurrentItem(3);
                break;
        }
    }

    public void setupViewPager(ViewPager vpMeDetail, UserProfile userProfile) {
        ViewPagerSwipeFragmentAdapter adapter = new ViewPagerSwipeFragmentAdapter(getChildFragmentManager());

        updateUserProfileFragment = new UpdateUserProfileFragment_();
        makeFriendFragment = new MakeFriendFragment_();
        myFriendFragment = new MyFriendFragment_();
        myRatingFragment = new MyRatingFragment_();

        adapter.addFragment(updateUserProfileFragment, "CHỈNH SỬA");
        adapter.addFragment(makeFriendFragment, "THÊM BẠN");
        adapter.addFragment(myFriendFragment, "BẠN TÔI");
        adapter.addFragment(myRatingFragment, "ĐÁNH GIÁ");

        vpMeDetail.setAdapter(adapter);
    }
}