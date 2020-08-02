package com.vikaskaushik.instagramclone.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vikaskaushik.instagramclone.R;
import com.vikaskaushik.instagramclone.Utils.MainfeedListAdapter;
import com.vikaskaushik.instagramclone.models.Comment;
import com.vikaskaushik.instagramclone.models.Like;
import com.vikaskaushik.instagramclone.models.Photo;
import com.vikaskaushik.instagramclone.models.UserAccountSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    //vars
    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    private ArrayList<String> mFollowing;
    private ListView mListView;
    private MainfeedListAdapter mAdapter;
    private int mResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mListView = view.findViewById(R.id.listView);
        mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();

        getFollowing();
        return view;
    }

    private void getFollowing() {
        Log.d(TAG, "getFollowing: searching for following");
        try {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_following))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: found user:" +
                                singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());

                        mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString());
                    }
                    mFollowing.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    // get the photos
                    getPhotos();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG, "getFollowing: NullPointerException :" + e.getMessage());
        }

    }

    private void getPhotos() {
        Log.d(TAG, "getPhotos: getting photos  ");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        for (int i = 0; i < mFollowing.size(); i++) {
            Log.d(TAG, "getPhotos: count :" + i);
            final int count = i;
            Query query1 = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(i))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(i));


            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: looping");

                        Photo photo = new Photo();

                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setCaption(objectMap.get(getString(R.string.field_caption)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());


                        ArrayList<Comment> commentList = new ArrayList<Comment>();
                        for (DataSnapshot dSnapShot : singleSnapshot.child(getString(R.string.field_comments)).getChildren()) {
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapShot.getValue(Comment.class).getUser_id());
                            comment.setDate_created(dSnapShot.getValue(Comment.class).getDate_created());
                            comment.setComment(dSnapShot.getValue(Comment.class).getComment());
                            commentList.add(comment);
                        }
                        photo.setComments(commentList);
                        mPhotos.add(photo);
                        Log.d(TAG, "onDataChange: photos are :" + mPhotos);
                    }
                    if (count >= mFollowing.size() - 1) {
                        // display our photos
                        displayPhotos();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void displayPhotos() {
        mPaginatedPhotos = new ArrayList<>();
        Log.d(TAG, "displayPhotos: displaying photo");
        if (mPhotos != null) {
            try {

                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mPhotos.size();

                if (iterations > 10) {
                    iterations = 10;
                }

                mResults = 10;
                for (int i = 0; i < iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            } catch (NullPointerException e) {
                Log.d(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
            } catch (IndexOutOfBoundsException ar) {
                Log.d(TAG, "displayPhotos: IndexOutOfBoundsException: " + ar.getMessage());
            }


        }
    }

    public void displayMorePhotos() {
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try {

            if (mPhotos.size() > mResults && mPhotos.size() > 0) {

                int iterations;
                if (mPhotos.size() > (mResults + 10)) {
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                } else {
                    Log.d(TAG, "displayMorePhotos: less than 10 photos");
                    iterations = mPhotos.size() - mResults;
                }

                // add the new photos to the paginated results
                for (int i = mResults; i < mResults + iterations; i++) {
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();

            }

        } catch (NullPointerException e) {
            Log.d(TAG, "displayPhotos: NullPointerException: " + e.getMessage());
        } catch (IndexOutOfBoundsException ar) {
            Log.d(TAG, "displayPhotos: IndexOutOfBoundsException: " + ar.getMessage());
        }

    }
}
