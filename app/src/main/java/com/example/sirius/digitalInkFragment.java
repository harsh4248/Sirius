package com.example.sirius;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.Ink;
import com.google.mlkit.vision.digitalink.RecognitionResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link digitalInkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class digitalInkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PaintView paintView;
    Ink ink;
    TextView resultTextView;
    Button recognizeButton,clearButton;
    int changeTextView = 0;
    String resultString="";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public digitalInkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment digitalInkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static digitalInkFragment newInstance(String param1, String param2) {
        digitalInkFragment fragment = new digitalInkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_digital_ink, container, false);
        //Ink.Builder inkbuilder = Ink.builder();
        paintView = view.findViewById(R.id.paintView);
        resultTextView = view.findViewById(R.id.resulttextview);
        recognizeButton = view.findViewById(R.id.recognizebutton);
        clearButton = view.findViewById(R.id.clearbutton);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        //checkModelDownLoad();

        //inkRecognize(ink);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                paintView.init(metrics);*/
                paintView.clear();
            }
        });
        recognizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resultString="";
                ink = PaintView.inkBuilder.build();
                inkRecognize(ink);
               /* String temp =resultTextView.getText().toString();
                temp+=resultString;
                resultTextView.setText(temp);*/

            }
        });
        return view;
    }

    /*private void checkModelDownLoad() {
        DigitalInkRecognitionModel model = ...;
        RemoteModelManager remoteModelManager = RemoteModelManager.getInstance();
    }*/

    public  void inkRecognize(Ink ink) {

        DigitalInkRecognitionModelIdentifier modelIdentifier = null;
        try {
            modelIdentifier =
                    DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
        } catch (MlKitException e) {
            // language tag failed to parse, handle error.
        }
        if (modelIdentifier == null) {
            // no model was found, handle error.
        }
        /*DigitalInkRecognitionModel model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
                .setModelIdentifier(modelIdentifier)
                .build();*/
        DigitalInkRecognitionModel model = DigitalInkRecognitionModel.builder(modelIdentifier).build();
        RemoteModelManager remoteModelManager = RemoteModelManager.getInstance();
        remoteModelManager.isModelDownloaded(model);
        remoteModelManager
                .download(model, new DownloadConditions.Builder().build())
                .addOnSuccessListener(aVoid -> Log.i("Model ML KIT", "Model downloaded"))
                .addOnFailureListener(
                        e -> Log.e("Model ML KIT", "Error while downloading a model: " + e));


// Get a recognizer for the language
        DigitalInkRecognizer recognizer = DigitalInkRecognition.getClient(
                DigitalInkRecognizerOptions.builder(model).build());
        /*recognizer.recognize(ink)
                .addOnSuccessListener(
                        // `result` contains the recognizer's answers as a RecognitionResult.
                        // Logs the text from the top candidate.

                       result -> Log.i("Success", result.getCandidates().get(0).getText()))

                .addOnFailureListener(
                        e -> Log.e("Failed", "Error during recognition: " + e));*/

        recognizer.recognize(ink).addOnSuccessListener(new OnSuccessListener<RecognitionResult>() {
            @Override
            public void onSuccess(RecognitionResult recognitionResult) {
               String temp1 = recognitionResult.getCandidates().get(0).getText();
               //resultString +=temp1;
               //recognitionResult.getCandidates().clear();
               resultTextView.setText(temp1);
               /*if(changeTextView==0) {
                   resultTextView.setText(temp1);
                   changeTextView=1;
               }
               else {
                   resultString = resultTextView.getText().toString();
                   resultString += " "+temp1;
                   resultTextView.setText(resultString);
               }*/
                Log.e("Temp ans",temp1);
                //temp=temp1;
                //Toast.makeText(, temp, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });

       /* remoteModelManager.deleteDownloadedModel(model)
                .addOnSuccessListener(
                        aVoid -> Log.i("Model Delete", "Model successfully deleted"))
                .addOnFailureListener(
                        e -> Log.e("Model Delete", "Error while deleting a model: " + e));*/


    }



}