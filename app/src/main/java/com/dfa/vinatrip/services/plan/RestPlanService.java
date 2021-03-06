package com.dfa.vinatrip.services.plan;

import com.beesightsoft.caf.services.common.RestMessageResponse;
import com.dfa.vinatrip.domains.main.fragment.plan.Plan;
import com.dfa.vinatrip.domains.main.fragment.plan.UserInPlan;
import com.dfa.vinatrip.domains.main.fragment.plan.make_plan.PlanSchedule;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by duytq on 11/01/2017.
 */

public interface RestPlanService {
    @POST("plan")
    Observable<RestMessageResponse<String>> createPlan(
            @Body Plan newPlan
    );

    @POST("plan/update/{planId}")
    Observable<RestMessageResponse<String>> updatePlan(
            @Body Plan newPlan
    );

    @GET("plan/{userId}")
    Observable<RestMessageResponse<List<Plan>>> getPlan(
            @Path("userId") long userId,
            @Query("title") String title,
            @Query("type") int type,
            @Query("expired") int expired,
            @Query("currentTimestamp") long currentTimestamp,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("planSchedule/{planId}")
    Observable<RestMessageResponse<List<PlanSchedule>>> getPlanSchedule(
            @Path("planId") long planId
    );

    @GET("planUser/{planId}")
    Observable<RestMessageResponse<List<UserInPlan>>> getPlanUser(
            @Path("planId") long planId
    );

    @DELETE("plan/cancel")
    Observable<RestMessageResponse<String>> cancelPlan(
            @Query("userId") long userId,
            @Query("planId") long planId
    );

    @DELETE("plan/remove")
    Observable<RestMessageResponse<String>> removePlan(
            @Query("planId") long planId
    );
}
