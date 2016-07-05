package com.xya.MainActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.Slider;
import com.xya.LocalApplication.VariableApplication;
import com.xya.UserInterface.ActionBarView;

public class ColorActivity extends ActionBarActivity {

    private View color;
    private ImageView statusBar;
    private Toolbar toolbar;
    private Slider rColor, gColor, bColor;
    private TextView rText, gText, bText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_settings_color);
        statusBar = (ImageView) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarView.initActionbar(this, statusBar, toolbar);
        setTitle("颜色选择");
        //actionbar logo位置图标
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));

        color = findViewById(R.id.color);
        rColor = (Slider) findViewById(R.id.R);
        gColor = (Slider) findViewById(R.id.G);
        bColor = (Slider) findViewById(R.id.B);

        rText = (TextView) findViewById(R.id.rText);
        gText = (TextView) findViewById(R.id.gText);
        bText = (TextView) findViewById(R.id.bText);
        //初始状态,与当前主题相同
        int current = ((VariableApplication) this.getApplication()).getPrimaryColor();
        color.setBackgroundColor(current);
        rColor.setValue(Color.red(current));
        rText.setText("R: " + Color.red(current));
        gColor.setValue(Color.green(current));
        gText.setText("G: " + Color.green(current));
        bColor.setValue(Color.blue(current));
        bText.setText("B: " + Color.blue(current));

        rColor.setOnValueChangedListener(new onValueChangedListener());
        gColor.setOnValueChangedListener(new onValueChangedListener());
        bColor.setOnValueChangedListener(new onValueChangedListener());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_color, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_OK) {
            int primaryColor = Color.rgb(rColor.getValue(), gColor.getValue(), bColor.getValue());
            Intent intent = new Intent();
            intent.putExtra("primaryColor", primaryColor);
            setResult(23, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class onValueChangedListener implements Slider.OnValueChangedListener {
        private int red, green, blue;


        public void onValueChanged(int value) {
            this.red = rColor.getValue();
            this.green = gColor.getValue();
            this.blue = bColor.getValue();
            rText.setText("R: " + red);
            gText.setText("G: " + green);
            bText.setText("B: " + blue);

            int primaryColor = Color.rgb(red, green, blue);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                statusBar.setImageDrawable(new ColorDrawable(ActionBarView.darkenColor(primaryColor)));
            else
                statusBar.setImageDrawable(new ColorDrawable(primaryColor));
            toolbar.setBackgroundColor(primaryColor);
            color.setBackgroundColor(primaryColor);
            Log.d("primaryColor", primaryColor + "");
        }
    }
}
