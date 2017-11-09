package com.dfa.vinatrip.domains.main.fragment.me.detail_me.make_friend;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dfa.vinatrip.R;
import com.dfa.vinatrip.models.response.User;
import com.dfa.vinatrip.utils.AdapterUserListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import static com.dfa.vinatrip.utils.Constants.CANCEL_REQUEST;
import static com.dfa.vinatrip.utils.Constants.MAKE_REQUEST;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ProfileViewHolder> {

    private List<User> userList;
    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;
    private AdapterUserListener adapterUserListener;

    public ListUserAdapter(AdapterUserListener adapterUserListener) {
        this.imageLoader = ImageLoader.getInstance();
        this.imageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_avatar)
                .showImageForEmptyUri(R.drawable.ic_avatar)
                .showImageOnFail(R.drawable.ic_avatar)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_4444)
                .build();
        this.adapterUserListener = adapterUserListener;
    }

    public void setListUser(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_profile, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        User user = userList.get(position);
        if (user.getStatusFriend() == 1 || user.getStatusFriend() == 2) {
            holder.llRoot.setVisibility(View.VISIBLE);
            holder.tvNickname.setText(user.getUsername());
            holder.tvEmail.setText(user.getEmail());
            if (user.getAvatar() != null) {
                imageLoader.displayImage(user.getAvatar(), holder.ivAvatar, imageOptions);
            }
            holder.tvSex.setText(user.getStringSex());
            if (user.getStatusFriend() == 1) {
                holder.btnAction.setText(R.string.request);
                holder.btnAction.setTag(MAKE_REQUEST);
            } else {
                holder.btnAction.setText(R.string.sent);
                holder.btnAction.setTag(CANCEL_REQUEST);
            }
            holder.btnAction.setOnClickListener(v -> {
                adapterUserListener.onBtnActionClick(position, holder.btnAction.getTag().toString());
            });
        } else {
            holder.llRoot.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (userList != null) {
            return userList.size();
        }
        return 0;
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNickname, tvEmail, tvSex;
        private ImageView ivAvatar;
        private Button btnAction;
        private LinearLayout llRoot;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            tvNickname = (TextView) itemView.findViewById(R.id.item_user_profile_tv_nickname);
            tvEmail = (TextView) itemView.findViewById(R.id.item_user_profile_tv_email);
            tvSex = (TextView) itemView.findViewById(R.id.item_user_profile_tv_sex);
            ivAvatar = (ImageView) itemView.findViewById(R.id.item_user_profile_iv_avatar);
            btnAction = (Button) itemView.findViewById(R.id.item_user_profile_btn_action);
            llRoot = (LinearLayout) itemView.findViewById(R.id.item_user_profile_ll_root);
        }
    }
}