package de.domjos.schooltools.adapter.syncAdapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.schooltools.R;
import de.domjos.schooltools.activities.MainActivity;
import de.domjos.schooltoolslib.model.timetable.Teacher;
import de.domjos.schooltools.helper.Helper;
import de.domjos.schooltools.helper.SQLite;

import static android.provider.ContactsContract.Groups;
import static android.provider.ContactsContract.CommonDataKinds.StructuredName;
import static android.provider.ContactsContract.CommonDataKinds.Note;
import static android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import static android.provider.ContactsContract.Contacts;
import static android.provider.ContactsContract.RawContacts;


public class ContactSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String group;
    private final SQLite sql;
    private long group_id = 0;
    private final String type;
    private final Date last_sync;

    public ContactSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.group = MainActivity.globals.getUserSettings().getSyncContactsGroupName();
        this.sql = MainActivity.globals.getSqLite();
        this.type = ContactSyncAdapter.class.getSimpleName();
        this.last_sync = this.sql.getLastSyncDate(this.type);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if(MainActivity.globals.getUserSettings().isSynContactsTurnOn()) {
            try {
                this.addOrGetGroup(provider, account);

                Map<Teacher, Date> contactsTeacherMap = this.getTeachersFromContacts(provider, account);
                List<Teacher> savedTeachers = this.sql.getTeachers("");

                for(Teacher teacher : savedTeachers) {
                    boolean available = false, dirty = false;
                    for(Map.Entry<Teacher, Date> entry : contactsTeacherMap.entrySet()) {
                        Teacher contact = entry.getKey();
                        String contactFirst = contact.getFirstName() == null ? "" : contact.getFirstName().toLowerCase().trim();
                        String contactLast = contact.getLastName() == null ? "" : contact.getLastName().toLowerCase().trim();
                        String teacherFirst = teacher.getFirstName() == null ? "" : teacher.getFirstName().toLowerCase().trim();
                        String teacherLast = teacher.getLastName() == null ? "" : teacher.getLastName().toLowerCase().trim();

                        if(contactFirst.equals(teacherFirst) && contactLast.equals(teacherLast)) {

                            if(entry.getValue().after(last_sync)) {
                                teacher.setDescription(contact.getDescription());
                                teacher.setID(contact.getID());
                                dirty = true;
                            }
                            available = true;
                        }
                    }

                    if(!available) {
                        teacher.setID(0);
                        this.saveTeachersToContact(teacher, provider, account);
                    } else {
                        if(dirty) {
                            this.saveTeachersToContact(teacher, provider, account);
                        }
                    }
                }

                contactsTeacherMap = this.getTeachersFromContacts(provider, account);
                for(Map.Entry<Teacher, Date> entry : contactsTeacherMap.entrySet()) {
                    Teacher contact = entry.getKey();
                    boolean available = false, dirty = false;
                    if(contact!=null) {
                        for(Teacher teacher : savedTeachers) {
                            if(teacher!=null) {
                                String contactFirst = contact.getFirstName() == null ? "" : contact.getFirstName().toLowerCase().trim();
                                String contactLast = contact.getLastName() == null ? "" : contact.getLastName().toLowerCase().trim();
                                String teacherFirst = teacher.getFirstName() == null ? "" : teacher.getFirstName().toLowerCase().trim();
                                String teacherLast = teacher.getLastName() == null ? "" : teacher.getLastName().toLowerCase().trim();

                                if(contactFirst.equals(teacherFirst) && contactLast.equals(teacherLast)) {
                                    String teacherDescription = teacher.getDescription() == null ? "" : teacher.getDescription().trim().toLowerCase();
                                    String contactDescription = contact.getDescription() == null ? "" : contact.getDescription().trim().toLowerCase();

                                    if(!teacherDescription.equals(contactDescription)) {
                                        contact.setDescription(teacher.getDescription());
                                        contact.setID(teacher.getID());
                                        dirty = true;
                                    }
                                    available = true;
                                }
                            }
                        }
                    }

                    if(contact!=null) {
                        if(!available) {
                            contact.setID(0);
                            this.sql.insertOrUpdateTeacher(contact);
                        } else {
                            if(dirty) {
                                this.sql.insertOrUpdateTeacher(contact);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getContext());
            }

            this.sql.addSync(this.type);
        }
    }


    private void addOrGetGroup(ContentProviderClient provider, Account account) throws Exception {
        Long id = this.getContactLists(provider, account).get(this.group);
        if(id!=null) {
            this.group_id = id;
        } else {
            final ContentValues contentValues = new ContentValues();
            contentValues.put(Groups.ACCOUNT_NAME, account.name);
            contentValues.put(Groups.ACCOUNT_TYPE, account.type);
            contentValues.put(Groups.TITLE, this.group);
            contentValues.put(Groups.GROUP_VISIBLE, 1);
            contentValues.put(Groups.SHOULD_SYNC, true);
            final Uri newGroupUri = provider.insert(ContactSyncAdapter.asSyncAdapter(Groups.CONTENT_URI, account), contentValues);
            this.group_id = ContentUris.parseId(newGroupUri);
        }
    }

    private static Uri asSyncAdapter(Uri uri, Account account) {
        return uri.buildUpon()
                .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(ContactsContract.PRIMARY_ACCOUNT_NAME, account.name)
                .appendQueryParameter(ContactsContract.PRIMARY_ACCOUNT_TYPE, account.type).build();
    }

    private Map<String, Long> getContactLists(ContentProviderClient provider, Account account) throws Exception {
        Map<String, Long> directoryMap = new LinkedHashMap<>();
        String[] projection = new String[]{Groups._ID, Groups.TITLE};

        Cursor groupCursor = provider.query(asSyncAdapter(Groups.CONTENT_URI, account), projection, null, null, null);
        if(groupCursor!=null) {
            while (groupCursor.moveToNext()) {
                directoryMap.put(groupCursor.getString(1), groupCursor.getLong(0));
            }
            groupCursor.close();
        }
        return directoryMap;
    }

    private Map<Teacher, Date> getTeachersFromContacts(ContentProviderClient provider, Account account) throws Exception {
        Map<Teacher, Date> teachers = new LinkedHashMap<>();

        Date dt = new Date();
        String[] projection;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            projection = new String[]{Contacts._ID, Contacts.CONTACT_LAST_UPDATED_TIMESTAMP};
        } else {
            projection = new String[]{Contacts._ID};
        }

        Cursor teacherCursor = provider.query(asSyncAdapter(Contacts.CONTENT_URI, account), projection, null, null, null);
        if(teacherCursor!=null) {
            while (teacherCursor.moveToNext()) {
                Teacher teacher = new Teacher();
                teacher.setID(teacherCursor.getInt(teacherCursor.getColumnIndex(Contacts._ID)));

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    dt = new Date(teacherCursor.getLong(teacherCursor.getColumnIndex(Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
                }

                String[] structuredProjection = new String[]{StructuredName.FAMILY_NAME, StructuredName.GIVEN_NAME};
                String selection = StructuredName.CONTACT_ID + "=?";
                String[] arguments = new String[]{String.valueOf(teacher.getID())};
                Cursor structuredCursor = provider.query(asSyncAdapter(ContactsContract.Data.CONTENT_URI, account),structuredProjection, selection, arguments, null);
                if(structuredCursor!=null) {
                    while (structuredCursor.moveToNext()) {
                        String lastName = structuredCursor.getString(structuredCursor.getColumnIndex(StructuredName.FAMILY_NAME));
                        String firstName = structuredCursor.getString(structuredCursor.getColumnIndex(StructuredName.GIVEN_NAME));
                        if(lastName!=null) {
                            teacher.setLastName(lastName);
                        }
                        if(firstName!=null) {
                            teacher.setFirstName(firstName);
                        }
                    }
                    structuredCursor.close();
                }

                String[] noteProjection = new String[]{Note.NOTE};
                selection = Note.CONTACT_ID + "=?";
                Cursor noteCursor = provider.query(asSyncAdapter(ContactsContract.Data.CONTENT_URI, account), noteProjection, selection, arguments, null);
                if(noteCursor!=null) {
                    while (noteCursor.moveToNext()) {
                        teacher.setDescription(noteCursor.getString(noteCursor.getColumnIndex(Note.NOTE)));
                    }
                    noteCursor.close();
                }
                teachers.put(teacher, dt);
            }
            teacherCursor.close();
        }
        return teachers;
    }

    private void saveTeachersToContact(Teacher teacher, ContentProviderClient provider, Account account) throws Exception {
        long contact_id = 0;
        ContentValues contentValues = new ContentValues();
        contentValues.put(RawContacts.ACCOUNT_NAME, account.name);
        contentValues.put(RawContacts.ACCOUNT_TYPE, account.type);
        if(teacher.getID()==0) {
            Uri uri = provider.insert(asSyncAdapter(RawContacts.CONTENT_URI, account), contentValues);
            contact_id = ContentUris.parseId(uri);
        } else {
            String selection = RawContacts._ID + "=?";
            provider.update(asSyncAdapter(RawContacts.CONTENT_URI, account), contentValues, selection, new String[]{String.valueOf(teacher.getID())});
        }

        contentValues = new ContentValues();
        contentValues.put(StructuredName.FAMILY_NAME, teacher.getLastName());
        contentValues.put(StructuredName.GIVEN_NAME, teacher.getFirstName());
        contentValues.put(StructuredName.RAW_CONTACT_ID, contact_id);
        contentValues.put(StructuredName.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        provider.insert(asSyncAdapter(ContactsContract.Data.CONTENT_URI, account), contentValues);

        contentValues = new ContentValues();
        contentValues.put(GroupMembership.GROUP_ROW_ID, this.group_id);
        contentValues.put(GroupMembership.RAW_CONTACT_ID, contact_id);
        contentValues.put(GroupMembership.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE);
        provider.insert(asSyncAdapter(ContactsContract.Data.CONTENT_URI, account), contentValues);

        contentValues = new ContentValues();
        contentValues.put(Note.NOTE, teacher.getDescription());
        contentValues.put(Note.RAW_CONTACT_ID, contact_id);
        contentValues.put(Note.MIMETYPE, Note.CONTENT_ITEM_TYPE);
        provider.insert(asSyncAdapter(ContactsContract.Data.CONTENT_URI, account), contentValues);
    }
}
