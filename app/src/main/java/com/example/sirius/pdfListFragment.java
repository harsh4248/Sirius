package com.example.sirius;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pdfListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pdfListFragment extends Fragment {

    ArrayList<String> createdPdfList;
    ListView listView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public pdfListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pdfListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static pdfListFragment newInstance(String param1, String param2) {
        pdfListFragment fragment = new pdfListFragment();
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

        /*pdfListView.findViewById(R.id.pdflistview);
        getBatchDirectoryName();
        createdPdfList = new ArrayList<>();
        fetchPDF();
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,createdPdfList);
        //ArrayAdapter<String> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),createdPdfList,android.R.layout.simple_list_item_1)
        pdfListView.setAdapter(arrayAdapter);
        Intent intentFromCapturedImages = getActivity().getIntent();*/



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //pdfListView = findViewById(R.id.pdflistview);
        View view =  inflater.inflate(R.layout.fragment_pdf_list, container, false);

        listView = view.findViewById(R.id.pdflistview2);
        createdPdfList = new ArrayList<>();
        fetchPDF();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,createdPdfList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(getBatchDirectoryName(),createdPdfList.get(position));

                /*Uri uri = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setDataAndType(uri, "application/pdf");*/
                Uri pdfContentFile = FileProvider.getUriForFile(getActivity(),BuildConfig.APPLICATION_ID+".provider",file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(pdfContentFile,"application/pdf");


                try {
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e) {
                    Toast.makeText(getActivity(), "Failed to open", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;

    }

    private void fetchPDF() {

        String path = getBatchDirectoryName();

        File directory = new File(path);
        File[] files = directory.listFiles();

        for(int i=0;i<files.length;i++) {

            String fileName = files[i].getName();
            createdPdfList.add(fileName);
        }


    }

    public String getBatchDirectoryName() {

        String app_folder_path = "";
        // app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        app_folder_path = getActivity().getExternalFilesDir(null).toString()+"/pdf";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {

        }

        return app_folder_path;
    }
}