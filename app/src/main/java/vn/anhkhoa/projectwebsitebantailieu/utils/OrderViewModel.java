package vn.anhkhoa.projectwebsitebantailieu.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {
    private final MutableLiveData<Void> reloadTrigger = new MutableLiveData<>();

    /** Gọi khi muốn tất cả fragment reload dữ liệu */
    public void triggerReload() {
        reloadTrigger.setValue(null);
    }

    /** Fragment call observe để nhận sự kiện reload */
    public LiveData<Void> getReloadTrigger() {
        return reloadTrigger;
    }
}
