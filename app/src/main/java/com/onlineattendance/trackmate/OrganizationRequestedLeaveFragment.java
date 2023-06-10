package com.onlineattendance.trackmate;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onlineattendance.trackmate.databinding.FragmentOrganizationRequestedLeaveBinding;

import java.util.ArrayList;
import java.util.List;


public class OrganizationRequestedLeaveFragment extends Fragment {
    private FragmentOrganizationRequestedLeaveBinding binding;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ListView listView;
    private ArrayAdapter<EmployeeLeaves> adapter;
    private List<EmployeeLeaves> leavesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrganizationRequestedLeaveBinding.inflate(inflater,container,false);
        View rootview = binding.getRoot();

        // Link the ID of widgets with variable
        listView = rootview.findViewById(R.id.org_requestedLeave_listView);

        adapter = new ArrayAdapter<EmployeeLeaves>(requireContext(), android.R.layout.simple_list_item_1, leavesList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                // Customize the view for each item in the list
                View view = super.getView(position, convertView, parent);

                // Get the current EmployeeLeaves object
                EmployeeLeaves employeeLeaves = leavesList.get(position);

                // Create the string to display in the TextView
                String itemText =  employeeLeaves.getName_of_the_Employee()+
                        "\nEmp No : " + employeeLeaves.getEmp_No() +
                        "\nLeave Type : " + employeeLeaves.getLeave_Type()+
                        "\nReason : " + employeeLeaves.getReason() +
                        "\nBegin Date : " + employeeLeaves.getBegin_Date() +
                        "\nEnd Date : " + employeeLeaves.getEnd_Date() ;

                // Set the text in the TextView
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(itemText);

                // Set text color, font size, padding
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                textView.setPadding(20, 20, 20, 20);

                // Create a background drawable with border
                Drawable backgroundDrawable = getResources().getDrawable(R.drawable.listview_background);
                textView.setBackground(backgroundDrawable);

                return view;
            }
        };
        listView.setAdapter(adapter);

        // Fetch and display the Leaves data from the database
        fetchLeavesData();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(position);
                return true;
            }
        });

        return rootview;
    }

    private void showOptionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Responds for Leave Requests");
        builder.setItems(new CharSequence[]{"Approve", "Reject", "Cancel"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        approveLeave(position);
                        break;
                    case 1:
                        rejectLeave(position);
                        break;
                    case 2:
                        dialogInterface.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    private void rejectLeave(int position) {
        EmployeeLeaves selectedLeave = leavesList.get(position);
        String empNo = selectedLeave.getEmp_No();

        // Get the reference to the specific leave request
        DatabaseReference leaveRef = database.getReference("Leave Request").child(empNo);

        // Remove the leave request from the database
        leaveRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Remove the leave from the list and notify the adapter
                        leavesList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Leave rejected successfully", Toast.LENGTH_SHORT).show();
                        // Refresh the page
                        refreshPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to reject leave", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void approveLeave(int position) {
        EmployeeLeaves selectedLeave = leavesList.get(position);
        String empNo = selectedLeave.getEmp_No();

        // Get the reference to the specific leave request
        DatabaseReference leaveRef = database.getReference("Leave Request").child(empNo);

        // Remove the leave request from the database
        leaveRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Remove the leave from the list and notify the adapter
                        leavesList.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(requireContext(), "Leave approved successfully", Toast.LENGTH_SHORT).show();
                        // Refresh the page
                        refreshPage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to approve leave", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Refresh the page
    private void refreshPage() {
        // Clear the leavesList
        leavesList.clear();
        // Fetch and display the maintenance data from the database
        fetchLeavesData();
    }

    private void fetchLeavesData(){
        DatabaseReference databaseReference = database.getReference("Leave Request");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String empNo = dataSnapshot.getKey();

                    // Retrieve the values for each leave request field
                    String name_of_the_Employee = dataSnapshot.child("Name_of_the_Employee").getValue(String.class);
                    String emp_No = dataSnapshot.child("Emp_No").getValue(String.class);
                    String leave_Type = dataSnapshot.child("Leave_Type").getValue(String.class);
                    String reason = dataSnapshot.child("Reason").getValue(String.class);
                    String begin_Date = dataSnapshot.child("Begin_Date").getValue(String.class);
                    String end_Date = dataSnapshot.child("End_Date").getValue(String.class);

                    EmployeeLeaves employeeLeaves = new EmployeeLeaves(name_of_the_Employee,emp_No,leave_Type,reason,begin_Date,end_Date);
                    leavesList.add(employeeLeaves);
                }
                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class EmployeeLeaves{
        // Declare private fields to hold the leave request data
        private String Name_of_the_Employee,Emp_No,Leave_Type,Reason,Begin_Date,End_Date;

        // Constructor to initialize the leave request data
        public EmployeeLeaves(String name_of_the_Employee, String emp_No, String leave_Type, String reason, String begin_Date, String end_Date) {
            Name_of_the_Employee = name_of_the_Employee;
            Emp_No = emp_No;
            Leave_Type = leave_Type;
            Reason = reason;
            Begin_Date = begin_Date;
            End_Date = end_Date;
        }

        // Getter methods to retrieve the data
        public String getName_of_the_Employee() {
            return Name_of_the_Employee;
        }

        public String getEmp_No() {
            return Emp_No;
        }

        public String getLeave_Type() {
            return Leave_Type;
        }

        public String getReason() {
            return Reason;
        }

        public String getBegin_Date() {
            return Begin_Date;
        }

        public String getEnd_Date() {
            return End_Date;
        }
    }

}

