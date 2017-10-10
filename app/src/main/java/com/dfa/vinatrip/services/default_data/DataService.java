package com.dfa.vinatrip.services.default_data;

import com.dfa.vinatrip.models.response.Province;
import com.dfa.vinatrip.models.response.hotel.HotelResponse;
import com.dfa.vinatrip.models.response.place.PlaceResponse;

import java.util.List;

import rx.Observable;

/**
 * Created by duonghd on 10/6/2017.
 */

public interface DataService {
    Observable<List<Province>> getProvinces(long page, long per_page);

    Observable<List<HotelResponse>> getHotels(int province_id, long page, long per_page);

    Observable<List<PlaceResponse>> getPlaces(int province_id, long page, long per_page);
}