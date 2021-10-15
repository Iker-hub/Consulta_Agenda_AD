package org.izv.igg.consultaagendaad;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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

        SharedPreferences preferenciasActividad = getPreferences(Context.MODE_PRIVATE);
        String lastSearch = preferenciasActividad.getString(getString(R.string.last_search),"");
        if(!lastSearch.isEmpty()){
            etPhone.setText(lastSearch);
        }

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

        // Buscar entre contactos
        // ContentProvider Proveedor de contenidos
        // ContentResolver Consultor de contenidos
        // Queries the user dictionary and returns results
        // url: https://ieszaidinvergeles.org/carpeta/carpeta2/pagina.html?dato=1
        // uri: protocolo://direccion/ruta/recurso

        String phone = etPhone.getText().toString();

        tvResult.setText("");

        // Guardamos la última busqueda de contacto
        SharedPreferences preferenciasActividad = getPreferences(Context.MODE_PRIVATE); // Preferencias compartidas de la actividad actual, coge el nombre de la actividad actual
        SharedPreferences.Editor editor = preferenciasActividad.edit();
        editor.putString(getString(R.string.last_search),phone);
        editor.commit();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this ); // SharedPreferences: Permite acceder a las preferencias compartidas de mi aplicación
        String email = sharedPreferences.getString(getString(R.string.settings_email), getString(R.string.no_email)); // Así cogemos el String
        tvResult.append(email + "\n");

        phone = searchFormat(phone);

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String proyeccion[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                               ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}; // Todos los campos
        String seleccion = ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";
        String argumentos[] = new String[]{phone};
        String orden = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);

        String[] columnas = cursor.getColumnNames();
        int columnaNombre = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int columnaNumero = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        String nombre, numero;

        while (cursor.moveToNext()){
        nombre = cursor.getString(columnaNombre);
        numero = cursor.getString(columnaNumero);
            for (String s : columnas) {
                int pos = cursor.getColumnIndex(s);
                String valor = cursor.getString(pos);
                tvResult.append(s + " " + valor + "\n");
            }
        }
    }

    private String searchFormat(String phone) {
        String newString = "";
        for (char ch : phone.toCharArray()){
            newString += ch + "%";
        }
        return newString;
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