package com.dfa.vinatrip.services.default_data;

import com.beesightsoft.caf.services.common.RestMessageResponse;
import com.dfa.vinatrip.models.response.Province;
import com.dfa.vinatrip.models.response.hotel.HotelResponse;
import com.dfa.vinatrip.models.response.place.PlaceResponse;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by duonghd on 10/6/2017.
 */

public interface RestDataService {

    @GET("api/provinces")
    Observable<RestMessageResponse<List<Province>>> getProvinces(
            @Query("page") long page,
            @Query("per_page") long per_page);

    @GET("api/province/{province_id}/hotels")
    Observable<RestMessageResponse<List<HotelResponse>>> getHotels(
            @Path("province_id") int province_id,
            @Query("page") long page,
            @Query("per_page") long per_page);

    @GET("api/province/{province_id}/places")
    Observable<RestMessageResponse<List<PlaceResponse>>> getPlaces(
            @Path("province_id") int province_id,
            @Query("page") long page,
            @Query("per_page") long per_page);
}