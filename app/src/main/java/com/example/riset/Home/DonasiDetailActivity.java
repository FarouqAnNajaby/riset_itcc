package com.example.riset.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.riset.Berdonasi.Model.BerdonasiUangModel;
import com.example.riset.Donasi.BerdonasiStep1Activity;
import com.example.riset.Home.Adapter.DonaturDetailDonasiAdapter;
import com.example.riset.Home.Adapter.TerdekatKamuAdapter;
import com.example.riset.Home.Model.ButuhSegeraModel;
import com.example.riset.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DonasiDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.textJudul)
    TextView txtJudul;
    @BindView(R.id.textTerkumpul)
    TextView txtTerkumpul;
    @BindView(R.id.textKetTerkumpul)
    TextView txtKetTerkumpul;
    @BindView(R.id.imageProfile)
    ImageView imgProfile;
    @BindView(R.id.textPengguna)
    TextView txtPengguna;
    @BindView(R.id.textSisaHari)
    TextView txtSisaHari;
    @BindView(R.id.textDeskripsi)
    TextView txtDeskripsi;
    @BindView(R.id.relative2)
    RelativeLayout relativeLayout;
    @BindView(R.id.scroll1)
    ScrollView scrollView;

    private List<BerdonasiUangModel> list = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DonaturDetailDonasiAdapter donaturDetailDonasiAdapter;
    String id = null;
    String judul = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi_detail);
        ButterKnife.bind(this);

        Intent i = getIntent();
        id = i.getStringExtra("id");

        support_action_bar();

        donaturDetailDonasiAdapter = new DonaturDetailDonasiAdapter(this, list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(donaturDetailDonasiAdapter);

        get_data_donatur();
        get_data_detail_donasi();
        Log.d("TAG", "id diterima => "+id);
    }

    private void support_action_bar(){
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void get_data_detail_donasi(){
        db.collection("Posting")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ButuhSegeraModel model = null;
                        model = documentSnapshot.toObject(ButuhSegeraModel.class);
                        txtJudul.setText(model.getJudul());
                        txtDeskripsi.setText(model.getDeskripsi());
                    }
                });
    }

    private void get_data_donatur(){
        relativeLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        db.collection("Posting")
                .document(id)
                .collection("berdonasi")
                .limit(3)
                .orderBy("created_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()){
//                            Log.d("TAG ", "Hasil = > "+doc.getData());
                            Gson gson = new Gson();
                            JsonElement jsonElement =gson.toJsonTree(doc.getData());
                            list.add(gson.fromJson(jsonElement, BerdonasiUangModel.class));
                        }
                        donaturDetailDonasiAdapter = new DonaturDetailDonasiAdapter(DonasiDetailActivity.this, list);
                        recyclerView.setAdapter(donaturDetailDonasiAdapter);
                        relativeLayout.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.btn_donasi)
    void btn_donasi_action(){
        Intent i = new Intent(DonasiDetailActivity.this, BerdonasiStep1Activity.class);
        startActivity(i);
    }
}
