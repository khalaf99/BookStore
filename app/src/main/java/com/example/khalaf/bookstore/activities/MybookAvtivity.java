package com.example.khalaf.bookstore.activities;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khalaf.bookstore.Adaptor.BookAdaptor;
import com.example.khalaf.bookstore.Fragments.BookDetailsFragment;
import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.data.Book;
import com.example.khalaf.bookstore.data.Publisher;
import com.example.khalaf.bookstore.util.ActivityLauncter;
import com.example.khalaf.bookstore.util.Constans;
import com.example.khalaf.bookstore.util.Utilities;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MybookAvtivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BookAdaptor.OnBookClickListener {

    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private FirebaseAuth auth;
    private Publisher publisher;
    private ImageView ivProfilepic, ivAddBook;
    private TextView tvPublisherName, tvPublisherEmail;
    private FirebaseUser user;
    private RecyclerView recyclemybook;
    private BookAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybooksavtivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        books = new ArrayList<>();

        user = FirebaseAuth.getInstance().getCurrentUser();

        auth = FirebaseAuth.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncter.openAddBookActivity(MybookAvtivity.this);
            }
        });

        // drawer mn el 2a5er lazmeto eno y2fel we yeft7 el beta3 elly 3la ganb de
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // lazem 3shan tgeeb text view fel navigation view lazem tegbha 3n taree2 el navigation view
        ivProfilepic = navigationView.getHeaderView(0).findViewById(R.id.iv_profile);
        tvPublisherName = navigationView.getHeaderView(0).findViewById(R.id.tv_publisher_name);
        tvPublisherEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_publisher_email);

        recyclemybook =(RecyclerView) findViewById(R.id.recycler_my_books);
        adapter = new BookAdaptor(books, this);
        // to make recycler view vertical
        recyclemybook.setLayoutManager(new GridLayoutManager(this, 3));
        recyclemybook.setAdapter(adapter);

        // get info from database
        showPublisherInfoAndBooks();

    }

    private void showPublisherInfoAndBooks() {
        if (Utilities.isNetworkAvailable(this)) {
            Utilities.showLoadingDialog(this, Color.WHITE);
            getpublisherinfo();
            getbooks();
        } else {
            Snackbar.make(recyclemybook, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPublisherInfoAndBooks();
                        }
                    })
                    .setActionTextColor(Color.WHITE)
                    .show();
        }

    }


    private ArrayList<Book> books;

    private void getbooks() {
        Utilities.showLoadingDialog(MybookAvtivity.this,Color.WHITE);
        FirebaseDatabase
                .getInstance()
                .getReference(Constans.REF_BOOK)
                .orderByChild(Constans.PUBLISHER_ID)
                .equalTo(user.getUid())
                //lisener fire when add a book
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Book book = dataSnapshot.getValue(Book.class);
                        if (book != null) {
                            books.add(book);
                            adapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Book book = dataSnapshot.getValue(Book.class);
                        for (int i = 0; i < books.size(); i++) {
                            if (books.get(i).getId().equals(book.getId())) {
                                books.set(i, book);
                                adapter.notifyItemChanged(i);
                                return;
                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Book book = dataSnapshot.getValue(Book.class);
                        books.remove(book);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        Utilities.dismissLoadingDialog();

    }

    private void getpublisherinfo() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // lw la2aa el el user
        if (firebaseUser != null) {
            // 3awzeen ngeeb el user elly 3amel login fel database
            FirebaseDatabase.getInstance()
                    .getReference(Constans.REF_PUBLISHER)
                    .child(firebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        // de hafire hena lw 7asal change fel data
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // b7wawlly mn json le 7aga mn nooo3 object
                            publisher = dataSnapshot.getValue(Publisher.class);
                            if (publisher != null) {
                                // put values into views in navigation drawer in my books activity
                                tvPublisherName.setText(publisher.getName());
                                tvPublisherEmail.setText(publisher.getEmail());
                                // picasso lib can download images and but it in any image view you want
                                // glid hya hya picasso bc 27san we mabt3l2sh fel loading
                                Glide.with(MybookAvtivity.this)
                                        .load(publisher.getImageuri())
                                        .into(ivProfilepic);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        // de lw press button 2ma yekoon fat7 el drawer
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // de el back el 3adeya
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mybooksavtivity, menu);

        // dool el 3 lines i will use it in any app using searchview

        // na 3awz 2ma 2dooos 3la el search yewadeny 3la el search view
        // on click firing searchview
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // de mn el 2a5er bet2ool ll search en el search da tab3 el app beta3y
        // mn el 2a5er 3shan el search teshta8al lazem ta5od info 3n el app beta3k
        // de el rabtaa been el 2 elly fooo2
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ActivityLauncter.openEditProfileActivity(MybookAvtivity.this , publisher);

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {

            Logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are You sure ?").setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        auth.signOut();
                        ActivityLauncter.openLoginActivity(MybookAvtivity.this);
                        finish();
                    }
                }).show();

    }

    @Override
    public void onBookClick(Book book) {

        BookDetailsFragment.with(book).show(getSupportFragmentManager(), "");
    }
}
