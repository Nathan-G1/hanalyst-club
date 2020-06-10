package hanalyst.application.hanalystclub.lifecycle;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import hanalyst.application.hanalystclub.Adapter.AttackAdapter;
import hanalyst.application.hanalystclub.Adapter.DefenseAdapter;
import hanalyst.application.hanalystclub.Adapter.PlayersAdapter;
import hanalyst.application.hanalystclub.Entity.Attack;
import hanalyst.application.hanalystclub.Entity.Defense;
import hanalyst.application.hanalystclub.Entity.Player;
import hanalyst.application.hanalystclub.R;
import hanalyst.application.hanalystclub.Util.AnalysisFactory;
import hanalyst.application.hanalystclub.lifecycle.viewmodels.PlayerViewModel;

public class Analysis extends AppCompatActivity {

    GridView gridAttack, gridDefence;
    String currentTime = "";
    Timer timerObj = new Timer();
    int minute = 0, second = 0;
    TextView timerTextView, pauseText;
    boolean pause = false;
    PlayerViewModel playerViewModel;
    int zone = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);


        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        gridAttack = findViewById(R.id.grid_attack);
        gridDefence = findViewById(R.id.grid_defence);
        timerTextView = findViewById(R.id.timer);
        pauseText = findViewById(R.id.pause);
        // Attack
        final ArrayList<Attack> attacks = new AnalysisFactory().getAttackList();
        final AttackAdapter attackAdapter = new AttackAdapter(getApplicationContext(), attacks);
        gridAttack.setAdapter(attackAdapter);
        gridAttack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int prev = attacks.get(position).getValue();
                attacks.get(position).setValue(prev + 1);
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
                final Dialog dialog = new Dialog(Analysis.this, R.style.FullHeightDialog);

                dialog.setContentView(R.layout.player_list_who);

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                RecyclerView recyclerView = dialog.findViewById(R.id.players_list);

                final PlayersAdapter playersAdapter = new PlayersAdapter();
                playersAdapter.setOnItemClickListener(new PlayersAdapter.ClickListener() {
                    @Override
                    public void onItemClick(int playerPosition, View v) {
                        final Dialog dialog2 = new Dialog(Analysis.this);
                        dialog2.setContentView(R.layout.zone_where);
                        Button button1 = dialog2.findViewById(R.id.btn1);
                        Button button2 = dialog2.findViewById(R.id.btn2);
                        Button button3 = dialog2.findViewById(R.id.btn3);
                        Button button4 = dialog2.findViewById(R.id.btn4);
                        Button button5 = dialog2.findViewById(R.id.btn5);
                        Button button6 = dialog2.findViewById(R.id.btn6);
                        Button button7 = dialog2.findViewById(R.id.btn7);
                        Button button8 = dialog2.findViewById(R.id.btn8);
                        Button button9 = dialog2.findViewById(R.id.btn9);
                        Button button10 = dialog2.findViewById(R.id.btn10);
                        Button button11 = dialog2.findViewById(R.id.btn11);
                        Button button12 = dialog2.findViewById(R.id.btn12);
                        ArrayList<Button> arrayList = new ArrayList<>();

                        dialog2.show();
                        arrayList.add(button1);
                        arrayList.add(button2);
                        arrayList.add(button3);
                        arrayList.add(button4);
                        arrayList.add(button5);
                        arrayList.add(button6);
                        arrayList.add(button7);
                        arrayList.add(button8);
                        arrayList.add(button9);
                        arrayList.add(button10);
                        arrayList.add(button11);
                        arrayList.add(button12);

                        for (Button button : arrayList) {
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    zone = arrayList.indexOf(button) + 1;
//                                    saveNotation(defence.get(position), zone, playerPosition, currentTime);
                                    Toast.makeText(Analysis.this, zone + " is zone", Toast.LENGTH_SHORT).show();
//                                    dialog2.dismiss();
                                }
                            });
                        }
//                        zoneDialog.show();
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


                recyclerView.setAdapter(playersAdapter);
                playerViewModel.getAllPlayers().observe(Analysis.this, new Observer<List<Player>>() {
                    @Override
                    public void onChanged(List<Player> players) {
                        playersAdapter.setPlayers(players);
                    }
                });
                dialog.show();
                gridDefence.setAdapter(new DefenseAdapter(getApplicationContext(), defence));
            }
        });
        timer();
    }

    public void timer() {
        this.timerObj = new Timer();
        this.timerObj.schedule(setTask(), 0, 1000);
    }

    public TimerTask setTask() {
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
            }
        };
        return task;
    }

    public void pauseTimer(View v) {
        pause = !pause;
        if (pause) {
            this.timerObj.cancel();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String p = "RESUME";
                    pauseText.setText(p);
                }
            });
        } else {
            timer();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String r = "PAUSE";
                    pauseText.setText(r);
                }
            });
        }
    }

    private void saveNotation(Defense defense, int zone, int playerPosition, String currentTime) {

    }

    private void saveNotation(Attack attack, int zone, int playerPosition, String currentTime) {

    }

    private boolean saveNotation() {
        return false;
    }
}
