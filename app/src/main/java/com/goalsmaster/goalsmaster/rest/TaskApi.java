package com.goalsmaster.goalsmaster.rest;

import com.goalsmaster.goalsmaster.data.Id;
import com.goalsmaster.goalsmaster.data.Task;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
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

public interface TaskApi {
    @POST("task")
    @FormUrlEncoded
    Call<Id> createTask(@Field("userId") long userId,
                        @Field("title") String title,
                        @Field("description") String description,
                        @Field("date") Date date,
                        @Field("duration") long duration,
                        @Field("latitude") double latitude,
                        @Field("longitude") double longitude);

    @GET("task/{id}")
    Call<Task> getTaskById(@Path("id") long id);

    @GET("findTaskByDateRange/{dateFrom}/{dateTo}")
    Call<List<Task>> findTaskByDateRange(@Header("userId") long userId, @Path("dateFrom") Date dateFrom, @Path("dateTo") Date dateTo);

    @GET("findTaskByIdGroup/{idFrom}/{size}")
    Call<List<Task>> findTaskByIdGroup(@Header("userId") long userId, @Path("idFrom") long idFrom, @Path("size") long size);

    @PUT("task")
    @FormUrlEncoded
    Call<String> updateTask(@Field("id") long id,
                            @Field("userId") long userId,
                            @Field("title") String title,
                            @Field("description") String description,
                            @Field("date") Date date,
                            @Field("duration") long duration,
                            @Field("latitude") double latitude,
                            @Field("longitude") double longitude);

    @DELETE("task/{id}")
    Call<String> deleteTaskById(@Path("id") long id);
}
