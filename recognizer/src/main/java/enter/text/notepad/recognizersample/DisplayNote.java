package enter.text.notepad.recognizersample;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yandex.speechkit.recognizersample.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DisplayNote extends AppCompatActivity {
    private NDb mydb;
    EditText name;
    EditText content;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    Context context = this;
    private CoordinatorLayout coordinatorLayout;
    String dateString;
    Bundle extras;
    int id_To_Update = 0;
    Snackbar snackbar;
    FloatingActionButton RecButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        name = (EditText) findViewById(R.id.txtname);
        content = (EditText) findViewById(R.id.txtcontent);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        mydb = new NDb(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                snackbar = Snackbar
                        .make(coordinatorLayout, "Id : " + String.valueOf(Value), Snackbar.LENGTH_LONG);
                snackbar.show();
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                String nam = rs.getString(rs.getColumnIndex(NDb.name));
                String contents = rs.getString(rs.getColumnIndex(NDb.remark));
                if (!rs.isClosed()) {
                    rs.close();
                }
                name.setText((CharSequence) nam);
                content.setText((CharSequence) contents);
            }
            String Result = extras.getString("result");
            if (Result != "") {
                content.setText(Result);
            }
            RecButton = (FloatingActionButton) findViewById(R.id.RecButton);

            RecButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent Recognizer = new Intent(v.getContext(), RecognizerSampleActivity.class);
                    startActivityForResult(Recognizer, 1);
                }
            });
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            getMenuInflater().inflate(R.menu.display_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.Delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.DeleteNote)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mydb.deleteNotes(id_To_Update);
                                        Toast.makeText(DisplayNote.this, "Удалено", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(
                                                getApplicationContext(),
                                                MyNotes.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("Нет",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                    }
                                });
                AlertDialog d = builder.create();
                d.setTitle("Вы уверены?");
                d.show();
                return true;
            case R.id.Save:
                Bundle extras = getIntent().getExtras();
                Calendar c = Calendar.getInstance();
                System.out.println("Текущее время => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                dateString = formattedDate;
                if (extras != null) {
                    int Value = extras.getInt("id");
                    if (Value > 0) {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, "Введите текст", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            if (mydb.updateNotes(id_To_Update, name.getText()
                                    .toString(), dateString, content.getText()
                                    .toString())) {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Обновлено", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Ошибка", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    } else {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            snackbar = Snackbar
                                    .make(coordinatorLayout, "Введите название заметки", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } else {
                            if (mydb.insertNotes(name.getText().toString(), dateString,
                                    content.getText().toString())) {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Добавлено", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            } else {
                                snackbar = Snackbar
                                        .make(coordinatorLayout, "Ошибка", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(
                getApplicationContext(),
                MyNotes.class);
        startActivity(intent);
        finish();
        return;
    }
}

