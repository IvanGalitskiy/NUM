package com.vallsoft.num.data;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import io.reactivex.Single;

public class AvatarStorage {
    private StorageReference storageRef;
    private FirebaseStorage storage;

    public AvatarStorage() {
        storage = FirebaseStorage.getInstance();
    }

    public Single<String> uploadAvatar(Bitmap bitmap, String phone) {
        return Single.create(emitter -> {
            storageRef = storage.getReference().child(phone + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(emitter::onError)
                    .addOnSuccessListener(taskSnapshot -> {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> emitter.onSuccess(uri.toString()))
                                .addOnFailureListener(emitter::onError);

                    });
        });
    }

    public Single<String> uploadAvatar(Uri uri, String phone) {
        return Single.create(emitter -> {
            storageRef = storage.getReference().child(phone + ".jpg");
            UploadTask uploadTask = storageRef.putFile(uri);
            uploadTask.addOnFailureListener(emitter::onError)
                    .addOnSuccessListener(taskSnapshot -> {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        storageRef.getDownloadUrl().addOnSuccessListener(img -> emitter.onSuccess(img.toString()))
                                .addOnFailureListener(emitter::onError);
                    });
        });
    }
}
