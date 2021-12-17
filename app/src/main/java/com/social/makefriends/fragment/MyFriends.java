package com.social.makefriends.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.social.makefriends.R;
import com.social.makefriends.adapter.MyFriendAdapter;
import com.social.makefriends.model.Friends;
import com.social.makefriends.utils.CheckInternetConnection;

public class MyFriends extends Fragment {

    private RecyclerView recyclerView;
    private MyFriendAdapter myFriendAdapter;
    private FirebaseAuth firebaseAuth;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_friends, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.my_friend_rec);
        editText = (EditText) view.findViewById(R.id.search_friend2);
        //hide automatic soft keyboard
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().
                setQuery(FirebaseDatabase.getInstance().getReference("Friend").child(firebaseAuth.getUid())
                        .orderByChild("status").equalTo("friend"), Friends.class).build();

        myFriendAdapter = new MyFriendAdapter(options,getActivity());
        recyclerView.setAdapter(myFriendAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().
                        setQuery(FirebaseDatabase.getInstance().getReference("Friend").child(firebaseAuth.getUid()).orderByChild("friend_userName")
                                .startAt(s.toString()).endAt(s.toString()+"\uf8ff"), Friends.class).build();

                myFriendAdapter = new MyFriendAdapter(options,getActivity());
                myFriendAdapter.startListening();
                recyclerView.setAdapter(myFriendAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {
                FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().
                        setQuery(FirebaseDatabase.getInstance().getReference("Friend").child(firebaseAuth.getUid()).orderByChild("friend_userName")
                                .startAt(s.toString()).endAt(s.toString()+"\uf8ff"), Friends.class).build();

                myFriendAdapter = new MyFriendAdapter(options,getActivity());
                myFriendAdapter.startListening();
                recyclerView.setAdapter(myFriendAdapter);
            }
        });
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        myFriendAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        myFriendAdapter.stopListening();
        getActivity().finish();

    }


}