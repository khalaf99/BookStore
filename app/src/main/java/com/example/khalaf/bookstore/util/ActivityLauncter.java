package com.example.khalaf.bookstore.util;

import android.content.Context;
import android.content.Intent;

import com.example.khalaf.bookstore.Fragments.BookDetailsFragment;
import com.example.khalaf.bookstore.activities.AddBookActivity;
import com.example.khalaf.bookstore.activities.EditBookActivity;
import com.example.khalaf.bookstore.activities.EditProfileActivity;
import com.example.khalaf.bookstore.activities.LoginActivity;
import com.example.khalaf.bookstore.activities.MybookAvtivity;
import com.example.khalaf.bookstore.activities.PDFViewrActivity;
import com.example.khalaf.bookstore.activities.RegistrationActivity;
import com.example.khalaf.bookstore.data.Book;
import com.example.khalaf.bookstore.data.Publisher;

/**
 * Created by KHALAF on 10/4/2017.
 */

public final class ActivityLauncter {

    public static final String BOOK_KEY ="khalaf";
    public static final String Publisher_KEY ="publisher";

    public static void openLoginActivity(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }

    public static void openRegisterationActivity(Context context) {
        Intent i = new Intent(context, RegistrationActivity.class);
        context.startActivity(i);
    }

    public static void openMyBooksActivity(Context context) {
        Intent i = new Intent(context, MybookAvtivity.class);
        context.startActivity(i);
    }

    public static void openAddBookActivity(Context context) {
        Intent i = new Intent(context, AddBookActivity.class);
        context.startActivity(i);
    }

    public static void openEditBookActivity(Context context , Book book) {
        Intent i = new Intent(context, EditBookActivity.class);
        i.putExtra(BOOK_KEY , book);
        context.startActivity(i);
    }

    public static void openEditProfileActivity(Context context , Publisher publisher) {
        Intent i = new Intent(context, EditProfileActivity.class);
        i.putExtra(Publisher_KEY, publisher);
        context.startActivity(i);
    }

    public static void openPDFVIEWR(Context context , Book book) {
        Intent i = new Intent(context, PDFViewrActivity.class);
        i.putExtra(BOOK_KEY, book);
        context.startActivity(i);
    }


}
