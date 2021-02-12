package com.example.feedthehunger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link status#newInstance} factory method to
 * create an instance of this fragment.
 */
public class status extends Fragment {
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<String> new_list;
    private FirebaseAuth mAuth;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    Donation donation;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public status() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment status.
     */
    // TODO: Rename and change types and number of parameters
    public static status newInstance(String param1, String param2) {
        status fragment = new status();
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
        return inflater.inflate(R.layout.fragment_status, container, false);
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        listView = getView().findViewById(R.id.listview_status);
        database= FirebaseDatabase.getInstance();
        ref=database.getReference("donations");
        donation=new Donation();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid=user.getUid();
        list=new ArrayList<>();
        new_list=new ArrayList<>();
        adapter=new ArrayAdapter<String>(getActivity(),R.layout.donation_info,R.id.donation_info,new_list);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    donation=ds.getValue(Donation.class);
//                if(donation.donor_userid!=null){
//                    new_list.add(donation.donor_userid.toString()+" "+donation.type.toString()+" "+donation.description.toString());
//                }
//                }
//                for(int i=0;i<new_list.size();i++){
//                    String str=new_list.get(i).substring(0,18);
//                    if(str.equals(uid)){
//                        list.add(new_list.get(i));
//                    }
                    if(donation.doneeid.equals(uid) && (donation.status==1 ||donation.status==2)){
                        String sts="";
                        if(donation.status==1){
                            sts+="Requested";
                        }else if(donation.status==2){
                            sts+="Donor Accepted";
                        }
                        list.add(donation.donationid+" "+donation.type+" "+donation.description+" "+donation.status+" "+sts);
                        new_list.add("Type: "+donation.type+"\n"+"Description: "+donation.description+" "+"Status: "+sts);
                    }
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = list.get(position);
                String new_str[] = str.split(" ");
                if (new_str[3].equals("2")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Recieved");
                    StringBuilder s = new StringBuilder();
                    s.append("Type: " + new_str[1]);
                    s.append("\n");
                    s.append("Description " + new_str[2]);
                    s.append("\n");
                    s.append("Status " + new_str[4]);
                    s.append("\n");
                    builder.setMessage(s.toString());

                    // add the buttons
                    builder.setPositiveButton("Recieved", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("donations").child(new_str[0]);
                            mDatabase.child("status").setValue(3);
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Details");
                    StringBuilder s = new StringBuilder();
                    s.append("Type: " + new_str[1]);
                    s.append("\n");
                    s.append("Description: " + new_str[2]);
                    s.append("\n");
                    s.append("Status: " + new_str[4]);
                    s.append("\n");
                    builder.setMessage(s.toString());

                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
            }
        });
    }
}