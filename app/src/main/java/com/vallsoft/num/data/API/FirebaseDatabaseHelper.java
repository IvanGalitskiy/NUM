package com.vallsoft.num.data.API;

import android.accounts.AuthenticatorException;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.vallsoft.num.domain.IUserRetriever;
import com.vallsoft.num.domain.utils.FirebaseUser;
import com.vallsoft.num.domain.utils.User;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;

import static com.vallsoft.num.data.JsonConverterKt.parseJson;


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

//        mFirebaseInstance = FirebaseDatabase.getInstance();
//        mFirebaseDatabase = mFirebaseInstance.getReference();


        this.retriever = retriever;
    }

    // Получаем пользователя по телефону
    public void getUserByPhone(String phone) {
        if (phone != null && !phone.isEmpty()) {
            if (phone.startsWith("+"))
                phone = phone.substring(1, phone.length());
            phone = phone.replaceAll("[^\\d.]", "");
            Long lastPart = Long.parseLong(phone.substring(phone.length() / 2 + 1));
            Long firstPart = Long.parseLong(phone.substring(0, phone.length() / 2 + 1));
            String finalPhone = phone;
            mFirebaseDatabase.child(firstPart.toString()).child(lastPart.toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User defUser = new User();
                            defUser.setPhone(finalPhone);
                            if (dataSnapshot.getValue() != null) {
                                User localUser = parseJson(new Gson().toJson(dataSnapshot.getValue()));
                                if (localUser != null) {
                                    localUser.setPhone(finalPhone);
                                    retriever.onUserRecieved(localUser, SOURCE_MSG);
                                } else {
                                    retriever.onUserRecieved(defUser, "");
                                }
                            } else {
                                retriever.onUserRecieved(defUser, "");
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    public Completable updateUserByPhone(User user) {
        return Completable.create(emitter -> {
            if (!user.getPhone().isEmpty()) {
                Long lastPart = Long.parseLong(user.getPhone().substring(user.getPhone().length() / 2 + 1));
                Long firstPart = Long.parseLong(user.getPhone().substring(0, user.getPhone().length() / 2 + 1));
                mFirebaseDatabase.child(firstPart.toString()).child(lastPart.toString()).setValue(new FirebaseUser(user)).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emitter.onError(e);
                    }
                }).addOnSuccessListener(aVoid -> {
                    emitter.onComplete();
                });
            }
        });
    }
    public void removeProfile(String phone){
        if (phone != null && !phone.isEmpty()) {
            if (phone.startsWith("+"))
                phone = phone.substring(1, phone.length());
            phone = phone.replaceAll("[^\\d.]", "");
            Long lastPart = Long.parseLong(phone.substring(phone.length() / 2 + 1));
            Long firstPart = Long.parseLong(phone.substring(0, phone.length() / 2 + 1));
            String finalPhone = phone;

            mFirebaseDatabase.child(firstPart.toString()).child(lastPart.toString()).removeValue();
        }
    }
    public Completable updateAvatar(String url, String phone) {
        if (phone != null && !phone.isEmpty()) {
            if (phone.startsWith("+"))
                phone = phone.substring(1, phone.length());
            phone = phone.replaceAll("[^\\d.]", "");

            String finalPhone = phone;
            return Completable.create(emitter -> {

                Long lastPart = Long.parseLong(finalPhone.substring(finalPhone.length() / 2 + 1));
                Long firstPart = Long.parseLong(finalPhone.substring(0, finalPhone.length() / 2 + 1));
                mFirebaseDatabase
                        .child(firstPart.toString())
                        .child(lastPart.toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Map<String, Object> postValues = new HashMap<String, Object>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    postValues.put(snapshot.getKey(), snapshot.getValue());
                                }
                                postValues.put("gr0_avatar", url);
                                mFirebaseDatabase
                                        .child(firstPart.toString())
                                        .child(lastPart.toString())
                                        .updateChildren(postValues)
                                        .addOnFailureListener(emitter::onError)
                                        .addOnSuccessListener(aVoid -> emitter.onComplete());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                emitter.onError(databaseError.toException());
                            }
                        });

            });
        } else  return Completable.error(AuthenticatorException::new);
    }

    public void getUserByPhone(Long phone) {
        getUserByPhone(phone.toString());
    }
}
