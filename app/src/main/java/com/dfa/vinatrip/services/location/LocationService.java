package com.dfa.vinatrip.services.location;

import com.dfa.vinatrip.domains.main.location_my_friend.UserLocation;

import java.util.List;

import rx.Observable;

/**
 * Created by duytq on 10/8/2017.
 */

public interface LocationService {
    Observable<List<UserLocation>> getLastLocation(String groupId);

}
