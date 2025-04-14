package vn.anhkhoa.projectwebsitebantailieu.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import vn.anhkhoa.projectwebsitebantailieu.database.CartDao;
import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<CartDto>> selectedCartItems = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<CartDto>> getSelectedCartItems() {
        return selectedCartItems;
    }

    public void setSelectedCartItems(List<CartDto> items) {
        selectedCartItems.setValue(items);
    }

    public void addSelectedCartItem(CartDto item) {
        List<CartDto> current = selectedCartItems.getValue();
        if (current != null && !current.contains(item)) {
            current.add(item);
            selectedCartItems.setValue(current);
        }
    }

    public void removeSelectedCartItem(CartDto item) {
        List<CartDto> current = selectedCartItems.getValue();
        if (current != null) {
            current.remove(item);
            selectedCartItems.setValue(current);
        }
    }

    public void clearSelectedCartItems() {
        List<CartDto> current = selectedCartItems.getValue();
        if (current != null) {
            current.clear();
            selectedCartItems.setValue(current);
        }
    }
}
