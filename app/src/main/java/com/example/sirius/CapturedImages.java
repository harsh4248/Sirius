package com.example.sirius;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

/*import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class CapturedImages extends AppCompatActivity implements Serializable {
    //    String app_folder_path=getExternalFilesDir(null).toString()+"/images";
    ListView listView;

    RecyclerView recyclerView;

    RecyclerView.LayoutManager RecyclerViewLayoutManager;

    recylerviewAdapter recylerviewAdapter;

    LinearLayoutManager HorizontalLayout;
    ArrayList<String> imagesDataArrayList;

    //Button cropButton;

    View ChildView;
    int RecyclerViewItemPosition;

    Button createPdfButton,ocrButton;
    String pdfname,globalWordNameString;
    TextRecognizer recognizer;
    String ocrString;
    ArrayList<String> ocrarraylist;
    int globalLoop,temploop;
    //boolean forWordDocument= true ;
    StringBuilder ocrLines;
    private FirebaseFunctions mFunctions;
    private View.OnClickListener onItemClickListener  = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            //Log.e("Item Click position",Integer.toString(position));
            //CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(CapturedImages.this);

            //CropImage.activity(Uri.parse(imagesDataArrayList.get(position))).setGuidelines(CropImageView.Guidelines.ON).start(CapturedImages.this);

            //CropImage.activity(Uri.parse(imagesDataArrayList.get(position))).start(CapturedImages.this);
            RecyclerViewItemPosition = position;
            String item = imagesDataArrayList.get(position);
            String itemDestination = imagesDataArrayList.get(position);
            Uri imageUri = Uri.fromFile(new File(item));
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(CapturedImages.this);
            //Toast.makeText(CapturedImages.this, "Item Position = "+ position, Toast.LENGTH_SHORT).show();

            /*Intent intent = CropImage.activity(Uri.parse(imagesDataArrayList.get(position))).getIntent(CapturedImages.this);
            startActivityForResult(intent,1004);*/
           // UCrop.of(imageUri,imageUri).withAspectRatio(16,9).withMaxResultSize(400,400).start(CapturedImages.this);
            //recyclerView.removeViewAt(position);
            //recyclerView.setAdapter(recylerviewAdapter);
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(Objects.requireNonNull(resultUri.getPath()));
                imagesDataArrayList.remove(RecyclerViewItemPosition);
                imagesDataArrayList.add(RecyclerViewItemPosition,file.toString());
                recylerviewAdapter.notifyItemChanged(RecyclerViewItemPosition);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error in crop", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_images);

        //old code
        /*listView = findViewById(R.id.listview);


        ArrayList<String> imagesDataArrayList = getIntent().getStringArrayListExtra("Key");
        String temp = Integer.toString(imagesDataArrayList.size());
        Log.e("ArrayListContent",temp);

        ImageAdapter imageAdapter = new ImageAdapter(this,R.layout.mylistlayout,imagesDataArrayList);

        listView.setAdapter(imageAdapter);*/
        mFunctions = FirebaseFunctions.getInstance();
        recognizer= TextRecognition.getClient();
        createPdfButton = findViewById(R.id.createpdfbutton);
        ocrarraylist = new ArrayList<>();
        imagesDataArrayList = getIntent().getStringArrayListExtra("Key");
        String temp = Integer.toString(imagesDataArrayList.size());
        Log.e("ArrayListContent",temp);
        Log.e("arrayLIstCONTENT",imagesDataArrayList.get(0));

        //cropButton = findViewById(R.id.cropbutton);
        recyclerView = findViewById(R.id.recyclerview);
        ocrButton = findViewById(R.id.ocrbutton);

        RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(RecyclerViewLayoutManager);

        recylerviewAdapter = new recylerviewAdapter(this,imagesDataArrayList);

        HorizontalLayout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        recyclerView.setLayoutManager(HorizontalLayout);

        SnapHelper snapHelper = new PagerSnapHelper();

        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(recylerviewAdapter);

        recylerviewAdapter.setOnItemClickListener(onItemClickListener);

       // CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);

        createPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(CapturedImages.this);
                View promptView = inflater.inflate(R.layout.layout_dialog,null);

                AlertDialog.Builder alertDailogbuilder = new AlertDialog.Builder(CapturedImages.this);

                alertDailogbuilder.setView(promptView);

                final EditText pdfnameString = promptView.findViewById(R.id.filenameedittext);

                alertDailogbuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pdfname = pdfnameString.getText().toString();
                        if(pdfname.matches(""))
                            pdfnameString.setError("Enter Name");
                        else {
                            //imagesDataArrayList.add(pdfname);
                            createPDF();
                            Intent intent = new Intent(CapturedImages.this, Dashboard.class);
                            intent.putExtra("Key", imagesDataArrayList);
                            intent.putExtra("Referesh",1);
                            startActivity(intent);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setTitle("Enter PDF name");

                AlertDialog alertDialog = alertDailogbuilder.create();

                alertDialog.show();
            }
        });

        ocrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocrString="";
               // forWordDocument = true;
                //InputImage image;
                /*try {

                    for(int i=0;i<imagesDataArrayList.size();i++) {
                        image = InputImage.fromFilePath(CapturedImages.this, Uri.fromFile(new File(imagesDataArrayList.get(i))));
                        recognizeText(image);
                        ocrString="\n";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                LayoutInflater inflater = LayoutInflater.from(CapturedImages.this);
                View promptView = inflater.inflate(R.layout.layout_dialog,null);

                AlertDialog.Builder alertDailogbuilder = new AlertDialog.Builder(CapturedImages.this);

                alertDailogbuilder.setView(promptView);
                final EditText wordnameString = promptView.findViewById(R.id.filenameedittext);

                alertDailogbuilder.setPositiveButton("create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String wordName = wordnameString.getText().toString();
                        globalWordNameString = wordName;
                        //deleteTempFileContent();
                        if(wordName.matches("")) {
                            wordnameString.setError("Enter name");

                        }
                        else {

                                Thread thread = new Thread(()->{
                                    globalLoop = imagesDataArrayList.size();
                                    for(int i=0;i<imagesDataArrayList.size();i++) {

                                        try {

                                            File compressedImage = Compressor.getDefault(CapturedImages.this).compressToFile(new File(imagesDataArrayList.get(i)));
                                            InputImage image = InputImage.fromFilePath(CapturedImages.this, Uri.fromFile(compressedImage));
                                            recognizeText(image,i);
                                            Log.e("TimesLoop", Integer.toString(i));
                                            temploop=i;


                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }


                                    }
                                });
                                thread.start();
                                while(thread.isAlive()) {

                                    //editWord(ocrarraylist);
                                }
                                /*Intent intent = new Intent(CapturedImages.this,Dashboard.class);
                                startActivity(intent);*/
                                //String tempString = readFromTextFile();
                                //createWord(tempString);
                        }
                    }


                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setTitle("Enter word name");

                AlertDialog alertDialog = alertDailogbuilder.create();

                alertDialog.show();

            }
        });

    }

   /* private void editWord(ArrayList<String> lines)  {
        String fileString = getExternalFilesDir(null).toString()+"/word";
        final File file = new File(fileString,globalWordNameString+".docx");
        XWPFDocument xwpfDocument = new XWPFDocument();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        for(String s:lines) {


            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();


            XWPFRun xwpfRun = xwpfParagraph.createRun();

            xwpfRun.setText(s);

        }
        try {
            xwpfDocument.write(fileOutputStream);
            assert fileOutputStream != null;
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createWord(String temp)  {
        //final File file = new File(getExternalFilesDirs(null).toString()+"/word/"+globalWordNameString+".docx");
        Log.e("TimesLoop",Integer.toString(0));
        String fileString = getExternalFilesDir(null).toString()+"/word";
        final File file = new File(fileString,globalWordNameString+".docx");
        Log.e("TimesLoop",Integer.toString(1));
        try {
            *//*if(!file.exists()) {
                file.createNewFile();
            }*//*
            Log.e("WordFun",Integer.toString(2));
            XWPFDocument xwpfDocument = new XWPFDocument();
            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
            XWPFRun xwpfRun = xwpfParagraph.createRun();

            Log.e("WordFun",Integer.toString(3));
            Log.e("Last Log",temp);
            xwpfRun.setText(temp);
            xwpfRun.setFontSize(16);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            xwpfDocument.write(fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
            xwpfDocument.close();



        } catch (Exception e) {
            e.printStackTrace();
            Log.e("word Fun",e.toString());
        }


    }*/

    private void createPDF() {

        final File file = new File(getBatchDirectoryName(),pdfname +".pdf");

        final ProgressDialog progressDialog = ProgressDialog.show(this,"","Generating PDF");
        progressDialog.show();

        Thread createPDFThread = new Thread(()->{
            Bitmap bitmap;
            PdfDocument pdfDocument = new PdfDocument();

            int height = 1010;
            int width = 714;
            int reqH, reqW;
            reqW = width;

            for(int i=0;i<imagesDataArrayList.size();i++) {

                bitmap = Compressor.getDefault(this).compressToBitmap(new File(imagesDataArrayList.get(i)));

                reqH = width * bitmap.getHeight() / bitmap.getWidth();
                Log.e("reqH", "=" + reqH);

                if (reqH < height) {
                    //  bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
                } else {
                    reqH = height;
                    reqW = height * bitmap.getWidth() / bitmap.getHeight();
                    Log.e("reqW", "=" + reqW);
                    //   bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
                }

                // Compress image by decreasing quality
                // ByteArrayOutputStream out = new ByteArrayOutputStream();
                //  bitmap.compress(Bitmap.CompressFormat.WEBP, 50, out);
                //    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                //bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
                //Create an A4 sized page 595 x 842 in Postscript points.
                //PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                Log.e("PDF", "pdf = " + bitmap.getWidth() + "x" + bitmap.getHeight());
                /*float x = (714 - bitmap.getScaledWidth(canvas)) / 2;
                float y = (1010 - bitmap.getScaledHeight(canvas)) / 2;*/

                canvas.drawBitmap(bitmap, 0, 0, null);

                pdfDocument.finishPage(page);
                }
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file);
                    pdfDocument.writeTo(fos);
                    pdfDocument.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }




        });

        createPDFThread.start();

        while(createPDFThread.isAlive()) {

        }


    }




    public String getBatchDirectoryName() {

        String app_folder_path = "";
        // app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        app_folder_path = getExternalFilesDir(null).toString()+"/pdf";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }
    private void recognizeText(InputImage image,int temp) {

        // [START get_detector_default]
        TextRecognizer recognizer = TextRecognition.getClient();
        //String ocrString="";
        // [END get_detector_default]
        //final File file = new File(getExternalFilesDirs("word").toString(),globalWordNameString+".docx");
        // [START run_detector]
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            //String temp="";
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // [START_EXCLUDE]
                                // [START get_text]

                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    Rect boundingBox = block.getBoundingBox();
                                    Point[] cornerPoints = block.getCornerPoints();
                                    String text = block.getText();

                                    for (Text.Line line: block.getLines()) {
                                        // ...
                                        for (Text.Element element: line.getElements()) {
                                            // ...
                                           // Log.e("Text",element.getText());
                                            ocrString+= " "+element.getText();

                                        }
                                        ocrString+="\n";
                                        createTextFile(ocrString);
                                        //createWord(ocrString);
                                        //createWord(ocrString);
                                        //Log.e("Text",ocrString);
                                        //ocrString="";
                                        ocrarraylist.add(ocrString);
                                        ocrString="";
                                    }
                                }
                                //Log.e("Text",ocrString);

                                // [END get_text]
                                // [END_EXCLUDE]

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        Log.e("ERROR",e.toString());
                                    }
                                });
        while(!result.isComplete()) {}
        Log.d("Image Number",Integer.toString(temp)+" "+Integer.toString(globalLoop));
        if(temp==(globalLoop-1)) {
            Intent intent = new Intent(CapturedImages.this,Dashboard.class);
            startActivity(intent);
        }

       // return result;
        // [END run_detector]
        //return ocrString;
    }
    private String readFromTextFile() {
        FileInputStream is;
        BufferedReader reader;
        String line="";
        String fileString = getExternalFilesDir(null).toString()+"/word"+"/temp.txt";

        //final File file = new File(fileString,"temp.txt");
        File file = new File(fileString);

        try {

            if (file.exists()) {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                line = reader.readLine();
                while (line != null) {
                    Log.e("StackOverflow", line);
                    line += reader.readLine();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

   /* private void deleteTempFileContent() {
        String fileString = getExternalFilesDir(null).toString()+"/word"+"/temp.txt";

        //final File file = new File(fileString,"temp.txt");
        File file = new File(fileString);
        try {
            if(!file.createNewFile()) {
                Log.e("File","file already exist");
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append("");
                myOutWriter.close();
                fOut.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
*/
    private void createTextFile(String s) {
        /*try {
            String fileString = getExternalFilesDir(null).toString()+"/word";

            final File file = new File(fileString,"temp.txt");
            FileOutputStream fileout=new FileOutputStream(file);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.append(s);
            outputWriter.close();
            //display file saved message

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        try {
            String fileString = getExternalFilesDir(null).toString()+"/word"+"/"+globalWordNameString+".txt";

            //final File file = new File(fileString,"temp.txt");
            File file = new File(fileString);

            /*if(!file.exists()) {
                file.createNewFile();
            }*/
           /* if(!file.createNewFile()) {
                Log.e("File","file already exist");
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(s);
                myOutWriter.close();
                fOut.close();
            }
            else {
            FileWriter fileWritter = new FileWriter(file.getName(),true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(s);
            bw.close();
            }*/
            long fileLength = file.length();
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(fileLength);
            raf.writeBytes(s);
            raf.close();
        }
        catch (IOException E) {
            E.printStackTrace();
        }
    }

}