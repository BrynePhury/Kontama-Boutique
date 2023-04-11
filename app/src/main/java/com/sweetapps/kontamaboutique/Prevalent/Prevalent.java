package com.sweetapps.kontamaboutique.Prevalent;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sweetapps.kontamaboutique.Models.ProductModel;
import com.sweetapps.kontamaboutique.Models.User;

import java.util.ArrayList;
import java.util.List;

public class Prevalent {

    //Current active user
    public static User CURRENT_USER;

    //Compression level for all images
    public static final int UPLOAD_IMAGE_QUALITY = 30;


    //Constant Database References
    public static final CollectionReference PRODUCTS_COLLECTION =
            FirebaseFirestore.getInstance().collection("Products");

    public static final CollectionReference PROMOTIONS_COLLECTION =
            FirebaseFirestore.getInstance().collection("Promotions");

    public static final CollectionReference CART_COLLECTION =
            FirebaseFirestore.getInstance().collection("Carts");

    public static final CollectionReference ORDER_COLLECTION =
            FirebaseFirestore.getInstance().collection("Orders");

    public static final CollectionReference USERS_COLLECTION =
            FirebaseFirestore.getInstance().collection("Users");

    public static final StorageReference USER_IMAGES_STORAGE =
            FirebaseStorage.getInstance().getReference().child("User Images");




}
