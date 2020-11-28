package com.gxma.foodoc.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gxma.foodoc.models.Category;

import java.util.List;

public class CategoryHelper {
    private static final String COLLECTION_NAME = "categories";
    public static List<DocumentSnapshot> categories;

    public static CollectionReference getAllCategories(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Task<Void> createCategory(Category category) {
        return CategoryHelper.getAllCategories().document().set(category);

    }

    public static Task<DocumentSnapshot> getCategory(String idcategory) {
        return CategoryHelper.getAllCategories().document(idcategory).get();
    }

    public static Task<Void> updateCategory(String idCategory, Category cat) {
        return CategoryHelper.getAllCategories().document(idCategory).set(cat);
    }

    public static Task<Void> deleteCategory(String name) {
        return CategoryHelper.getAllCategories().document(name).delete();
    }

}
