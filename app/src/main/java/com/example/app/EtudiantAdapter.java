package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ma.emsi.projet.classes.Etudiant;
import ma.emsi.projet.util.ImageUtil;

public class EtudiantAdapter extends RecyclerView.Adapter<EtudiantAdapter.EtudiantViewHolder> {
    private Context context;
    private List<Etudiant> etudiantList;
    private OnEtudiantListener onEtudiantListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public EtudiantAdapter(Context context, List<Etudiant> etudiantList, OnEtudiantListener onEtudiantListener) {
        this.context = context;
        this.etudiantList = etudiantList;
        this.onEtudiantListener = onEtudiantListener;
    }

    @NonNull
    @Override
    public EtudiantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_etudiant, parent, false);
        return new EtudiantViewHolder(view, onEtudiantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EtudiantViewHolder holder, int position) {
        Etudiant etudiant = etudiantList.get(position);

        holder.textId.setText(String.valueOf(etudiant.getId()));
        holder.textNom.setText(etudiant.getNom());
        holder.textPrenom.setText(etudiant.getPrenom());

        if (etudiant.getDateNaissance() != null) {
            holder.textDateNaissance.setText(dateFormat.format(etudiant.getDateNaissance()));
        } else {
            holder.textDateNaissance.setText("-");
        }

        if (etudiant.getImagePath() != null && !etudiant.getImagePath().isEmpty()) {
            Bitmap bitmap = ImageUtil.loadBitmapFromPath(etudiant.getImagePath());
            if (bitmap != null) {
                holder.imageEtudiant.setImageBitmap(bitmap);
            } else {
                holder.imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
            }
        } else {
            holder.imageEtudiant.setImageResource(R.drawable.ic_person_placeholder);
        }
    }


    @Override
    public int getItemCount() {
        return etudiantList.size();
    }

    public class EtudiantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textId, textNom, textPrenom, textDateNaissance;
        ImageView imageEtudiant;
        OnEtudiantListener onEtudiantListener;

        public EtudiantViewHolder(@NonNull View itemView, OnEtudiantListener onEtudiantListener) {
            super(itemView);
            textId = itemView.findViewById(R.id.text_id);
            textNom = itemView.findViewById(R.id.text_nom);
            textPrenom = itemView.findViewById(R.id.text_prenom);
            textDateNaissance = itemView.findViewById(R.id.text_date_naissance);
            imageEtudiant = itemView.findViewById(R.id.image_etudiant);
            this.onEtudiantListener = onEtudiantListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEtudiantListener.onEtudiantClick(getAdapterPosition());
        }
    }

    public interface OnEtudiantListener {
        void onEtudiantClick(int position);
    }
}
