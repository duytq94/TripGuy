package com.dfa.vinatrip.services.account;

import com.beesightsoft.caf.services.authentication.AuthenticationManager;
import com.dfa.vinatrip.models.request.AuthRequest;
import com.dfa.vinatrip.models.request.ResetPasswordRequest;
import com.dfa.vinatrip.models.request.SignInSocialRequest;
import com.dfa.vinatrip.models.response.user.User;

import rx.Observable;


/**
 * Created by duytq on 9/17/2017.
 */

public interface AccountService extends AuthenticationManager<User, AuthRequest, SignInSocialRequest> {
    Observable<User> signUp(AuthRequest authRequest);

    Observable<User> editProfile(User user);

    Observable<String> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
