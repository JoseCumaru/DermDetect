package com.example.dermdetect.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.ml.Model;
import com.example.dermdetect.viewmodels.ImageClassifier;
import com.example.dermdetect.viewmodels.UserHistoryManager;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;

public class HomeActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    private static HomeActivity instance;
    TextView textInformation, result, toolbarTitle, about, zoom, confidence;
    Button predict;
    CardView linearImg;
    ImageView camera, gallery, leftIcon, rightIcon, selectedImage;
    int imageSize = 256;
    String classe;
    MenuBuilder.ItemInvoker itemConfig;
    private final Handler handler = new Handler();
    private boolean isLongPress = false;
    private boolean selected = false;

    UserHistoryManager historyManager = new UserHistoryManager();
    ImageClassifier imageClassifier;

    private static final int BACK_PRESS_INTERVAL = 2000; // Intervalo em milissegundos
    private long backPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        imageClassifier = new ImageClassifier(getApplicationContext());
        initializeComponents();
        leftIcon.setVisibility(View.INVISIBLE);
        initializeClicks();

        instance = this;
    }

    public void initializeComponents(){
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        textInformation = findViewById(R.id.textInformacao);
        toolbarTitle = findViewById(R.id.toolbar_title);
        result = findViewById(R.id.textResult);
        confidence = findViewById(R.id.textConfidence);
        camera = findViewById(R.id.imgBtnCamera);
        gallery = findViewById(R.id.imgBtnGaleria);
        zoom = findViewById(R.id.textZoom);
        linearImg = findViewById(R.id.linearimage);
        predict = findViewById(R.id.buttonInferir);
        selectedImage = findViewById(R.id.selectedimage);
        about = findViewById(R.id.textViewInformations);
        about.setVisibility(View.INVISIBLE);
        itemConfig = findViewById(R.id.item_config);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_itens, menu); // Infla o menu
        return true;
    }

    private final Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            // Amplia a ImageView enquanto o dedo estiver pressionando
            linearImg.setScaleX(2.1f);
            linearImg.setScaleY(2.1f);
            isLongPress = false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public void initializeClicks(){
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);

                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent,2);
            }
        });

        selectedImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) {
                zoom.setText("");
                if(selected){
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Inicia o long press
                        isLongPress = true;

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Esconde o texto informativo após o tempo definido
                                textInformation.setVisibility(View.INVISIBLE);
                                result.setVisibility(View.INVISIBLE);
                                about.setVisibility(View.INVISIBLE);
                                confidence.setVisibility(View.INVISIBLE);
                            }
                        }, 50);

                        handler.postDelayed(longPressRunnable, 400); // 500ms = 0.5 segundos
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Libera o long press
                        linearImg.setScaleX(1.0f);
                        linearImg.setScaleY(1.0f);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Esconde o texto informativo após o tempo definido
                                textInformation.setVisibility(View.VISIBLE);
                                result.setVisibility(View.VISIBLE);
                                confidence.setVisibility(View.VISIBLE);
                                if(imageClassifier.getInferiu()){
                                    about.setVisibility(View.VISIBLE);
                                }


                            }
                        }, 50);
                        handler.removeCallbacks(longPressRunnable);

                    }
                }

                return true;
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, findViewById(R.id.right_icon), R.style.PopupMenuStyle);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_itens, popupMenu.getMenu());

                // Configura um ouvinte de clique de item de menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Lida com o clique no item de menu
                        switch (item.getTitle().toString()) {
                            case "Configurações":
                                Intent intentS = new Intent(HomeActivity.this, SetingsActivity.class);
                                startActivity(intentS);
                                return true;

                            case "Histórico":
                                Intent intentH = new Intent(HomeActivity.this, HistoryActivity.class);
                                startActivity(intentH);
                                return true;

                            case "Sobre":
                                Intent intentA = new Intent(HomeActivity.this, AboutActivity.class);
                                startActivity(intentA);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                // Exibe o menu
                popupMenu.show();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InformationActivity.class);
                intent.putExtra("class",imageClassifier.getClasse());
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                result.setText("");
                confidence.setText("");
                about.setVisibility(View.INVISIBLE);
                imageClassifier.setInferiu(false);

                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                selectedImage.setImageBitmap(image);
                zoom.setText("Segure para dar zoom na imagem");

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                selected =true;
                imageClassifier.predictImage(image, predict, result, confidence, about, historyManager);

            } else {

                result.setText("");
                confidence.setText("");
                about.setVisibility(View.INVISIBLE);
                imageClassifier.setInferiu(false);

                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(image);
                zoom.setText("Segure para dar zoom na imagem");
                confidence.setText("");

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                selected = true;

                imageClassifier.predictImage(image, predict, result, confidence, about, historyManager);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Pressione novamente para sair", Toast.LENGTH_SHORT).show();
        }
        backPressTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isFinishing()) {
            instance = null;
        }
    }
    public static HomeActivity getInstance() {
        return instance;
    }
}