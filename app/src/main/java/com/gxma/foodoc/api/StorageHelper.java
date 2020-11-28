package com.gxma.foodoc.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StorageHelper {
    public static Task<Void> deleteFile(String urlImage) {
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(urlImage);
        return photoRef.delete();
    }
}
