package com.vallsoft.num.data;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;
import com.vallsoft.num.data.database.BillingPreference;
import com.vallsoft.num.domain.utils.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


// Клас, который получает список контактов на телефоне
public class ContactHelper {
    private Context mContext;
    private List<User> contacts = new ArrayList<>();
    private BillingPreference preference;

    public ContactHelper(Context context) {
        this.mContext = context;
        preference = new BillingPreference(context);
    }

    // ищем пользователя в сохраненном списке контактов
    public User getContactByPhoneNumber(String phone) {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        } else {
            String name = getContactName(phone);
            if (name != null) {
                User user = new User();
                user.setPhone(phone);
                user.setName(name);
                return user;
            }
            return null;
        }
    }

    private String getContactName(String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = null;
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }


    public User getUserByPhone(Long phone) {
        for (User u : contacts) {
            if (phone.toString().equals(u.getPhone())) {
                return u;
            }
        }
        return null;
    }

    /*Сохраняем результат поиска в контакты*/
    public void saveUser(final User user) {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            if (!user.getPhone().isEmpty() &&
                    user.getName() != null && !user.getName().isEmpty() && preference.getBillingGranted()) {
                contacts.add(user);
                final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                int rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                //Phone Number
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, user.getPhone())
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build());

                if (user.getName() != null) {
                    //Display name/Contact name
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                    rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, user.getName())
                            .build());
                }
                //Email details
//        ops.add(ContentProviderOperation
//                .newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
//                        rawContactInsertIndex)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "abc@aho.com")
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, "2").build());


                //Postal Address
                if (user.getAddress() != null && !user.getAddress().isEmpty() &&
                        user.getCountry() != null && !user.getCountry().isEmpty() &&
                        user.getRegion() != null && !user.getRegion().isEmpty()) {


                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                    rawContactInsertIndex)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, "Postbox")
//
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, "street")

//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, "postcode")

                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, user.getCountry())

                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, user.getRegion())

                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, user.getAddress())

                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, "3")


                            .build());
                }
                if (user.getCategory() != null) {
                    //Organization details
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, user.getCategory())
                            .build());
                }
                //IM details
                if (user.getOperator() != null) {
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
                                    rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Im.DATA, user.getOperator())
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE )
//                .withValue(ContactsContract.CommonDataKinds.Im.DATA5, "2")
                            .build());
                }
                new BitmapSavingTask(mContext, ops, user).execute();
            }
        }
    }

    private static class BitmapSavingTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> context;
        private ArrayList<ContentProviderOperation> ops;
        private User user;

        private BitmapSavingTask(Context context, ArrayList<ContentProviderOperation> ops, User u) {
            this.context = new WeakReference<>(context);
            this.ops = ops;
            this.user = u;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                Bitmap bmp = null;
                try {
                    bmp = Picasso.get().load(user.getAvatar()).resize(500, 500).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    Crashlytics.log(2, "Bitmap", "size too large");
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bmp != null) {

                    bmp.compress(Bitmap.CompressFormat.PNG, 1, stream);
                    byte[] byteArray = stream.toByteArray();
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray)
                            .build());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                context.get().getContentResolver().applyBatch(
                        ContactsContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
                Crashlytics.log(2, "Bitmap", "size too large");
            } catch (OperationApplicationException e) {
                e.printStackTrace();
                Crashlytics.log(2, "Bitmap", "size too large");

            }
        }
    }

}
