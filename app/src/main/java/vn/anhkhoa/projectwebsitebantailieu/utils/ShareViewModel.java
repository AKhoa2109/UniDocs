package vn.anhkhoa.projectwebsitebantailieu.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class ShareViewModel extends ViewModel {
    private final MutableLiveData<FilterCriteria> filterCriteria = new MutableLiveData<>();

    public void setFilterCriteria(FilterCriteria criteria) {
        filterCriteria.setValue(criteria);
    }

    public LiveData<FilterCriteria> getFilterCriteria() {
        return filterCriteria;
    }
}
