package com.example.gallery;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static com.example.gallery.R.id.info;

public class FullImageActivity extends AppCompatActivity {
    ViewPager slider;
    Toolbar img_toolbar;
    //ImageView fullImage;
    FullImageSlider imageSlider;
    ArrayList<File> arrayList;
    int CurrentPosition;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        img_toolbar = (Toolbar) findViewById(R.id.image_toolbar);
        setSupportActionBar(img_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        String data = getIntent().getExtras().getString("id");
        //fullImage.setImageURI(Uri.parse(data));
        slider =(ViewPager) findViewById(R.id.image_viewpaprer);
        arrayList = (ArrayList<File>) getIntent().getSerializableExtra("list");
        CurrentPosition = getIntent().getExtras().getInt("position");
        System.out.println("hihi"+arrayList);
        imageSlider =new FullImageSlider(FullImageActivity.this,arrayList,data);
        slider.setAdapter(imageSlider);
        slider.setCurrentItem(CurrentPosition);
        slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CurrentPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_menu_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()){
            case info:
                ShowImageDetail();
                Toast.makeText(FullImageActivity.this, "Detail" , Toast.LENGTH_LONG).show();
                return true;
            case R.id.edit:
                EditImage();
                Toast.makeText(FullImageActivity.this, "Edit clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.delete:
                DeleteImage();
                Toast.makeText(FullImageActivity.this, "Delete clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.img_favorite:
                Toast.makeText(FullImageActivity.this, "Favorite clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.home:
            case android.R.id.home:
                //finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void DeleteImage() {
        String imgPath = arrayList.get(CurrentPosition).getParentFile().getPath();
        String imgName= arrayList.get(CurrentPosition).getName();
        File dir = new File(imgPath);
        dir.mkdir();
        Toast.makeText(FullImageActivity.this, imgPath + imgName, Toast.LENGTH_LONG).show();

        final File temp = new File(dir, imgName);
        boolean succ = temp.delete();
        if(succ == false)
        {
            Toast.makeText(FullImageActivity.this, "Delete failed", Toast.LENGTH_LONG).show();
            return;
        }
        else
            Toast.makeText(FullImageActivity.this, "Delete successful", Toast.LENGTH_LONG).show();
        arrayList.get(CurrentPosition).delete();
        if(arrayList.size() == 1)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return;
        }
        if(CurrentPosition - 1 >= 0) {
            slider.setCurrentItem(CurrentPosition - 1);
        }
        else {
            slider.setCurrentItem(CurrentPosition + 1);
        }
    }

    public void ShowImageDetail(){
        Date lastModifiedDate = new Date(arrayList.get(CurrentPosition).lastModified());
        String lastModified = lastModifiedDate.toString();
        String imgName= arrayList.get(CurrentPosition).getName();
        String imgPath =arrayList.get(CurrentPosition).getParentFile().getPath();
        double imgSize = (double)arrayList.get(CurrentPosition).length()/(double)(1024*1024);

        Intent myIntentA1A2;
        myIntentA1A2 = new Intent(FullImageActivity.this, ImageDetail.class);
        Bundle myBundle1 = new Bundle();
        myBundle1.putString("date", lastModified);
        myBundle1.putString("name", imgName);
        myBundle1.putString("path", imgPath);
        myBundle1.putDouble("size", imgSize);
        myIntentA1A2.putExtras(myBundle1);
        startActivityForResult(myIntentA1A2, 1122);
    }

    public void EditImage() {
        Intent i= new Intent(FullImageActivity.this,EditImageActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("path",arrayList.get(CurrentPosition).getPath());
        i.putExtra("array",arrayList);
        i.putExtra("pos",CurrentPosition);
        startActivity(i);
    }
}
