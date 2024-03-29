package com.eziamtech.malwapathshala.Webservice;

import com.eziamtech.malwapathshala.Model.Blog.BlogListingModel;
import com.eziamtech.malwapathshala.Model.Blog.BlogStatusModel;
import com.eziamtech.malwapathshala.Model.BlogCategoryModel.BlogCategoryModel;
import com.eziamtech.malwapathshala.Model.BlogComment.AddComment;
import com.eziamtech.malwapathshala.Model.BlogComment.BlogCommentModel;
import com.eziamtech.malwapathshala.Model.BlogFeatures.BlogFeaturesModel;
import com.eziamtech.malwapathshala.Model.BlogFeatures.Result;
import com.eziamtech.malwapathshala.Model.BlogLanguage.BlogLanguageModel;
import com.eziamtech.malwapathshala.Model.CategoryModel.CategoryModel;
import com.eziamtech.malwapathshala.Model.ForgotPassModel.ForgotPassModel;
import com.eziamtech.malwapathshala.Model.GeneralSettingsModel.GeneralSettingsModel;
import com.eziamtech.malwapathshala.Model.LeaderBoardModel.LeaderBoardModel;
import com.eziamtech.malwapathshala.Model.LevelModel.LevelModel;
import com.eziamtech.malwapathshala.Model.LoginRegiModel.LoginRegiModel;
import com.eziamtech.malwapathshala.Model.ProfileModel.ProfileModel;
import com.eziamtech.malwapathshala.Model.QuestionLanguage.QuestionLanguageModel;
import com.eziamtech.malwapathshala.Model.QuestionModel.QuestionModel;
import com.eziamtech.malwapathshala.Model.RecentQuizModel.RecentQuizModel;
import com.eziamtech.malwapathshala.Model.SuccessModel.SuccessModel;
import com.eziamtech.malwapathshala.Model.TodayLeaderBoardModel.TodayLeaderBoardModel;
import com.eziamtech.malwapathshala.Model.WithdrawalModel.WithdrawalModel;
import com.eziamtech.malwapathshala.Model.language.GetLanguage;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppAPI {

    @GET("genaral_setting")
    Call<GeneralSettingsModel> genaral_setting();

    @GET("home/get_lang")
    Call<GetLanguage> getLanguage();

    @FormUrlEncoded
    @POST("checkStatus")
    Call<SuccessModel> checkStatus(@Field("purchase_code") String purchase_code,
                                   @Field("package_name") String package_name);

    @FormUrlEncoded
    @POST("login")
    Call<LoginRegiModel> login(@Field("email") String email,
                               @Field("password") String password,
                               @Field("type") String type,
                               @Field("device_token") String device_token);

    @Multipart
    @POST("login")
    Call<LoginRegiModel> login(@Part("first_name") RequestBody first_name,
                               @Part("email") RequestBody email,
                               @Part("password") RequestBody password,
                               @Part("type") RequestBody type,
                               @Part("device_token") RequestBody device_token,
                               @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("registration")
    Call<LoginRegiModel> registration(@Field("email") String email,
                                      @Field("password") String password,
                                      @Field("first_name") String first_name,
                                      @Field("mobile_number") String mobile_number,
                                      @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("forgot_password")
    Call<ForgotPassModel> forgotpassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("profile")
    Call<ProfileModel> profile(@Field("user_id") String user_id);


    @GET("get_category")
    Call<CategoryModel> get_category();

    @FormUrlEncoded
    @POST("get_lavel")
    Call<LevelModel> get_lavel(@Field("category_id") String category_id,
                               @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_question_by_lavel")
    Call<QuestionModel> get_question_by_lavel(@Field("category_id") String category_id,
                                              @Field("level_id") String level_id);

    @FormUrlEncoded
    @POST("save_question_report")
    Call<SuccessModel> save_question_report(@Field("category_id") String category_id,
                                            @Field("level_id") String level_id,
                                            @Field("questions_attended") String questions_attended,
                                            @Field("total_questions") String total_questions,
                                            @Field("correct_answers") String correct_answers,
                                            @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("getLeaderBoard")
    Call<LeaderBoardModel> getLeaderBoard(@Field("user_id") String user_id,
                                          @Field("type") String type);

    @FormUrlEncoded
    @POST("getTodayLeaderBoard")
    Call<TodayLeaderBoardModel> getTodayLeaderBoard(@Field("level_id") String level_id,
                                                    @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("RecentQuizByUser")
    Call<RecentQuizModel> RecentQuizByUser(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("withdrawal_list")
    Call<WithdrawalModel> withdrawal_list(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("withdrawal_request")
    Call<SuccessModel> withdrawal_request(@Field("user_id") String user_id,
                                          @Field("payment_detail") String payment_detail,
                                          @Field("payment_type") String payment_type);


    @GET("home/get_questiontranslation")
    Call<QuestionLanguageModel> getChangedLanguageQuestion(@Query("question_id") String question_id,
                                                           @Query("lang_id") String lang_id);


    @GET("home/get_blogtranslation")
    Call<BlogLanguageModel> getChangedLanguageBlog(@Query("blog_id") String blog_id,
                                                   @Query("lang_id") String lang_id);

    @GET("home/get_blog")
    Call<BlogListingModel> getBlogListing(@Query("cat_id") String cat_id);

    @GET("home/get_blogcategory")
    Call<BlogCategoryModel> getBlogCategory();

    @GET("home/get_blogfeatures")
    Call<BlogFeaturesModel> getBlogFeatures(@Query("blog_id") String blog_id,
                                            @Query("lang_id") String lang_id);


    @GET("home/blogstatus")
    Call<BlogStatusModel> updateStatus(@Query("blog_id") String blog_id,
                                       @Query("lang_id") String lang_id,
                                       @Query("like") String like,
                                       @Query("watch") String watch,
                                       @Query("share") String share);


    @GET("home/get_blogcommentcount")
    Call<BlogCommentModel> getComments(@Query("blog_id") String blog_id,
                                       @Query("lang_id") String lang_id);

    @GET("home/blogcomment")
    Call<AddComment> addComment(@Query("blog_id") String blog_id,
                                @Query("lang_id") String lang_id,
                                @Query("comment") String comment);


}
