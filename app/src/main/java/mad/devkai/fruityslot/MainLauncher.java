package mad.devkai.fruityslot;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainLauncher extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        startActivity(new Intent(MainLauncher.this, MainSlot.class));
        finish();
    }


}
