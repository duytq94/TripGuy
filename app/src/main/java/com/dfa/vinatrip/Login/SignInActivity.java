package com.dfa.vinatrip.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dfa.vinatrip.R;
import com.dfa.vinatrip.SplashScreen.SplashScreenActivity_;
import com.dfa.vinatrip.TripGuyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_sign_in)
public class SignInActivity extends AppCompatActivity implements Validator.ValidationListener {

    private Validator validator;

    @NotEmpty
    @Email
    @ViewById(R.id.activity_sign_in_et_email)
    EditText etEmail;

    @Password
    @ViewById(R.id.activity_sign_in_et_password)
    EditText etPassword;

    @ViewById(R.id.activity_sign_in_progressBar)
    ProgressBar progressBar;

    @ViewById(R.id.activity_sign_in_ll_root)
    LinearLayout llRoot;

    @ViewById(R.id.activity_sign_in_iv_symbol)
    ImageView ivSymbol;

    private FirebaseAuth firebaseAuth;
    private Animation animSlideUp;
    private Animation animSlideDown;

    @AfterViews
    void onCreate() {
        animSlideUp = AnimationUtils.loadAnimation(this, R.anim.anim_slide_up);
        animSlideDown = AnimationUtils.loadAnimation(this, R.anim.anim_slide_down);

        llRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                llRoot.getWindowVisibleDisplayFrame(r);
                int screenHeight = llRoot.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    ivSymbol.startAnimation(animSlideUp);
                } else {
                    // keyboard is closed
                    ivSymbol.startAnimation(animSlideDown);
                }
            }
        });


        validator = new Validator(this);
        validator.setValidationListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        changeColorStatusBar();
    }

    @Override
    public void onValidationSucceeded() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        progressBar.setVisibility(View.VISIBLE);
        TripGuyUtils.setEnableAllViews(llRoot, false);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        TripGuyUtils.setEnableAllViews(llRoot, true);
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Email hoặc mật khẩu không đúng!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(SignInActivity.this, SplashScreenActivity_.class));
                        }
                    }
                });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages
            switch (message) {
                case "Invalid email\nThis field is required":
                    message = "Email trống\nBạn phải nhập email";
                    break;
                case "Invalid email":
                    message = "Email không hợp lệ";
                    break;
                case "Invalid password":
                    message = "Mật khẩu phải từ 6 ký tự trở lên";
                    break;
            }
            ((EditText) view).setError(message);
        }
    }

    @Click(R.id.activity_sign_in_btn_sign_up)
    void btnSignUpClicked() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity_.class));
    }

    @Click(R.id.activity_sign_in_btn_sign_in)
    void btnSignInClicked() {
        validator.validate();
    }

    @Click(R.id.activity_sign_in_btn_reset_password)
    void btnResetPassword() {
        startActivity(new Intent(SignInActivity.this, ResetPasswordActivity_.class));
    }

    public void changeColorStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
