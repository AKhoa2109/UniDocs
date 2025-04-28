package vn.anhkhoa.projectwebsitebantailieu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import vn.anhkhoa.projectwebsitebantailieu.model.CartDto;

public class CartDao extends BaseDao{
    public static final String TABLE_NAME = "Cart";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_DOCUMENT_ID = "DocumentId";
    public static final String COLUMN_DOCUMENT_NAME = "DocumentName";
    public static final String COLUMN_DOCUMENT_PRICE = "DocumentPrice";
    public static final String COLUMN_DOCUMENT_IMAGE = "DocumentImage";
    public static final String COLUMN_USER_ID = "UserId";
    public static final String COLUMN_IS_SELECTED = "IsSelected";
    public static final String COLUMN_ACTION = "ActionS";
    public static final String COLUMN_SYNC_STATUS    = "SyncStatus";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                    COLUMN_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_QUANTITY      + " INTEGER, " +
                    COLUMN_USER_ID       + " INTEGER, " +
                    COLUMN_DOCUMENT_ID        + " INTEGER, " +
                    COLUMN_DOCUMENT_NAME      + " TEXT, " +
                    COLUMN_DOCUMENT_PRICE    + " REAL, " +
                    COLUMN_DOCUMENT_IMAGE + " TEXT, " +
                    COLUMN_IS_SELECTED   + " INTEGER, " +
                    COLUMN_ACTION + " VARCHAR(200) DEFAULT 'INSERT', " +
                    COLUMN_SYNC_STATUS  + " INTEGER" +
                    ")";

    public CartDao(Context context) {
        super(context, TABLE_NAME);
    }

    public long addToCart(CartDto cartDto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, cartDto.getCartId());
        values.put(COLUMN_QUANTITY, cartDto.getQuantity());
        values.put(COLUMN_USER_ID, cartDto.getUserId());
        values.put(COLUMN_DOCUMENT_ID, cartDto.getDocId());
        values.put(COLUMN_DOCUMENT_NAME, cartDto.getDocName());
        values.put(COLUMN_DOCUMENT_PRICE, cartDto.getSellPrice());
        values.put(COLUMN_DOCUMENT_IMAGE, cartDto.getDocImageUrl());
        values.put(COLUMN_IS_SELECTED,   cartDto.isSelected() ? 1 : 0);
        values.put(COLUMN_ACTION, cartDto.getAction());
        values.put(COLUMN_SYNC_STATUS, cartDto.getSyncStatus());
        return insert(values);
    }

    public int updateCartItem(CartDto cartDto) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY,    cartDto.getQuantity());
        values.put(COLUMN_IS_SELECTED, cartDto.isSelected() ? 1 : 0);
        values.put(COLUMN_SYNC_STATUS, cartDto.getSyncStatus());
        String where = COLUMN_ID + " = ?";
        String[] args = new String[]{ String.valueOf(cartDto.getCartId()) };
        return update(values, where, args);
    }

    public int markAsSynced(long cartId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SYNC_STATUS, 1);
        String where = COLUMN_ID + " = ?";
        String[] args = {String.valueOf(cartId)};
        return update(values, where, args);
    }

    public int deleteCartItem(long cartId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTION, "DELETE");
        values.put(COLUMN_SYNC_STATUS, 0);
        String where = COLUMN_ID + " = ?";
        String[] args = {String.valueOf(cartId)};
        return update(values, where, args);
    }

    public int deleteCartPermanently(long cartId) {
        String where = COLUMN_ID + " = ?";
        String[] args = new String[]{ String.valueOf(cartId) };
        return delete(where, args);
    }

    public int deleteAllForUser(Long userId) {
        String where = COLUMN_USER_ID + " = ?";
        String[] args = new String[]{String.valueOf(userId)};
        return delete(where, args);
    }

    public List<CartDto> getCartItemsByUser(long userId) {
        List<CartDto> list = new ArrayList<>();
        String selection = COLUMN_USER_ID + " = ?";
        String[] args = new String[]{ String.valueOf(userId) };
        Cursor c = query(
                new String[]{
                        COLUMN_ID,
                        COLUMN_QUANTITY,
                        COLUMN_USER_ID,
                        COLUMN_DOCUMENT_ID,
                        COLUMN_DOCUMENT_NAME,
                        COLUMN_DOCUMENT_PRICE,
                        COLUMN_DOCUMENT_IMAGE,
                        COLUMN_IS_SELECTED,
                        COLUMN_SYNC_STATUS
                },
                selection,
                args,
                COLUMN_ID + " ASC"
        );
        while (c.moveToNext()) {
            CartDto item = new CartDto(c.getLong(0),c.getInt(1),c.getLong(2),
                    c.getLong(3),c.getString(4),c.getDouble(5),c.getString(6),
                    c.getInt(7) == 1,c.getString(8),c.getInt(9));
        }
        c.close();
        return list;
    }

    public CartDto getCartByUserAndDocId(long userId, long docId) {
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_DOCUMENT_ID + " = ?";
        String[] args = new String[]{String.valueOf(userId), String.valueOf(docId)};

        Cursor c = query(
                new String[]{
                        COLUMN_ID,
                        COLUMN_QUANTITY,
                        COLUMN_USER_ID,
                        COLUMN_DOCUMENT_ID,
                        COLUMN_DOCUMENT_NAME,
                        COLUMN_DOCUMENT_PRICE,
                        COLUMN_DOCUMENT_IMAGE,
                        COLUMN_IS_SELECTED,
                        COLUMN_ACTION,
                        COLUMN_SYNC_STATUS
                },
                selection,
                args,
                null
        );

        if (c.moveToFirst()) {
            CartDto cart = new CartDto(
                    c.getLong(0),
                    c.getInt(1),
                    c.getLong(2),
                    c.getLong(3),
                    c.getString(4),
                    c.getDouble(5),
                    c.getString(6),
                    c.getInt(7) == 1,
                    c.getString(8),
                    c.getInt(9)
            );
            c.close();
            return cart;
        }
        c.close();
        return null;
    }

    public List<CartDto> getUnsyncedItems() {
        List<CartDto> list = new ArrayList<>();
        String selection = COLUMN_SYNC_STATUS + " = ?";
        String[] args = new String[]{"0"};
        Cursor c = query(new String[]{
                COLUMN_ID,
                COLUMN_QUANTITY,
                COLUMN_USER_ID,
                COLUMN_DOCUMENT_ID,
                COLUMN_DOCUMENT_NAME,
                COLUMN_DOCUMENT_PRICE,
                COLUMN_DOCUMENT_IMAGE,
                COLUMN_IS_SELECTED,
                COLUMN_ACTION,
                COLUMN_SYNC_STATUS
        }, selection, args, null);

        while (c.moveToNext()) {
            CartDto item = new CartDto(
                    c.getLong(0),
                    c.getInt(1),
                    c.getLong(2),
                    c.getLong(3),
                    c.getString(4),
                    c.getDouble(5),
                    c.getString(6),
                    c.getInt(7) == 1,
                    c.getString(8),
                    c.getInt(9)
            );
            list.add(item);
        }
        c.close();
        return list;
    }

    public boolean existsInCart(long userId, long docId) {
        String selection = COLUMN_USER_ID + " = ? AND " + COLUMN_DOCUMENT_ID + " = ?";
        String[] args = new String[]{ String.valueOf(userId), String.valueOf(docId) };
        Cursor c = query(new String[]{ COLUMN_ID }, selection, args, null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    public int getCountCart(long userId) {
        int count = 0;
        String selection = COLUMN_USER_ID + " = ? ";
        String[] args = new String[]{ String.valueOf(userId)};
        Cursor c = query(new String[]{ COLUMN_ID }, selection, args, null);
        count = c.getCount();
        c.close();
        return count;
    }


}
