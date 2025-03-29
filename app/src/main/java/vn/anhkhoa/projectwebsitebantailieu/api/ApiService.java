package vn.anhkhoa.projectwebsitebantailieu.api;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.ChatLineDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;
import vn.anhkhoa.projectwebsitebantailieu.model.request.LoginRequest;
import vn.anhkhoa.projectwebsitebantailieu.model.response.ConversationOverviewDto;
import vn.anhkhoa.projectwebsitebantailieu.model.response.UserResponse;
import vn.anhkhoa.projectwebsitebantailieu.utils.LocalDateTimeAdapter;

@RequiresApi(api = Build.VERSION_CODES.O)
public interface ApiService {
    //Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
     Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("document/list")
    Call<ResponseData<List<DocumentDto>>> getListDocument();

    @GET("category/list")
    Call<ResponseData<List<CategoryDto>>> getListCategory();

    @GET("document/search-document")
    Call<ResponseData<List<DocumentDto>>> searchDocument(@Query("keyword") String keyword);

    //chat
    @GET("conversations/{conversationId}/messages")
    Call<ResponseData<List<ChatLineDto>>> getChatMessages(@Path("conversationId") Long conversationId);

    //user
    @POST("user/login")
    Call<ResponseData<UserResponse>> login(@Body LoginRequest request);

    //conversation
    @GET("conversation/{userId}")
    Call<ResponseData<List<ConversationOverviewDto>>> findConversationsOverview(@Path("userId") Long userId);
}
