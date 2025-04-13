package vn.anhkhoa.projectwebsitebantailieu.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.anhkhoa.projectwebsitebantailieu.api.ApiService;
import vn.anhkhoa.projectwebsitebantailieu.api.ResponseData;
import vn.anhkhoa.projectwebsitebantailieu.database.CartDao;
import vn.anhkhoa.projectwebsitebantailieu.database.DatabaseHandler;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;

public class SyncManager {
    public void syncCarts(Context context) {
        if (!NetworkUtil.isNetworkAvailable(context))
            return;
        CartDao cartDao = new CartDao(context);
        List<CartDto> unsyncedCarts = cartDao.getUnsyncedItems();

        for (CartDto cart : unsyncedCarts) {
            switch (cart.getAction()) {
                case "INSERT":
                case "UPDATE":
                    syncAddOrUpdate(cart, cartDao);
                    break;
                case "DELETE":
                    syncDelete(cart, cartDao);
                    break;
            }
        }
    }

    private void  syncAddOrUpdate(CartDto cart, CartDao cartDao) {
        ApiService.apiService.addOrUpdate(cart).enqueue(new Callback<ResponseData<CartDto>>() {
            @Override
            public void onResponse(Call<ResponseData<CartDto>> call, Response<ResponseData<CartDto>> response) {
                if(response.isSuccessful() && response.body() != null){
                    cartDao.markAsSynced(cart.getCartId());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<CartDto>> call, Throwable t) {
                Log.d("ERROR","Lỗi kết nối API");
                t.printStackTrace();
            }
        });
    }

    private void syncDelete(CartDto cart, CartDao cartDao) {
        ApiService.apiService.deleteCartItem(cart.getCartId()).enqueue(new Callback<ResponseData<Void>>() {
            @Override
            public void onResponse(Call<ResponseData<Void>> call, Response<ResponseData<Void>> response) {
                if(response.isSuccessful()){
                    cartDao.deleteCartPermanently(cart.getCartId());
                }
            }

            @Override
            public void onFailure(Call<ResponseData<Void>> call, Throwable t) {
                Log.d("ERROR","Lỗi kết nối API");
                t.printStackTrace();
            }
        });
    }
}
