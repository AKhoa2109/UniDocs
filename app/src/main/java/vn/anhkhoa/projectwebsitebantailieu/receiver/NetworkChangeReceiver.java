package vn.anhkhoa.projectwebsitebantailieu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vn.anhkhoa.projectwebsitebantailieu.utils.NetworkUtil;
import vn.anhkhoa.projectwebsitebantailieu.utils.SyncManager;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            new SyncManager().syncCarts(context);
        }
    }
}
