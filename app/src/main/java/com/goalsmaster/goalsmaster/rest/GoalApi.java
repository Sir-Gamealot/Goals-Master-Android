package com.goalsmaster.goalsmaster.rest;

import com.goalsmaster.goalsmaster.data.Id;
import com.goalsmaster.goalsmaster.data.Goal;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by tudor on 5/7/2017.
 */

public interface GoalApi {
    @POST("goal")
    @FormUrlEncoded
    Call<Id> createGoal(@Field("userId") long userId,
                        @Field("title") String title,
                        @Field("description") String description,
                        @Field("date") Date date,
                        @Field("priority") String priority,
                        @Field("photo") String photo);

    @GET("goal/{id}")
    Call<Goal> getGoalById(@Path("id") long id);

    @GET("findGoalByDateRange/{dateFrom}/{dateTo}")
    Call<List<Goal>> findGoalByDateRange(@Header("userId") long userId, @Path("dateFrom") Date dateFrom, @Path("dateTo") Date dateTo);

    @GET("findGoalByIdGroup/{idFrom}/{size}")
    Call<List<Goal>> findGoalByIdGroup(@Header("userId") long userId, @Path("idFrom") long idFrom, @Path("size") long size);

    @PUT("goal")
    @FormUrlEncoded
    Call<String> updateGoal(@Field("id") long id,
                            @Field("userId") long userId,
                            @Field("title") String title,
                            @Field("description") String description,
                            @Field("date") Date date,
                            @Field("priority") String priority,
                            @Field("photo") String photo);

    @DELETE("goal/{id}")
    Call<String> deleteGoalById(@Path("id") long id);
}
