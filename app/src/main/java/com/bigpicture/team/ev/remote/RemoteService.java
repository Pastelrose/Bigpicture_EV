package com.bigpicture.team.ev.remote;

import com.bigpicture.team.ev.item.ESCInfoItem;
import com.bigpicture.team.ev.item.KeepItem;
import com.bigpicture.team.ev.item.MemberInfoItem;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 서버에 호출할 메소드를 선언하는 인터페이스
 */
public interface RemoteService {
    String BASE_URL = "http://211.253.25.114:3000/";
    String MEMBER_ICON_URL = BASE_URL + "/member/";
    String IMAGE_URL = BASE_URL + "/img/";

    //사용자 정보
    @GET("/member/{member_id}")
    Call<MemberInfoItem> selectMemberInfo(@Path("member_id") String phone);

    @POST("/member/info")
    Call<String> insertMemberInfo(@Body MemberInfoItem memberInfoItem);

    @FormUrlEncoded
    @POST("/member/member_id")
    Call<String> insertMemberPhone(@Field("member_id") String phone);

    @Multipart
    @POST("/member/icon_upload")
    Call<ResponseBody> uploadMemberIcon(@Part("member_seq") RequestBody memberSeq,
                                        @Part MultipartBody.Part file);

    //맛집 정보
    @GET("/food/info/{info_seq}")
    Call<ESCInfoItem> selectESCInfo(@Path("cpId") int esId,
                                      @Query("member_id") int memberId);

    @POST("/esc/info")
    Call<String> insertESCInfo(@Body ESCInfoItem infoItem);

    @Multipart
    @POST("/esc/info/image")
    Call<ResponseBody> uploadESCImage(@Part("cpId") RequestBody infoSeq,
                                       @Part("image_memo") RequestBody imageMemo,
                                       @Part MultipartBody.Part file);

    @GET("/esc/list")
    Call<ArrayList<ESCInfoItem>> listESCInfo(@Query("member_id") String memberId,
                                               @Query("user_latitude") double userLatitude,
                                               @Query("user_longitude") double userLongitude,
                                               @Query("order_type") String orderType,
                                               @Query("current_page") int currentPage);


    //지도
    @GET("/esc/map/list")
    Call<ArrayList<ESCInfoItem>> listMap( @Query("lat") double latitude,
                                          @Query("lon") double longitude,
                                          @Query("distance") int distance,
                                          @Query("user_latitude") double userLatitude,
                                          @Query("user_longitude") double userLongitude);


    //즐겨찾기
    @POST("/keep/{member_seq}/{info_seq}")
    Call<String> insertKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

    @DELETE("/keep/{member_seq}/{info_seq}")
    Call<String> deleteKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

    @GET("/keep/list")
    Call<ArrayList<KeepItem>> listKeep(@Query("member_seq") int memberSeq,
                                       @Query("user_latitude") double userLatitude,
                                       @Query("user_longitude") double userLongitude);
}