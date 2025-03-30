package com.example.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ma.emsi.projet.services.EtudiantService;
import ma.emsi.projet.classes.Etudiant;
import ma.emsi.projet.util.ImageUtil;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText editNom, editPrenom, editId;
    private TextView txtDateNaissance;
    private ImageView imageEtudiant;
    private Button btnValider, btnChercher, btnSupprimer, btnList, btnSelectDate, btnSelectImage;
    private TextView txtResult;
    private EtudiantService etudiantService;

    private Date selectedDate = null;
    private String selectedImagePath = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNom = findViewById(R.id.nom);
        editPrenom = findViewById(R.id.prenom);
        editId = findViewById(R.id.id);
        txtDateNaissance = findViewById(R.id.date_naissance);
        imageEtudiant = findViewById(R.id.image_etudiant);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnValider = findViewById(R.id.bn);
        btnChercher = findViewById(R.id.load);
        btnSupprimer = findViewById(R.id.delete);
        btnList = findViewById(R.id.viewTable);
        txtResult = findViewById(R.id.res);
        etudiantService = new EtudiantService(this);

        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEtudiant();
            }
        });

        btnChercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findEtudiant();
            }
        });

        btnSupprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEtudiant();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListEtudiant.class);
                startActivity(intent);
            }
        });

        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });

        imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDate = calendar.getTime();
                        updateDateLabel();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateLabel() {
        if (selectedDate != null) {
            txtDateNaissance.setText(dateFormat.format(selectedDate));
        } else {
            txtDateNaissance.setText("Non sélectionnée");
        }
    }

    private void saveEtudiant() {
        String nom = editNom.getText().toString().trim();
        String prenom = editPrenom.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        Etudiant etudiant = new Etudiant(nom, prenom);

        if (selectedDate != null) {
            etudiant.setDateNaissance(selectedDate);
        }

        if (selectedImagePath != null) {
            etudiant.setImagePath(selectedImagePath);
        }

        etudiantService.create(etudiant);

        Toast.makeText(this, "Étudiant ajouté avec succès", Toast.LENGTH_SHORT).show();

        editNom.setText("");
        editPrenom.setText("");
        selectedDate = null;
        selectedImagePath = null;
        updateDateLabel();
        imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
    }

    private void findEtudiant() {
        String idStr = editId.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un ID", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Etudiant etudiant = etudiantService.findById(id);

            if (etudiant != null) {
                editNom.setText(etudiant.getNom());
                editPrenom.setText(etudiant.getPrenom());

                if (etudiant.getDateNaissance() != null) {
                    selectedDate = etudiant.getDateNaissance();
                    calendar.setTime(selectedDate);
                    updateDateLabel();
                } else {
                    selectedDate = null;
                    txtDateNaissance.setText("Non sélectionnée");
                }

                selectedImagePath = etudiant.getImagePath();
                if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                    Bitmap bitmap = ImageUtil.loadBitmapFromPath(selectedImagePath);
                    if (bitmap != null) {
                        imageEtudiant.setImageBitmap(bitmap);
                    } else {
                        imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
                    }
                } else {
                    imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
                }

                txtResult.setText("Étudiant trouvé: " + etudiant.getNom() + " " + etudiant.getPrenom());
            } else {
                txtResult.setText("Aucun étudiant trouvé avec cet ID");
                editNom.setText("");
                editPrenom.setText("");
                selectedDate = null;
                selectedImagePath = null;
                txtDateNaissance.setText("Non sélectionnée");
                imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID invalide", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEtudiant() {
        String idStr = editId.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un ID", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Etudiant etudiant = etudiantService.findById(id);

            if (etudiant != null) {
                etudiantService.delete(etudiant);
                Toast.makeText(this, "Étudiant supprimé avec succès", Toast.LENGTH_SHORT).show();

                editNom.setText("");
                editPrenom.setText("");
                editId.setText("");
                txtResult.setText("");
                selectedDate = null;
                selectedImagePath = null;
                txtDateNaissance.setText("Non sélectionnée");
                imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
            } else {
                txtResult.setText("Aucun étudiant trouvé avec cet ID");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID invalide", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            try {
                selectedImagePath = ImageUtil.saveImageToPrivateStorage(this, selectedImageUri);

                if (selectedImagePath != null) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    imageEtudiant.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

