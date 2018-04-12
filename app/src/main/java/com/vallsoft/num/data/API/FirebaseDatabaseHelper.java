package com.vallsoft.num.data.API;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vallsoft.num.domain.IUserRetriever;
import com.vallsoft.num.domain.utils.User;

import static com.vallsoft.num.data.JsonPareserKt.parseJson;


public class FirebaseDatabaseHelper {
    private static final String TAG = FirebaseDatabaseHelper.class.toString();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private IUserRetriever retriever;
    private final String SOURCE_MSG = "Firebase Database";

    public FirebaseDatabaseHelper(IUserRetriever retriever) {
        // Получаем нашу удаленную базу данных
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);

        mFirebaseInstance = FirebaseDatabase.getInstance("https://phonetest-struct.firebaseio.com/");
//        mFirebaseInstance.setLogLevel(Logger.Level.INFO);
        // Получаем из нее таблицу с пользователями
        mFirebaseDatabase = mFirebaseInstance.getReference("Test_Struct_3");
        this.retriever = retriever;
    }

    // Получаем пользователя по телефону
    public void getUserByPhone(final String phone) {
        Long lastPart = Long.parseLong(phone.substring(phone.length() / 2 + 1));
        Long firstPart = Long.parseLong(phone.substring(0, phone.length() / 2 + 1));
        mFirebaseDatabase.child(firstPart.toString()).child(lastPart.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            User localUser = parseJson(new Gson().toJson(dataSnapshot.getValue()));
                            if (localUser != null) {
                                localUser.setPhone(phone);
                                retriever.onUserRecieved(localUser, SOURCE_MSG);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void getUserByPhone(Long phone) {
        getUserByPhone(phone.toString());
    }
}
