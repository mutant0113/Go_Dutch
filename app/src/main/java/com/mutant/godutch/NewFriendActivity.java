package com.mutant.godutch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.mutant.godutch.model.Friend;
import com.mutant.godutch.utils.Utility;

/**
 * Created by evanfang102 on 2017/6/8.
 */

public class NewFriendActivity extends BaseActivity {

    CoordinatorLayout mCoordinatorLayoutParent;
    FirebaseUser mFirebaseUser;

    public static Intent getIntent(Context context) {
        return new Intent(context, NewFriendActivity.class);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_friend;
    }

    @Override
    public void findViews() {
        mCoordinatorLayoutParent = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_parent);
    }

    @Override
    public void setup() {
        setupFirebase();
        setupZxingGenerateQrcode();
    }

    private void setupFirebase() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setupZxingGenerateQrcode() {
        final AppCompatImageView imageViewQrcode = (AppCompatImageView) findViewById(R.id.imageView_qrcode);
        imageViewQrcode.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mFirebaseUser != null) {
                        Bitmap bitmap = encodeAsBitmap(mFirebaseUser.getUid(), imageViewQrcode.getWidth(), imageViewQrcode.getHeight());
                        imageViewQrcode.setImageBitmap(bitmap);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Bitmap encodeAsBitmap(String str, int width, int height) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, height, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }

    public void onClickZxingScan(View view) {
        String zxingPackagename = "com.google.zxing.client.android.SCAN";
        Intent intent = new Intent(zxingPackagename);
        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {
            // 未安裝
            Snackbar.make(mCoordinatorLayoutParent, "請至 Play 商店安裝 ZXing 條碼掃描器", Toast.LENGTH_LONG).show();
            Utility.INSTANCE.intentToGoogleMarketToDowloadApp(this, "com.google.zxing.client.android");
        } else {
            // SCAN_MODE, 可判別所有支援的條碼
            // QR_CODE_MODE, 只判別 QRCode
            // PRODUCT_MODE, UPC and EAN 碼
            // ONE_D_MODE, 1 維條碼
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

            // 呼叫ZXing Scanner，完成動作後回傳 1 給 onActivityResult 的 requestCode 參數
            startActivityForResult(intent, 1);
        }
    }

    // 接收 ZXing 掃描後回傳來的結果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // ZXing回傳的內容
                String newFriendUid = intent.getStringExtra("SCAN_RESULT");
                sendInvitationToNewFriend(newFriendUid);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消掃描", Toast.LENGTH_LONG).show();
            }
        }
    }

    // TODO check is user exist
    private void sendInvitationToNewFriend(final String newFriendUid) {
        if (mFirebaseUser != null) {
            FirebaseDatabase.getInstance().getReference().child("users").child(newFriendUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        Friend newFriend = dataSnapshot.getValue(Friend.class);
                        newFriend.setState(Friend.STATE_NOT_BE_ACCEPTED);
                        DatabaseReference databaseFriends = FirebaseDatabase.getInstance().getReference().child("friends");
                        databaseFriends.child(mFirebaseUser.getUid()).child(newFriendUid).setValue(newFriend);

                        // TODO fix me
//                        Friend me = new Friend(mFirebaseUser.getUid(), mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString());
                        Friend me = new Friend(mFirebaseUser.getUid(), "Evan", "");
                        me.setState(Friend.STATE_BE_INVITED);
                        databaseFriends.child(newFriendUid).child(me.getUid()).setValue(me);
                        finish();
                    } else {
                        Snackbar.make(mCoordinatorLayoutParent, "此ID不存在", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Snackbar.make(mCoordinatorLayoutParent, "FireBaseUser == null", Toast.LENGTH_LONG).show();
        }
    }
}
