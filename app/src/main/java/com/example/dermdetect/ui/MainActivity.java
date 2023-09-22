package com.example.dermdetect.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {
    TextView result, toolbarTitle, about;// confidence;
    Button predict;
    ImageView camera,gallery, leftIcon, rightIcon, selectedImage;
    int imageSize = 256;
    String classe;
    Boolean selected = false;
    MenuBuilder.ItemInvoker itemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
        leftIcon.setVisibility(View.INVISIBLE);
        initializeClicks();
    }

    public void initializeComponents(){
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);

        toolbarTitle = findViewById(R.id.toolbar_title);
        result = findViewById(R.id.textResult);
        //confidence = findViewById(R.id.confidence);
        camera = findViewById(R.id.imgBtnCamera);
        gallery = findViewById(R.id.imgBtnGaleria);
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

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.right_icon), R.style.PopupMenuStyle);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_itens, popupMenu.getMenu());

                // Configure um ouvinte de clique de item de menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Lidar com o clique no item de menu
                        switch (item.getTitle().toString()) {
                            case "Configurações":
                                Intent intentS = new Intent(MainActivity.this, SetingsActivity.class);
                                startActivity(intentS);

                                return true;
                            case "Histórico":
                                Intent intentH = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(intentH);
                                return true;
                            case "Sobre":
                                Intent intentA = new Intent(MainActivity.this, AboutActivity.class);
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
                Intent intent = new Intent(MainActivity.this, InformationActivity.class);
                intent.putExtra("class","  " + classe);
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
            double confidenceThreshold = 0.50;

            if (maxConfidence >= confidenceThreshold) {
                result.setText(classes[maxPos]);
                about.setVisibility(View.VISIBLE);
                classe = classes[maxPos];
            }else{
                result.setText(R.string.no_disease_found);
            }

            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    public void preverImagem(Bitmap image){
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyImage(image);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                selectedImage.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                selected =true;
                preverImagem(image);
            } else {
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                selected = true;
                preverImagem(image);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}