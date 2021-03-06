package hanalyst.application.hanalystclub.lifecycle.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import hanalyst.application.hanalystclub.Entity.Game;
import hanalyst.application.hanalystclub.Entity.GameType;
import hanalyst.application.hanalystclub.Entity.Team;
import hanalyst.application.hanalystclub.Entity.remote.RGame;
import hanalyst.application.hanalystclub.Network.API;
import hanalyst.application.hanalystclub.R;
import hanalyst.application.hanalystclub.Util.FieldValidation;
import hanalyst.application.hanalystclub.Util.SharedPreferenceHAn;
import hanalyst.application.hanalystclub.Util.TimeManager;
import hanalyst.application.hanalystclub.lifecycle.Analysis;
import hanalyst.application.hanalystclub.lifecycle.HomeActivity;
import hanalyst.application.hanalystclub.lifecycle.viewmodels.GameTypeViewModel;
import hanalyst.application.hanalystclub.lifecycle.viewmodels.GameViewModel;
import hanalyst.application.hanalystclub.lifecycle.viewmodels.TeamViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FragmentGameForm extends Fragment {

    private TeamViewModel teamViewModel;
    private GameTypeViewModel gameTypeViewModel;
    private GameViewModel gameViewModel;
    private SharedPreferenceHAn sharedPreferenceHAn;
    private String teamId;
    private boolean ha = true;
    private TimeManager tM;
    private List<String> opponentTeamsName;

    public FragmentGameForm() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeActivity) getActivity()).setTitle("Game Information");
        View view = inflater.inflate(R.layout.fragment_game_form, container, false);
        TextView gameStartTime = view.findViewById(R.id.game_starting_hour);
        EditText venueEditText = view.findViewById(R.id.venue);
        EditText refereeNameEditText = view.findViewById(R.id.referee_name);
        RadioGroup radioGroup = view.findViewById(R.id.home_or_away_radio_group);
        Spinner gameTypeSpinner = view.findViewById(R.id.spinner_game_type);
        Spinner opponentTeamSpinner = view.findViewById(R.id.spinner_opponent_team);
        Spinner gameFormation = view.findViewById(R.id.spinner_game_formation);
        Button startGame = view.findViewById(R.id.game_start_button);

        gameStartTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    gameStartTime.setText("Time: " + selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ha = checkedId == R.id.game_home;
            }
        });

        // get currently signed in Team
        sharedPreferenceHAn = new SharedPreferenceHAn(getContext());
        teamId = sharedPreferenceHAn.getTeamId();

        teamViewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        gameTypeViewModel = new ViewModelProvider(this).get(GameTypeViewModel.class);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        opponentTeamsName = new ArrayList<>();
        opponentTeamsName.add("Select One");

        teamViewModel.getAllTeams().observe(getViewLifecycleOwner(), teams -> {
            for (Team team : teams) {
                if (!team.getId().equals(teamId)) opponentTeamsName.add(team.getName());
            }
        });
        List<String> formationsList = new ArrayList<String>( Arrays.asList("3-4-4", "4-4-3", "4-4-2") );
        ArrayAdapter<String> gameFormationArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, formationsList);
        gameFormationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameFormation.setAdapter(gameFormationArrayAdapter);

        ArrayAdapter<String> opponentTeamsArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, opponentTeamsName);
        opponentTeamsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        opponentTeamSpinner.setAdapter(opponentTeamsArrayAdapter);

        List<String> gameTypes = new ArrayList<>();
        gameTypes.add("Select One");

        gameTypeViewModel.getAllGameTypes().observe(getViewLifecycleOwner(), gameTypes1 -> {
            for (GameType gameType : gameTypes1) {
                gameTypes.add(gameType.getGameType());
            }
        });

        ArrayAdapter<String> gameTypesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, gameTypes);
        gameTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameTypeSpinner.setAdapter(gameTypesAdapter);


        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGameInfo(v, venueEditText, refereeNameEditText, gameTypeSpinner, opponentTeamSpinner, gameFormation);
            }
        });

        return view;
    }

    private void saveGameInfo(
            View v, EditText venueEditText, EditText refereeNameEditText,
            Spinner gameTypeSpinner, Spinner opponentTeamSpinner, Spinner gameFormation) {
        FieldValidation fv = new FieldValidation(getContext());
        tM = new TimeManager(getContext());
        String currentTime = tM.getCurrentTime();
        String endTime = tM.getEndTime();

        if (fv.isEmpty(venueEditText)) {
            fv.validateNotBlank(venueEditText);
            return;
        }
        if (fv.isEmpty(refereeNameEditText)) {
            fv.validateNotBlank(refereeNameEditText);
            return;
        }
        if (!fv.selectedItemPosition(gameTypeSpinner)) {
            return;
        }
        if (!fv.selectedItemPosition(opponentTeamSpinner)) {
            return;
        }

        List<String> playingTeams = new ArrayList<>();
        playingTeams.add(sharedPreferenceHAn.getTeamName());
        playingTeams.add(opponentTeamSpinner.getSelectedItem().toString());
        sharedPreferenceHAn.setPlayingTeams(sharedPreferenceHAn.getTeamName() + " vs " + opponentTeamSpinner.getSelectedItem().toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hanalyst.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        Call<RGame> call = api.saveGame(
                currentTime, endTime, venueEditText.getText().toString(), ha, refereeNameEditText.getText().toString(),
                "Celsius 23.12", "Addis Ababa", gameTypeSpinner.getSelectedItem().toString(), playingTeams, gameFormation.getSelectedItem().toString()
        );
        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle(getString(R.string.game_starting));
        progress.setMessage(getString(R.string.loading));
        progress.show();
        call.enqueue(new Callback<RGame>() {
            @Override
            public void onResponse(Call<RGame> call, Response<RGame> response) {
                Object j = response.body();
                if (response.isSuccessful()) {
                    RGame game1 = response.body();
                    gameViewModel.insertGame(new Game(
                            game1.getId(),
                            game1.getStartTime(),
                            game1.getEndTime(),
                            game1.getVenue(),
                            game1.isHa(),
                            game1.getReferee(),
                            game1.getTemperature(),
                            game1.getGameType(),
                            game1.getPlayingTeams(),
                            game1.getFormation()
                    ));
                    sharedPreferenceHAn.setCurrentGameId(game1.getId());
                    progress.dismiss();
                    startActivity(new Intent(getActivity(), Analysis.class));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getContext(), jObjError.getJSONObject("error").getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        progress.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RGame> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), R.string.problem_in_network, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
