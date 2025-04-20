package vn.anhkhoa.projectwebsitebantailieu.api;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentImageDto;
import vn.anhkhoa.projectwebsitebantailieu.model.ReviewDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.LoginRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserInfoDto;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserResponse;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;

@RequiresApi(api = Build.VERSION_CODES.O)
public interface ApiService {
    //Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    //sua IP
    public static String ipAddress = "10.0.2.2:8080";
    String baseUrl = "http://" + ipAddress + "/api/";
    /*String baseUrl = "https://hippo-powerful-fully.ngrok-free.app/api/";*/
     Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);
    //category
    @GET("category/list")
    Call<ResponseData<List<CategoryDto>>> getListCategory();

    @GET("category/{id}")
    Call<ResponseData<CategoryDto>> getDocumentById(@Path("id") Long id);

    //document
    @GET("document/list")
    Call<ResponseData<List<DocumentDto>>> getListDocument();

    @GET("document/search-document")
    Call<ResponseData<List<DocumentDto>>> searchDocument(@Query("keyword") String keyword);

    @GET("document/filter")
    Call<ResponseData<List<DocumentDto>>> filterDocument(@Query("keyword") String keyword,
                                                         @Query("sortType") String sortType,
                                                         @Query("categoryIds") Long[] categoryIds,
                                                         @Query("minPrice") Double minPrice,
                                                         @Query("maxPrice") Double maxPrice,
                                                         @Query("ratings") Integer[] ratings);
    @GET("document/document-detail")
    Call<ResponseData<DocumentDto>> getDocumentDetail(@Query("id") Long id);

    @GET("document/images/{id}")
    Call<ResponseData<List<DocumentImageDto>>> getAllImageByDocumentId(@Path("id") Long id);

    @GET("document/relevance")
    Call<ResponseData<List<DocumentDto>>> getRelevanceDocument(@Query("type") String type,
                                                               @Query("id") Long id,
                                                               @Query("docId") Long docId);
    @POST("document/discount-document")
    Call<ResponseData<List<DocumentDto>>> getDiscountDocument(@Body List<Long> docIds);
    //push document
    @POST("document/push")
    Call<ResponseData<DocumentDto>> pushDocument(@Body DocumentDto documentDto);

    //chat
    @GET("conversations/{conversationId}/messages")
    Call<ResponseData<List<ChatLineDto>>> getChatMessages(@Path("conversationId") Long conversationId);

    @Multipart
    @POST("send-chat-picture")
    Call<ChatLineDto> sendChatPicture(
            @Part MultipartBody.Part message,
            @Part List<MultipartBody.Part> files
    );

    //lay file
    @GET("get-file-by-id/{fileId}")
    Call<ResponseBody> getFileById(@Path("fileId") Long fileId);

    //user
    @POST("user/login")
    Call<ResponseData<UserResponse>> login(@Body LoginRequest request);

    @GET("user/shop-detail/{id}")
    Call<ResponseData<UserInfoDto>> getShopDetail(@Path("id") Long id);

    @POST("user/register")
    Call<ResponseData<OtpRequest>> register(@Body UserRegisterRequest registerRequest);

    @GET("user/check-email")
    Call<ResponseData<Boolean>> checkEmail(@Query("email") String email);

    @POST("user/verify-otp-for-activation")
    Call<ResponseData<String>> verifyOtpForActivation(@Body OtpRequest otpRequest);

    //conversation
    @GET("conversation/{userId}")
    Call<ResponseData<List<ConversationOverviewDto>>> findConversationsOverview(@Path("userId") Long userId);

    //review
    @GET("review/by-document/{docId}")
    Call<ResponseData<List<ReviewDto>>> getReviewsByDocumentId(@Path("docId") Long docId);

    @GET("review/rate-report/{docId}")
    Call<ResponseData<Map<Integer, Long>>> getRateReportByDocumentId(@Path("docId") Long docId);

    //cart
    @GET("cart/user/{userId}")
    Call<ResponseData<List<CartDto>>> getCartByUserId(@Path("userId") Long userId);

    @DELETE("cart/remove/{cartId}")
    Call<ResponseData<Void>> deleteCartItem(@Path("cartId") Long cartId);

    @DELETE("cart/remove/all/{userId}")
    Call<ResponseData<Void>> deleteAllCart(@Path("userId") Long userId);

    @POST("cart/add-update")
    Call<ResponseData<CartDto>> addOrUpdate(@Body CartDto cart);

    // discount
    @GET("discount/scope")
    Call<ResponseData<List<DiscountDto>>> getDiscountByScope(@Query("userIds") List<Long> userIds,
                                                             @Query("categoryIds") List<Long> categoryIds,
                                                             @Query("documentIds") List<Long> documentIds);

}
