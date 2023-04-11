package com.sweetapps.kontamaboutique.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.sweetapps.kontamaboutique.Fragments.CartFragment;
import com.sweetapps.kontamaboutique.Models.CartItem;
import com.sweetapps.kontamaboutique.Prevalent.Prevalent;
import com.sweetapps.kontamaboutique.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    List<CartItem> cartItems;
    Context context;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_cart, parent, false);
        context = parent.getContext();
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        final CartItem cartItem = cartItems.get(position);
        int i = holder.getAdapterPosition();
        int u = i;

        Picasso.get().load(cartItem.getProductImg()).into(holder.productImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(cartItem.getProductImg()).into(holder.productImage);

            }
        });

        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText("K" + String.valueOf(cartItem.getPrice()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                CharSequence[] options = new CharSequence[]{
                        "Remove",
                        "Check Out"
                };
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Prevalent.CART_COLLECTION.document(FirebaseAuth.getInstance().getUid())
                                        .collection("Cart")
                                        .document(cartItem.getItemId())
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Prevalent.CURRENT_USER.setCartPrice(Prevalent.CURRENT_USER.getCartPrice() -
                                                        cartItem.getPrice());

                                                if (Prevalent.CURRENT_USER.getCartPrice() < 0) {
                                                    Prevalent.CURRENT_USER.setCartPrice(0);
                                                }

                                                Prevalent.USERS_COLLECTION.document(Prevalent.CURRENT_USER.getUserID())
                                                        .set(Prevalent.CURRENT_USER)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                cartItems.remove(holder.getAdapterPosition());
                                                                notifyItemRemoved(holder.getAdapterPosition());


                                                                CartFragment.totalPrice.setText("K" + String.valueOf(Prevalent.CURRENT_USER.getCartPrice()));
                                                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                                break;
                            case 1:
                                Toast.makeText(context, "Check Out", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productPrice;
        TextView productName;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.cart_product_image);
            productPrice = itemView.findViewById(R.id.cart_product_price);
            productName = itemView.findViewById(R.id.cart_product_name);
        }
    }
}
