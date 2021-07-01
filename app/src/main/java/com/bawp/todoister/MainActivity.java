package com.bawp.todoister;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import com.bawp.todoister.adapter.OnTodoClickListener;
import com.bawp.todoister.adapter.RecyclerViewAdapter;
import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.TextureView;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTodoClickListener {
    private static final String TAG = "TaskItem";
    private TaskViewModel taskViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    BottomSheetFragment bottomSheetFragment;
    private SharedViewModel sharedViewModel;
    private Task deleteTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = (ConstraintLayout)findViewById(R.id.bottomSheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior = BottomSheetBehavior.from(constraintLayout);
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication()).create(TaskViewModel.class);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            showBottomSheetDialog();
        });

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        taskViewModel.getAllTasks().observe(this, tasks -> {
//            for(Task task: tasks){
//                Log.d(TAG, "onCreate: "+task.getTaskID());
//            }
            recyclerViewAdapter = new RecyclerViewAdapter(tasks,this);
            recyclerView.setAdapter(recyclerViewAdapter);
        });

    }
    private void showBottomSheetDialog(){
     bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_delete){
            if(deleteTask!=null) {
                Log.d("Delete", "onOptionsItemSelected: " + deleteTask.getTask());
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle(R.string.delete_title);
                builder.setMessage(R.string.delete_message);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TaskViewModel.delete(deleteTask);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                Log.d("Delete", "onOptionsItemSelected: nothing selected");
                Snackbar.make(recyclerView,"Please select a task", BaseTransientBottomBar.LENGTH_LONG).show();
            }
            return true;
        }else if(id == R.id.action_done){
            if(deleteTask!=null){

                if(!deleteTask.isDone()){
                    Log.d("Done", "onOptionsItemSelected: " + deleteTask.isDone());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(true);
                    builder.setTitle(R.string.done_title);
                    builder.setMessage(R.string.done_message);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteTask.setDone(true);
                            View todoRows = getLayoutInflater().inflate(R.layout.todo_row,null);
                            TextView textView = (TextView)todoRows.findViewById(R.id.todo_row_todo);
                            textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            textView.setEnabled(false);
                            Chip chip = (Chip)todoRows.findViewById(R.id.todo_row_chip);
                            chip.setEnabled(false);
                            TaskViewModel.update(deleteTask);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }else{
                    Snackbar.make(recyclerView,"Task already done", BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }else{
                Snackbar.make(recyclerView,"Please select a task", BaseTransientBottomBar.LENGTH_LONG).show();
            }

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTodoClick(Task task) {
        Log.d("Position", "onTodoClick: " + task.getTask());
        sharedViewModel.selectItem(task);
        sharedViewModel.setEdit(true);
        showBottomSheetDialog();
    }

    @Override
    public void onTodoRadioButtonClick(Task task) {
        deleteTask = task;
//        Log.d("Position", "onTodoClick: " + task.getTask());
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(R.string.delete_title);
//        builder.setMessage(R.string.delete_message);
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                TaskViewModel.delete(task);
//            }
//        });
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }
}