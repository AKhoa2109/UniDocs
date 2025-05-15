package vn.anhkhoa.projectwebsitebantailieu.api;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
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
import vn.anhkhoa.projectwebsitebantailieu.enums.OrderStatus;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DiscountDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentImageDto;
import vn.anhkhoa.projectwebsitebantailieu.model.FileDocument;
import vn.anhkhoa.projectwebsitebantailieu.model.MonthlyStatisticsDto;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationDto;
import vn.anhkhoa.projectwebsitebantailieu.model.NotificationGroup;
import vn.anhkhoa.projectwebsitebantailieu.model.OrderDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.ReviewDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.CreateOrderFromCartRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.LoginRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OrderDetailDtoRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.PasswordResetRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.request.UserRegisterRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.OtpRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserInfoDto;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserResponse;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateAdapter;
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
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
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

    @GET("category/by-user/{userId}")
    Call<ResponseData<List<CategoryDto>>> getCategoryByUserId(@Path("userId") Long userId);

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

    @GET("document/top-document/{userId}/{num}")
    Call<ResponseData<List<DocumentDto>>> getTopDocument(@Path("userId") Long userId,
                                                         @Path("num") Integer num);

    @GET("document/shop-detail/all-by/{userId}")
    Call<ResponseData<List<DocumentDto>>> getAllDocumentByUserId(@Path("userId") Long userId);

    @GET("document/filter-shop/{sortType}/{userId}")
    Call<ResponseData<List<DocumentDto>>> getAllDocumentByUserIdAndSortType(@Path("sortType") String sortType, @Path("userId") Long userId);
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

    @POST("user/forgot-password")
    Call<ResponseData<OtpRequest>> forgotPassword(@Query("email") String email, @Query("phoneNumber") String phoneNumber);

    @POST("user/check-otp")
    Call<ResponseData<String>> checkOtp(@Body OtpRequest otpRequest);

    @POST("user/verify-otp-for-password-reset")
    Call<ResponseData<String>> verifyOtpForResetPass(@Body PasswordResetRequest request);

    @GET("user/get-by-id/{userId}")
    Call<ResponseData<UserRegisterRequest>> getUser(@Path("userId") Long userId);

    @GET("user/get-by-email/{email}")
    Call<ResponseData<UserResponse>> getUserByEmail(@Path("email") String email);

    @POST("user/update-user")
    Call<ResponseData<UserRegisterRequest>> updateUser(@Body UserRegisterRequest userRegisterRequest);

    @Multipart
    @POST("user/upload-avatar")
    Call<ResponseData<String>> uploadAvatar(@Part MultipartBody.Part file);

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
    @GET("notifications/grouped")
    Call<ResponseData<List<NotificationGroup>>> getNotificationsGrouped(@Query("userId") Long userId);

    @POST("notifications/sync")
    Call<ResponseData<List<NotificationDto>>> syncNotification(@Body List<NotificationDto> notification);

    @POST("notifications/push/local")
    Call<ResponseData<Void>> pushLocalNotification(@Body NotificationDto notification);
    @POST("notifications/push")
    Call<ResponseData<Void>> pushWSNotification(@Body NotificationDto notification);


    @POST("discount/{userId}/addDiscount")
    Call<ResponseData<Long>> addDiscount(@Path("userId") Long userId,@Body DiscountDto discountDto);

    //File document
    @POST("file-document/by-document/{docId}")
    Call<ResponseData<FileDocument>> getFileDocumentByDocumentId(@Path("docId") Long docId);

    //payment

    @POST("payment/")
    Call<ResponseData<Map<String, String>>> createPayment(@Body CreateOrderFromCartRequest request);

//    @GET("payment/{orderId}")
//    Call<Map<String, String>> createPayment(
//            @Path("orderId") String orderId,
//            @Query("amount") double amount,
//            @Query("info") String info
//    );

//    @POST("/create")
//    Call<ResponseData<Long>> createOrderFromCart(@Body CreateOrderFromCartRequest request);

    //Dashboard
    @GET("statistics/monthly/{userId}")
    Call<ResponseData<List<MonthlyStatisticsDto>>> getMonthlyStats(@Path("userId") Long userId);

    @GET("order/status")
    Call<ResponseData<List<OrderDtoRequest>>> getOrdersByStatus(
            @Query("userId") Long userId,
            @Query("status") OrderStatus status
    );

    @GET("order/order-detail/{orderId}/{docId}/{userId}")
    Call<ResponseData<OrderDetailDtoRequest>> getOrderDetail(@Path("orderId") Long orderId,
                                                             @Path("docId") Long docId,
                                                             @Path("userId") Long userId);
}
