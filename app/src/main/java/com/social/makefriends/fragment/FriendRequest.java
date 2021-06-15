package com.social.makefriends.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.social.makefriends.R;
import com.social.makefriends.adapter.MyFriendRequestAdapter;
import com.social.makefriends.model.Request;


public class FriendRequest extends Fragment {
    private RecyclerView recyclerView;
    private MyFriendRequestAdapter myFriendRequestAdapter;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_friend_request, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView)view.findViewById(R.id.friend_request_rec);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().
                setQuery(FirebaseDatabase.getInstance().getReference("Friend Request").child(firebaseAuth.getUid()).
                        orderByChild("request_type").equalTo("received"), Request.class).build();

        myFriendRequestAdapter = new MyFriendRequestAdapter(options,getActivity());
        recyclerView.setAdapter(myFriendRequestAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myFriendRequestAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        myFriendRequestAdapter.stopListening();
        getActivity().finish();
    }
}