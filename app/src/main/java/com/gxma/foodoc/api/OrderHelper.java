package com.gxma.foodoc.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.gxma.foodoc.models.Order;

public class OrderHelper {
    private static final String COLLECTION_NAME = "orders";

    public static CollectionReference getAllOrders() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static Query getOrdersbyState(int state) {
        return OrderHelper.getAllOrders().whereEqualTo("state", state);
    }


    public static Task<Void> createOrder(Order order) {
        return OrderHelper.getAllOrders().document().set(order);
    }

    public static Task<DocumentSnapshot> getOrder(String id) {
        return OrderHelper.getAllOrders().document(id).get();
    }

    public static Task<Void> deleteOrder(String id) {
        return OrderHelper.getAllOrders().document(id).delete();
    }

    public static Task<Void> updateOrder(String id, Order order) {
        return OrderHelper.getAllOrders().document(id).set(order);
    }
}
