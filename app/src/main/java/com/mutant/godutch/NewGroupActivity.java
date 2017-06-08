package com.mutant.godutch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mutant.godutch.model.Friend;
import com.mutant.godutch.model.Group;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewGroupActivity extends BaseActivity {

    CoordinatorLayout mCoordinatorLayoutParent;
    AppCompatEditText mEditTextTitle;
    AppCompatEditText mEditTextDescription;
    ImageView mImageViewPhoto;
    RecyclerView mRecycleViewFriends;
    DatabaseReference mDatabase;
    StorageReference mStorage;

    public static Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, NewGroupActivity.class);
        return intent;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_group;
    }

    @Override
    public void findViews() {
        mCoordinatorLayoutParent = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_parent);
        mEditTextTitle = (AppCompatEditText) findViewById(R.id.editText_title);
        mEditTextDescription = (AppCompatEditText) findViewById(R.id.editText_description);
        mImageViewPhoto = ((ImageView) findViewById(R.id.imageView_photo));
    }

    @Override
    public void setup() {
        setupFireBase();
        setupFriends();
    }

    private void setupFireBase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String filePath = firebaseUser.getUid() + "/" + System.currentTimeMillis() + ".png";
        mStorage = FirebaseStorage.getInstance().getReference().child(filePath);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void onClickTakeAPhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageViewPhoto.setImageBitmap(imageBitmap);
        }
    }

    private void uploadImage(final Bitmap bitmap, OnFailureListener onFailureListener, OnSuccessListener onSuccessListener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorage.putBytes(data);
        uploadTask.addOnFailureListener(onFailureListener).addOnSuccessListener(onSuccessListener);
    }

    @SuppressWarnings("VisibleForTests")
    public void onClickCreateNewGroup(View view) {
        Bitmap bitmap = ((BitmapDrawable) mImageViewPhoto.getDrawable()).getBitmap();
        uploadImage(bitmap, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                exception.printStackTrace();
                Snackbar.make(mCoordinatorLayoutParent, R.string.upload_image_failed, Snackbar.LENGTH_LONG).show();
            }
        }, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar.make(mCoordinatorLayoutParent, R.string.upload_image_successfully, Snackbar.LENGTH_LONG).show();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                createNewGroup(taskSnapshot.getDownloadUrl());
            }
        });
    }

    private void createNewGroup(Uri imageDownloadUrl) {
        String title = mEditTextTitle.getText().toString();
        String description = mEditTextDescription.getText().toString();
        List<Friend> friendsFilterBySelected = ((RecycleViewAdapterFriends) mRecycleViewFriends.getAdapter()).getFriendsFilterBySelected();
        Group group = new Group(title, description, imageDownloadUrl.toString(), 0, friendsFilterBySelected);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userUid = firebaseUser.getUid();
            DatabaseReference databaseReference = mDatabase.child("groups").child(userUid).push();
            group.setId(databaseReference.getKey());
            databaseReference.setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                }
            });
        } else {
            try {
                Crashlytics.logException(new NullPointerException());
            } catch (IllegalThreadStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupFriends() {
        mRecycleViewFriends = (RecyclerView) findViewById(R.id.recycler_view_friends);
        // TODO fetch from web;
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("123", "小美", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend("123", "小明", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend("123", "小剛", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        friends.add(new Friend("123", "小智", "http://i.epochtimes.com/assets/uploads/2016/07/05c1348a7d53f02a1cc861f01d21878e-600x400.jpg"));
        RecycleViewAdapterFriends adapter = new RecycleViewAdapterFriends(friends);
        mRecycleViewFriends.setAdapter(adapter);
        mRecycleViewFriends.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private class RecycleViewAdapterFriends extends RecyclerView.Adapter<ViewHolder> {

        List<Friend> friends;
        boolean[] isSelected;

        public RecycleViewAdapterFriends(List<Friend> friends) {
            this.friends = friends;
            this.isSelected = new boolean[friends.size()];
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_item_friend, parent, false);
            // TODO judge isclicked
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Friend friend = friends.get(position);
            // TODO set image
//            holder.mImageViewProPic.setImageURI();
            holder.mTextViewName.setText(friend.getName());
            final View itemView = holder.itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (isSelected[position]) {
                        // TODO setbackground
                        itemView.setBackgroundColor(Color.WHITE);
                        isSelected[position] = false;
                    } else {
                        // TODO setbackground
                        itemView.setBackgroundColor(Color.YELLOW);
                        isSelected[position] = true;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        public List<Friend> getFriendsFilterBySelected() {
            List<Friend> friendsFliterBySelected = new ArrayList<>();
            for (int i = 0; i < friends.size(); i++) {
                if (isSelected[i]) {
                    friendsFliterBySelected.add(friends.get(i));
                }
            }
            return friendsFliterBySelected;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout mRelativeLayoutCompat;
        AppCompatImageView mImageViewProPic;
        AppCompatTextView mTextViewName;

        public ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        private void findViews(View itemView) {
            mRelativeLayoutCompat = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_friend);
            mImageViewProPic = (AppCompatImageView) itemView.findViewById(R.id.imageView_pro_pic);
            mTextViewName = (AppCompatTextView) itemView.findViewById(R.id.textView_name);
        }
    }
}
