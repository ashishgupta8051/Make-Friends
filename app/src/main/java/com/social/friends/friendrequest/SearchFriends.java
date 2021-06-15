package com.social.friends.friendrequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.social.friends.R;
import com.social.friends.activity.Home;
import com.social.friends.adapter.SearchFriendHistoryAdapter;
import com.social.friends.adapter.SearchFriendsAdapter;
import com.social.friends.loginactivity.Login;
import com.social.friends.model.SearchFriendHistory;
import com.social.friends.model.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SearchFriends extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchFriendsAdapter searchFriendsAdapter;
    private SearchFriendHistoryAdapter searchFriendHistoryAdapter;
    private FirebaseAuth firebaseAuth;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        getSupportActionBar().setTitle("Search");
        //toolbar1.setCollapseIcon(R.drawable.search);

        firebaseAuth = FirebaseAuth.getInstance();
        String CurrentUserId = firebaseAuth.getCurrentUser().getUid();
        textView = (TextView)findViewById(R.id.clear_all);

        recyclerView = (RecyclerView)findViewById(R.id.search_friend);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<SearchFriendHistory> options =
                new FirebaseRecyclerOptions.Builder<SearchFriendHistory>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("Search Friend History").child(CurrentUserId).orderByChild("totalCount"), SearchFriendHistory.class).build();
        searchFriendHistoryAdapter = new SearchFriendHistoryAdapter(options,SearchFriends.this);
        recyclerView.setAdapter(searchFriendHistoryAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Search Friend History").child(CurrentUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    textView.setVisibility(View.VISIBLE);
                    textView.setEnabled(true);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.removeValue();
                            textView.setVisibility(View.INVISIBLE);
                            textView.setEnabled(false);
                        }
                    });
                }else {
                    Log.e("Search Result","Not found any search result");
                    textView.setVisibility(View.INVISIBLE);
                    textView.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchFriends.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem item = menu.findItem(R.id.action_search2);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search your Friends !!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Query query1 = FirebaseDatabase.getInstance().getReference("User Details").orderByChild("userName").startAt(query).endAt(query+"\uf8ff");
                FirebaseRecyclerOptions<UserDetails> options =
                        new FirebaseRecyclerOptions.Builder<UserDetails>()
                                .setQuery(FirebaseDatabase.getInstance().getReference("User Details").orderByChild("usersName")
                                        .startAt(query).endAt(query+"\uf8ff"), UserDetails.class).build();
                searchFriendsAdapter = new SearchFriendsAdapter(options,SearchFriends.this);
                searchFriendsAdapter.startListening();
                recyclerView.setAdapter(searchFriendsAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.isEmpty()){
                    recyclerView.setAdapter(searchFriendHistoryAdapter);
                    searchFriendHistoryAdapter.startListening();
                }else {
                    //Query query1 = FirebaseDatabase.getInstance().getReference("User Details").orderByChild("userName").startAt(query).endAt(query+"\uf8ff");
                    FirebaseRecyclerOptions<UserDetails> options =
                            new FirebaseRecyclerOptions.Builder<UserDetails>()
                                    .setQuery(FirebaseDatabase.getInstance().getReference("User Details").orderByChild("usersName")
                                            .startAt(query).endAt(query+"\uf8ff"), UserDetails.class).build();
                    searchFriendsAdapter = new SearchFriendsAdapter(options,SearchFriends.this);
                    searchFriendsAdapter.startListening();
                    recyclerView.setAdapter(searchFriendsAdapter);
                }
                return false;
            }
        });
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        searchFriendHistoryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        searchFriendHistoryAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Home.class));
        finish();
    }
}