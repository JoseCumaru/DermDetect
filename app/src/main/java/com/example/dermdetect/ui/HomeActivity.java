package com.example.dermdetect.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

import com.example.dermdetect.R;
import com.example.dermdetect.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HomeActivity extends AppCompatActivity {
    TextView textInformation, result, toolbarTitle, about, zoom, confidence;
    Button predict;
    CardView linearImg;
    ImageView camera,gallery, leftIcon, rightIcon, selectedImage;
    int imageSize = 256;
    String classe;
    MenuBuilder.ItemInvoker itemConfig;
    private Handler handler = new Handler();
    private boolean isLongPress = false;
    private boolean selected = false;

    UserHistoryManager historyManager = new UserHistoryManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeComponents();
        leftIcon.setVisibility(View.INVISIBLE);
        initializeClicks();

    }

    public void initializeComponents(){
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        textInformation = findViewById(R.id.textInformaçao);


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
        getMenuInflater().inflate(R.menu.menu_itens, menu); // Infla o menu a partir do arquivo menu.xml
        return true;
    }

    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            // Amplia a ImageView enquanto o dedo estiver pressionando
            linearImg.setScaleX(1.9f);
            linearImg.setScaleY(1.9f);
            isLongPress = false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public void initializeClicks(){
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                result.setText("");
                about.setVisibility(View.INVISIBLE);
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
                result.setText("");
                about.setVisibility(View.INVISIBLE);
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
                                about.setVisibility(View.VISIBLE);
                                confidence.setVisibility(View.VISIBLE);
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
                        // Lidar com o clique no item de menu
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
                intent.putExtra("class",classe);
                startActivity(intent);
            }
        });
    }

    public void classifyImage(Bitmap image){
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 *imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for(int i =0; i < imageSize; i++){
                for(int j =0; j < imageSize; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) *(1.f/255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) *(1.f/255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f/255.f));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i =0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }

            String[] classes = {"Acne", "Rosácea", "Impetigo", "Melasma", "Psoríase", "Micose", "Dermatite Atópica", "Melanoma"};

            // Valor mínimo para se considerar doença
            double confidenceThreshold = 0.60;

            //Possivel doença encontrada
            if (maxConfidence >= confidenceThreshold) {

                setResult(classes[maxPos], maxPos, maxConfidence);
                classe = classes[maxPos];
                saveHistory(image, classes[maxPos], maxConfidence);

            }else{
                result.setText(R.string.no_disease_found);
            }

            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void setResult(String classe, int maxPos, float maxConfidence){
        result.setText(classe);

        // Cria uma animação de pulso usando scaleX e scaleY
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(result, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(result, "scaleY", 1.0f, 1.2f, 1.0f);
        // Define a duração da animação
        scaleXAnimator.setDuration(500); // 500ms
        scaleYAnimator.setDuration(500); // 500ms

        // Cria um AnimatorSet para executar as animações em conjunto
        AnimatorSet pulseAnimation = new AnimatorSet();
        pulseAnimation.playTogether(scaleXAnimator, scaleYAnimator);
        // Inicia a animação
        pulseAnimation.start();

        confidence.setText(String.format("%.2f", maxConfidence * 100) + "%" );
        about.setText("Informações>");
        about.setVisibility(View.VISIBLE);
    }

    public void predictImage(Bitmap image){
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyImage(image);
            }
        });
    }
    private void saveHistory(Bitmap image, String detectedDisease, double confidence){
        historyManager.addToHistory(image, detectedDisease, confidence)
                .addOnSuccessListener(documentReference -> {
                    // A adição ao histórico foi bem-sucedida.
                    // Você pode realizar ações adicionais, se necessário.
                })
                .addOnFailureListener(e -> {
                    // Lide com erros ao adicionar ao histórico, se necessário.
                });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                selectedImage.setImageBitmap(image);
                zoom.setText("Segure para dar zoom na imagem");

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                selected =true;
                predictImage(image);
            } else {
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
                predictImage(image);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}