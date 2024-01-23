package com.example.dermdetect.viewmodels;

import static java.security.AccessController.getContext;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dermdetect.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageClassifier {

    private boolean inferiu = false;
    private final Context context;
    public static String classe;
    public ImageClassifier(Context context) {
        this.context = context;
    }

    public void setClasse(String classe){
        ImageClassifier.classe = classe;
    }
    public String getClasse(){
        return classe;
    }

    public void setInferiu(boolean infer){
        inferiu = infer;
    }

    public boolean getInferiu(){
        return inferiu;
    }

    public void predictImage(Bitmap image, Button predictButton, TextView resultText, TextView confidenceText, TextView aboutText, UserHistoryManager historyManager) {
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classifyImage(image, resultText, confidenceText, aboutText, historyManager);
                inferiu = true;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void classifyImage(Bitmap image, TextView resultText, TextView confidenceText, TextView aboutText, UserHistoryManager historyManager) {
        try {
            int imageSize = 256;
            Model model = Model.newInstance(context);
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32);
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

            // Run model inference and gets result.
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

            /*String[] classes = {
                    "Acne and Rosacea Photos",
                    "Actinic Keratosis Basal Cell Carcinoma and other Malignant Lesions",
                    "Atopic Dermatitis Photos",
                    "Bullous Disease Photos",
                    "Cellulitis Impetigo and other Bacterial Infections",
                    "Eczema Photos",
                    "Exanthems and Drug Eruptions",
                    "Hair Loss Photos Alopecia and other Hair Diseases",
                    "Herpes HPV and other STDs Photos",
                    "Light Diseases and Disorders of Pigmentation",
                    "Lupus and other Connective Tissue diseases",
                    "Melanoma Skin Cancer Nevi and Moles",
                    "Nail Fungus and other Nail Disease",
                    "Poison Ivy Photos and other Contact Dermatitis",
                    "Psoriasis pictures Lichen Planus and related diseases",
                    "Scabies Lyme Disease and other Infestations and Bites",
                    "Seborrheic Keratoses and other Benign Tumors",
                    "Systemic Disease",
                    "Tinea Ringworm Candidiasis and other Fungal Infections",
                    "Urticaria Hives",
                    "Vascular Tumors",
                    "Vasculitis Photos",
                    "Warts Molluscum and other Viral Infections"
            };*/

            // Valor mínimo para se considerar doença
            double confidenceThreshold = 0.65;

            //Possivel doença encontrada
            if (maxConfidence >= confidenceThreshold) {

                // Depois de obter o resultado, atualiza as visualizações de texto
                setClasse(classes[maxPos]);
                setResult(classes[maxPos], maxConfidence, resultText, confidenceText, aboutText);
                aboutText.setText("Informações>");

                // Salva no histórico
                historyManager.addToHistory(image, classes[maxPos], maxConfidence);

            }else{
                noDiseaseFound(resultText);
            }
            model.close();
        } catch (IOException e) {

            // Lida com exceções, se necessário
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void setResult(String classe, float maxConfidence, TextView result, TextView confidence, TextView about){
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
        about.setText("Informações >");
        about.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    public void noDiseaseFound(TextView textView){
        textView.setText("Nenhum indício de doença encontrado");
    }

}
