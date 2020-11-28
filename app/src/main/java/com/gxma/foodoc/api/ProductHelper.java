package com.gxma.foodoc.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.gxma.foodoc.models.Product;

public class ProductHelper {
    private static final String COLLECTION_NAME = "products";
    // --- GET ---

    public static CollectionReference getAllProducts(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getAllProductsOfCategory(String category){
        return ProductHelper.getAllProducts().whereEqualTo("category",category);
    }

    // --- CREATE ---

    public static Task<DocumentSnapshot> getProduct(String idProduct) {
        return ProductHelper.getAllProducts().document(idProduct).get();
    }

    public static Task<Void> createProduct(Product product){
        return ProductHelper.getAllProducts().document().set(product);
    }

    public static Task<Void> modifyProduct(String idProduct, Product product) {
        return ProductHelper.getAllProducts().document(idProduct).set(product);
    }

    public static Task<Void> deleteProduct(String idProduct) {
        return ProductHelper.getAllProducts().document(idProduct).delete();

    }

}
