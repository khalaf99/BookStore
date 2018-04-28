package com.example.khalaf.bookstore.Fragments;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.data.Book;
import com.example.khalaf.bookstore.util.ActivityLauncter;

public class BookDetailsFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Book book;


    public BookDetailsFragment() {
    }


    public static BookDetailsFragment with(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();

        args.putSerializable(ActivityLauncter.BOOK_KEY, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(ActivityLauncter.BOOK_KEY);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_details, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        ImageView ivBook = view.findViewById(R.id.iv_book);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        Button readbook = view.findViewById(R.id.read_book);

        readbook.setOnClickListener(this);
        toolbar.setTitle(book.getTitle());
        tvTitle.setText(book.getDesc());
        Glide.with(getContext()).load(book.getImageUrl()).into(ivBook);

        return view;
    }

    @Override
    public void onClick(View view) {
        ActivityLauncter.openPDFVIEWR(getContext(),book);
    }
}
