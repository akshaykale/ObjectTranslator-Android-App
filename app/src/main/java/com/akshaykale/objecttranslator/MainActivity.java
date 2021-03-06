package com.akshaykale.objecttranslator;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MainActivity extends AppCompatActivity {

    public final int ANIMATION_DURATION = 500;

    //Views
    ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    CoordinatorLayout rootView;

    //orange to red
    String[] colourBlentA = {"#F1BA48","#EFB14B","#ECA94D","#EAA050", "#E89753","#E58F56","#E38658","#E07E5B","#DE755E","#DC6C61","#D96463","#D75B66"};// [];
    //orange to blue
    String[] colourBlentB = {"#F1BA48", "#E3B755","#D4B462","#C6B170","#B8AE7D","#A9AB8A","#9BA797","#8CA4A4","#7EA1B1","#709EBF","#619BCC","#5398D9"};// [];

    int CURRENT_POSITION = 1;
    private Window window;

    TextView toolbarTitle;

    ImageView iv_left_icon, iv_right_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        window = getWindow();
        rootView = findViewById(R.id.root_view);
        viewPager = findViewById(R.id.view_pager_main);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1,false);
        changeStatusBarColour(Color.parseColor("#F9A825"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("pager onPageScrolled","Current:"+CURRENT_POSITION+"   position: "+position+"   offset: "+positionOffset+"    pixels: "+positionOffsetPixels);
                int index = index =(int) (positionOffset*10);
                switch (CURRENT_POSITION){
                    case 0:
                        rootView.setBackgroundColor(Color.parseColor(colourBlentA[10-index]));
                        break;
                    case 1:
                        if (position == 0){ //A<-B C
                            rootView.setBackgroundColor(Color.parseColor(colourBlentA[10-index]));
                        }else if (position ==1) { //A B->C
                            rootView.setBackgroundColor(Color.parseColor(colourBlentB[index]));
                        }
                        break;
                    case 2:
                        if (position == 2){
                            rootView.setBackgroundColor(Color.parseColor(colourBlentB[9]));
                        }else
                            rootView.setBackgroundColor(Color.parseColor(colourBlentB[index]));
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                CURRENT_POSITION = position;
                Log.d("pager onPageSelected","position: "+position);
                if (position == 0){
                    rootView.setBackgroundColor(Color.parseColor(colourBlentA[9])); //Change the title bar colour
                    toolbarTitle.setText("Title Red");  //change the actionbar widget
                    changeStatusBarColour(Color.parseColor("#D50000")); //change the status bar colour

                }else if (position ==1){
                    rootView.setBackgroundColor(Color.parseColor(colourBlentA[0]));
                    toolbarTitle.setText("Title Orange");
                    changeStatusBarColour(Color.parseColor("#F9A825"));

                }else if (position == 2){
                    rootView.setBackgroundColor(Color.parseColor(colourBlentB[9]));
                    toolbarTitle.setText("Title Blue");
                    changeStatusBarColour(Color.parseColor("#00838F"));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("pager onPageStateChan","state: "+state);
            }
        });

        ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {
                view.setTranslationX(-(view.getWidth() * -position)/2);

                if(position <= -1.0F || position >= 1.0F) {
                    view.setAlpha(0.0F);
                } else if( position == 0.0F ) {
                    view.setAlpha(1.0F);
                } else {
                    // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                    view.setAlpha(1.0F - Math.abs(position));
                }
            }
        };
        viewPager.setPageTransformer(false, pageTransformer);
    }

    public void changeStatusBarColour(int colour){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(colour);
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
