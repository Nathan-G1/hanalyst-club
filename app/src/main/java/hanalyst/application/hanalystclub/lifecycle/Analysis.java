package hanalyst.application.hanalystclub.lifecycle;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import hanalyst.application.hanalystclub.Adapter.AttackAdapter;
import hanalyst.application.hanalystclub.Adapter.DefenseAdapter;
import hanalyst.application.hanalystclub.Entity.Attack;
import hanalyst.application.hanalystclub.Entity.Defense;
import hanalyst.application.hanalystclub.R;
import hanalyst.application.hanalystclub.Util.AnalysisFactory;

public class Analysis extends Activity {

    GridView gridAttack, gridDefence;
    String currentTime = "";
    int minute = 0, second = 0;
    TextView timerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        gridAttack = findViewById(R.id.grid_attack);
        gridDefence = findViewById(R.id.grid_defence);
        timerTextView = findViewById(R.id.timer);
        // Attack
        final ArrayList<Attack> attacks = new AnalysisFactory().getAttackList();
        final AttackAdapter attackAdapter = new AttackAdapter(getApplicationContext(), attacks);
        gridAttack.setAdapter(attackAdapter);
        gridAttack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int prev = attacks.get(position).getValue();
                attacks.get(position).setValue(prev + 1);
                Log.d("val", "" + prev);
                gridAttack.setAdapter(new AttackAdapter(getApplicationContext(), attacks));
            }
        });
        // Defence
        final ArrayList<Defense> defence = new AnalysisFactory().getDefenseList();
        DefenseAdapter defenseAdapter = new DefenseAdapter(getApplicationContext(), defence);
        gridDefence.setAdapter(defenseAdapter);
        gridDefence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int prev = defence.get(position).getValue();
                defence.get(position).setValue(prev + 1);
                gridDefence.setAdapter(new DefenseAdapter(getApplicationContext(), defence));
            }
        });
        timer();
    }

    public void timer() {
        Timer timerObj = new Timer();
        timerObj.schedule(task, 0, 1000);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            second++;
            if (second == 60) {
                second = 0;
                minute++;
            }
            currentTime = minute + ":" + second;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timerTextView.setText(currentTime);
                }
            });
            System.out.println("Hello World");
        }
    };
}
