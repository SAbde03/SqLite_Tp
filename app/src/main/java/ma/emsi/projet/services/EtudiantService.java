package ma.emsi.projet.services;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ma.emsi.projet.classes.Etudiant;
import ma.emsi.projet.services.EtudiantService;
import ma.emsi.projet.util.MySQLiteHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EtudiantService {
    private static final String TABLE_NAME = "etudiant";
    private static final String KEY_ID = "id";
    private static final String KEY_NOM = "nom";
    private static final String KEY_PRENOM = "prenom";
    private static final String KEY_DATE_NAISSANCE = "date_naissance";
    private static final String KEY_IMAGE_PATH = "image_path";

    private static String[] COLUMNS = {KEY_ID, KEY_NOM, KEY_PRENOM, KEY_DATE_NAISSANCE, KEY_IMAGE_PATH};

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private MySQLiteHelper helper;

    public EtudiantService(Context context) {
        this.helper = new MySQLiteHelper(context);
    }

    public void create(Etudiant e) {
        SQLiteDatabase db = this.helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOM, e.getNom());
        values.put(KEY_PRENOM, e.getPrenom());

        if (e.getDateNaissance() != null) {
            values.put(KEY_DATE_NAISSANCE, DATE_FORMAT.format(e.getDateNaissance()));
        }

        if (e.getImagePath() != null) {
            values.put(KEY_IMAGE_PATH, e.getImagePath());
        }

        db.insert(TABLE_NAME, null, values);
        Log.d("insert", e.getNom());
        db.close();
    }

    public void update(Etudiant e) {
        SQLiteDatabase db = this.helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, e.getId());
        values.put(KEY_NOM, e.getNom());
        values.put(KEY_PRENOM, e.getPrenom());

        if (e.getDateNaissance() != null) {
            values.put(KEY_DATE_NAISSANCE, DATE_FORMAT.format(e.getDateNaissance()));
        } else {
            values.putNull(KEY_DATE_NAISSANCE);
        }

        if (e.getImagePath() != null) {
            values.put(KEY_IMAGE_PATH, e.getImagePath());
        } else {
            values.putNull(KEY_IMAGE_PATH);
        }

        db.update(TABLE_NAME, values, "id = ?", new String[]{e.getId() + ""});
        db.close();
    }

    public Etudiant findById(int id) {
        Etudiant e = null;
        SQLiteDatabase db = this.helper.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, COLUMNS, "id = ?", new String[]{String.valueOf(id)},
                null, null, null, null);
        if (c.moveToFirst()) {
            e = new Etudiant();
            e.setId(c.getInt(0));
            e.setNom(c.getString(1));
            e.setPrenom(c.getString(2));
            String dateStr = c.getString(3);
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    e.setDateNaissance(DATE_FORMAT.parse(dateStr));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }

            String imagePath = c.getString(4);
            if (imagePath != null && !imagePath.isEmpty()) {
                e.setImagePath(imagePath);
            }
        }
        c.close();
        db.close();
        return e;
    }

    public void delete(Etudiant e) {
        SQLiteDatabase db = this.helper.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(e.getId())});
        db.close();
    }

    public List<Etudiant> findAll() {
        List<Etudiant> eds = new ArrayList<>();
        String req = "select * from " + TABLE_NAME;
        SQLiteDatabase db = this.helper.getReadableDatabase();
        Cursor c = db.rawQuery(req, null);
        Etudiant e = null;
        if (c.moveToFirst()) {
            do {
                e = new Etudiant();
                e.setId(c.getInt(0));
                e.setNom(c.getString(1));
                e.setPrenom(c.getString(2));
                String dateStr = c.getString(3);
                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        e.setDateNaissance(DATE_FORMAT.parse(dateStr));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                }

                String imagePath = c.getString(4);
                if (imagePath != null && !imagePath.isEmpty()) {
                    e.setImagePath(imagePath);
                }

                eds.add(e);
                Log.d("id = ", e.getId() + "");
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return eds;
    }
}