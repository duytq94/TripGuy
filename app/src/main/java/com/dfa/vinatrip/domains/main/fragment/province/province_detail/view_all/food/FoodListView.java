package com.dfa.vinatrip.domains.main.fragment.province.province_detail.view_all.food;

import com.dfa.vinatrip.base.BaseMvpView;
import com.dfa.vinatrip.models.response.food.FoodResponse;

import java.util.List;

/**
 * Created by duonghd on 10/13/2017.
 */

public interface FoodListView extends BaseMvpView {
    void getFoodsSuccess(List<FoodResponse> foodResponses);
}