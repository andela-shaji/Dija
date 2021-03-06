package com.example.suadahaji.dijaapplication.ui;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.suadahaji.dijaapplication.R;
import com.example.suadahaji.dijaapplication.api.ApiManager;
import com.example.suadahaji.dijaapplication.dagger.BooksApplication;
import com.example.suadahaji.dijaapplication.models.Book;
import com.example.suadahaji.dijaapplication.listbooks.MainView;
import com.example.suadahaji.dijaapplication.listbooks.PresenterImpl;

import java.util.ArrayList;

import javax.inject.Inject;

public class ListBooksActivity extends AppCompatActivity implements MainView, BooksAdapter.BookListener {

    public static final String EXTRA_BOOK_ITEM = "book_image_url";
    public static final String EXTRA_BOOK_IMAGE_TRANSITION_NAME = "book_image_transition_name";

    private RecyclerView recyclerView;
    private PresenterImpl presenter;
    private BooksAdapter adapter;

    @Inject
    ApiManager apiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.books_view);

        getSupportActionBar().setTitle("Dija Books");

        ((BooksApplication) getApplicationContext()).getAppComponent().inject(this);

        recyclerView = (RecyclerView) findViewById(R.id.books_recycler_view);

        presenter = new PresenterImpl(apiManager);
        presenter.attachedView(this);

        setupRecyclerView();

        presenter.onStart();
    }

    private void setupRecyclerView() {
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void showProgressBar() {
       findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void hideProgressBar() {
        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setBooks(ArrayList<Book> books) {
        adapter = new BooksAdapter(books, R.layout.book_list_view, this, this);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showErrorMessage() {
        findViewById(R.id.error_state).setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmptyStateMessage() {
        findViewById(R.id.empty_state).setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToHome(int pos, Book book, ImageView imageView) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(EXTRA_BOOK_ITEM, book);
        intent.putExtra(EXTRA_BOOK_IMAGE_TRANSITION_NAME,
                ViewCompat.getTransitionName(imageView));

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, imageView,
                        ViewCompat.getTransitionName(imageView));

        startActivity(intent, options.toBundle());

    }

    @Override
    public void onDetachedFromWindow() {
        presenter.detachView();
        super.onDetachedFromWindow();
    }

    @Override
    public void onBookClicked(int pos, Book book, ImageView shareImageView) {
        presenter.onItemSelected(pos, book, shareImageView);

    }
}
