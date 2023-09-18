package com.example.testedermdetect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.testedermdetect.ml.Model;

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
    PopupMenu popupMenu;
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
        predict = findViewById(R.id.buttonEnviar);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.right_icon) {
            // Criar e exibir o PopupMenu aqui
            PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.right_icon));
            popupMenu.inflate(R.menu.menu_itens);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // Realizar ação de acordo com a opção selecionada
                    switch (item.getOrder()) {
                        case 1:
                            // Ações para configurações
                            break;
                        case 2:
                            // Ações para histórico
                            break;
                        case 3:
                            // Ações para sobre
                            break;
                    }
                    return true;
                }
            });

            // Mostrar o popupMenu
            popupMenu.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
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

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, rightIcon);
                popupMenu.getMenuInflater().inflate(R.menu.menu_itens, popupMenu.getMenu());

                // Configure um ouvinte de clique de item de menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Lide com o clique no item de menu aqui
                        switch (item.getItemId()) {
                            case 1:
                                // Ação para o Item 1
                                return true;
                            case 2:
                                // Ação para o Item 2
                                return true;
                            // Adicione mais casos conforme necessário
                            case  3:
                            default:
                                return false;
                        }
                    }
                });

                // Exiba o menu
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
            result.setText(classes[maxPos]);
            about.setVisibility(View.VISIBLE);
            classe = classes[maxPos];

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
                preverImagem(image);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}