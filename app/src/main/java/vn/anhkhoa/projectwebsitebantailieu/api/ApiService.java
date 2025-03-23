package vn.anhkhoa.projectwebsitebantailieu.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vn.anhkhoa.projectwebsitebantailieu.model.CategoryDto;
import vn.anhkhoa.projectwebsitebantailieu.model.DocumentDto;

public interface ApiService {
    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.13:8080/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("document/list")
    Call<ResponseData<List<DocumentDto>>> getListDocument();

    @GET("category/list")
    Call<ResponseData<List<CategoryDto>>> getListCategory();

    @GET("document/search-document")
    Call<ResponseData<List<DocumentDto>>> searchDocument(@Query("keyword") String keyword);
}
