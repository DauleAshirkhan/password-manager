package com.example.projectpart1;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.util.Log;



public class MenuActivity extends AppCompatActivity {

    private ArrayList<String> passwords;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private UserDatabase userDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        userDatabase = new UserDatabase(this); // Инициализация базы данных
        userDatabase.getWritableDatabase(); // Этот вызов запустит метод onCreate, если база данных еще не существует
        // Инициализация списка паролей
        passwords = new ArrayList<>();

        // Получение паролей текущего пользователя из базы данных
        Cursor cursor = userDatabase.getUserPasswords(getUserId());
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String encryptedPassword = cursor.getString(cursor.getColumnIndexOrThrow("user_password"));
                try {
                    String decryptedPassword = EncryptionHelper.decrypt(encryptedPassword);
                    passwords.add(decryptedPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Инициализация адаптера для списка паролей
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, passwords);

        listView = findViewById(R.id.passwordListView);
        listView.setAdapter(adapter);

        // Обработчик долгого нажатия на элемент списка для удаления пароля
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                showDeletePasswordDialog(position);
                return true;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_add_password) {
            // Добавление пароля
            addPassword();
            return true;
        } else if (id == R.id.nav_delete_password) {
            // Удаление пароля
            deletePassword("");
            return true;
        } else if (id == R.id.nav_logout) {
            // Логаут
            finish();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Метод для отображения диалога подтверждения удаления пароля
    private void showDeletePasswordDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this password?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String passwordToDelete = passwords.get(position);
                        deletePassword(passwordToDelete); // Передаем пароль для удаления
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }




    // Метод для получения идентификатора текущего пользователя (замените на ваш метод получения идентификатора пользователя)
    private long getUserId() {
        // Здесь должен быть ваш код для получения идентификатора текущего пользователя
        return 1; // Временно возвращаем фиксированный идентификатор
    }

    // Метод для добавления пароля в базу данных
    // Ваш метод addPassword() может выглядеть примерно так
    // Метод для добавления пароля и описания в базу данных
    // Метод для добавления пароля и описания в базу данных
    private void addPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter New Password");

        // Поля ввода для пароля и описания
        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Enter Password");
        final EditText descriptionInput = new EditText(this);
        descriptionInput.setHint("Enter Description");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(passwordInput);
        layout.addView(descriptionInput);
        builder.setView(layout);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = passwordInput.getText().toString();
                String newDescription = descriptionInput.getText().toString();
                Log.d("PasswordDebug", "New Password: " + newPassword); // Debug
                Log.d("PasswordDebug", "New Description: " + newDescription); // Debug
                if (!TextUtils.isEmpty(newPassword)) {
                    long userId = getUserId();
                    long result = userDatabase.addPassword(userId, newPassword, newDescription); // Передаем и пароль, и описание
                    Log.d("PasswordDebug", "Result: " + result); // Debug
                    if (result != -1) {
                        passwords.add(newPassword + ": " + newDescription);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MenuActivity.this, "Password added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MenuActivity.this, "Failed to add password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MenuActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }




    // Метод для добавления пароля и описания в базу данных


    // Добавляем этот метод для добавления пароля с описанием



    // Метод для удаления пароля и описания из базы данных
    private void deletePassword(String passwordWithDescription) {
        String[] parts = passwordWithDescription.split(": ");
        String password = parts[0];
        String description = parts[1];
        long userId = getUserId();
        boolean success = userDatabase.deletePassword(userId, password, description);
        if (success) {
            passwords.remove(passwordWithDescription); // Удаляем пароль и описание из списка
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Password deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete password", Toast.LENGTH_SHORT).show();
        }
    }
}


