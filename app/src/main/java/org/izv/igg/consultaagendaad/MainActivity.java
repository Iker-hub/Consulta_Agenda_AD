package org.izv.igg.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.izv.igg.consultaagendaad.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private final int CONTACTS_PERMISSION = 1;
    private final String TAG = "xyzyx";

    private Button btSearch; // = findViewById(R.id.btSearch); -> Esto no se puede hacer porque aún no existe el layout
    private EditText etPhone;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG,"He entrado en onCreate"); // Verbose
        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG,"He entrado en onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ajustes) {
            viewSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        Log.v(TAG,"He entrado en onPause");
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG,"He entrado en onRequestPermissionsResult");
        switch (requestCode){
            case CONTACTS_PERMISSION:
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso
                    search();
                } else {
                    // Sin permiso
                }
                break;
        }
        //requestCode
        //permissions
        //grantResults
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG,"He entrado en onRestart");
    }

    @Override
    protected void onResume() {
        Log.v(TAG,"He entrado en onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.v(TAG,"He entrado en onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.v(TAG,"He entrado en onStop");
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void explain() {
        showRationaleDialog(getString(R.string.title)
                         , getString(R.string.message)
                         , Manifest.permission.READ_CONTACTS
                         , CONTACTS_PERMISSION);
        requestPermission();
    }

    private void initialize() {
        btSearch = findViewById(R.id.btSearch);
        etPhone = findViewById(R.id.etPhone);
        tvResult = findViewById(R.id.tvResult);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchIfPermitted();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() { // Petición del permiso
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION);
    }

    private void search() {
        
        tvResult.setText("a pelo ya sí");
        // Buscar entre contactos
        // ContentProvider Proveedor de contenidos
        // ContentResolver Consultor de contenidos
        // Queries the user dictionary and returns results
        // url: https://ieszaidinvergeles.org/carpeta/carpeta2/pagina.html?dato=1
        // uri: protocolo://direccion/ruta/recurso

        /*Cursor cursor = getContentResolver().query(
                UserDictionary.Words.CONTENT_URI,
                new String[]{"projection"},
                "campo1 = ? and campo2 > ? or campo3 = ?",
                new String[] {"pepe","4","23"},
                "campo5, campo3, campo4");*/

        /*
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String proyeccion[] = new String[] {ContactsContract.Contacts.DISPLAY_NAME}; // Todos los campos
        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " +
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";
        String argumentos[] = new String[]{"1","1"};
        seleccion = null; // Sin where
        argumentos = null; // Sin where
        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";
        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);
        */

        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion2[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                               ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // Todos los campos
        String seleccion2 = ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";
        String argumentos2[] = new String[]{/*"1%2%3"};*/etPhone.getText().toString()};
        String orden2 = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        /*
        Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion2[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
        String seleccion2 = ContactsContract.Data.MIMETYPE + "='" +
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND" +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";
        String argumentos2[] = new String[]{etPhone.getText().toString()};
        String orden2 = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        */


        Cursor cursor2 = getContentResolver().query(uri2, proyeccion2, seleccion2, argumentos2, orden2);

        String[] columnas2 = cursor2.getColumnNames();
        for (String s : columnas2) {
            Log.v(TAG, s);
        }
        int columnaNombre = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre, numero;
        while (cursor2.moveToNext()){
            nombre = cursor2.getString(columnaNombre);
            numero = cursor2.getString(columnaNumero);
            //Log.v(TAG, nombre + ": " + numero);
            for (String s : columnas2) {
                int pos = cursor2.getColumnIndex(s);
                String valor = cursor2.getString(pos);
                Log.v(TAG, pos + " " + s + " " + valor);
                tvResult.setText(valor);
            }
        }
    }

    private void searchIfPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // La versión de Android es de posterior a la 6 incluida
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                // Ya tengo el permiso
                search();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explain(); // Explicación del permiso
            } else {
                requestPermission(); //le pido el permiso
            }
        } else {
            // La versión de Android es anterior a la 6
            // Ya tengo el permiso
            search();
        }
    }

    private void showRationaleDialog(String title, String message, String permission, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // No hago nada
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        requestPermission();
                    }
                });
        builder.create().show();
    }

    private void viewSettings(){ // Abre la actividad settings
        // intent -> intención
        // Hay dos tipos de intent: explicita o implicita
        // Esta es explicita, porque defino que quiero ir del contexto actual a un contexto que se crea con la clase SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent); // Abre una actividad nueva al partir del intent
    }
}