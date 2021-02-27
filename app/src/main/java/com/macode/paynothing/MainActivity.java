package com.macode.paynothing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.macode.paynothing.activities.OtherItemDetailActivity;
import com.macode.paynothing.fragments.AccountFragment;
import com.macode.paynothing.fragments.HomeFragment;
import com.macode.paynothing.fragments.InboxFragment;
import com.macode.paynothing.fragments.SellFragment;
import com.macode.paynothing.utilities.Items;
import com.macode.paynothing.utilities.ItemsViewHolder;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference itemReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        itemReference = FirebaseDatabase.getInstance().getReference().child("Items");

        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            } else if (item.getItemId() == R.id.inbox) {
                fragment = new InboxFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            } else if (item.getItemId() == R.id.post) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            } else if (item.getItemId() == R.id.sell) {
                fragment = new SellFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            } else if (item.getItemId() == R.id.account) {
                fragment = new AccountFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            } else {
                return false;
            }
            return false;
        }
    };
}