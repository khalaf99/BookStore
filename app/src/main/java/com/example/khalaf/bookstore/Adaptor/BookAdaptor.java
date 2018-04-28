package com.example.khalaf.bookstore.Adaptor;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.khalaf.bookstore.R;
import com.example.khalaf.bookstore.data.Book;
import com.example.khalaf.bookstore.util.ActivityLauncter;
import com.example.khalaf.bookstore.util.Constans;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

/**
 * Created by KHALAF on 11/3/2017.
 */


// lazeem creation view holder first
public class BookAdaptor extends RecyclerView.Adapter<BookAdaptor.ViewHolder> {
    private ArrayList<Book> books;
    private Context context;
    private OnBookClickListener onBookClickListener;
    Book book;
    private static final int ITEM_DELETE = 1;
    private static final int ITEM_EDIT = 2;


    public BookAdaptor(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
        if (context instanceof OnBookClickListener) {
            onBookClickListener = (OnBookClickListener) context;
        } else {
            throw new RuntimeException("Context must implement OnBookClickListener");
        }

    }

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.book = books.get(position);
        holder.tvtitle.setText(holder.book.getTitle());
        holder.tvprice.setText(String.valueOf(holder.book.getPrice()));
        Glide.with(context).load(holder.book.getImageUrl()).into(holder.ivbook);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBookClickListener.onBookClick(holder.book);
            }
        });

        holder.ivoverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder);
            }
        });

    }

    private void showPopupMenu(final ViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(context, holder.ivoverflow);
        popupMenu.getMenu().add(1, ITEM_DELETE, 1, "Delete");
        popupMenu.getMenu().add(1, ITEM_EDIT, 2, "Edit");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == ITEM_DELETE) {

                    showConfirmationDeleteDialog(holder.book.getId());

                    return true;
                } else if (item.getItemId() == ITEM_EDIT) {
                    ActivityLauncter.openEditBookActivity(context, holder.book);
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showConfirmationDeleteDialog(final String id) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Book")
                .setMessage("Are u sure ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookById(id);
                    }
                })
                .show();

    }

    private void deleteBookById(String id) {
        FirebaseDatabase
                .getInstance()
                .getReference(Constans.REF_BOOK)
                .child(id)
                .removeValue();
        FirebaseStorage
                .getInstance()
                .getReference(Constans.BOOk_IMAGES_FOLDER)
                .child(id + ".jpg")
                .delete();
        FirebaseStorage
                .getInstance()
                .getReference(Constans.BOOK_PDF_FOLDER)
                .child(id + ".pdf")
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                } else {
                    if (task.getException() != null)
                        task.getException().printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    // holer da zy ma7fazaa
    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvprice, tvtitle;
        ImageView ivbook, ivoverflow;
        Book book;

        // iteam view da howa ell view el kbeeraa
        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvprice = view.findViewById(R.id.tv_priice);
            tvtitle = view.findViewById(R.id.tv_title);
            ivbook = view.findViewById(R.id.iv_book);
            ivoverflow = view.findViewById(R.id.iv_overflow_menu);
        }
    }


}
