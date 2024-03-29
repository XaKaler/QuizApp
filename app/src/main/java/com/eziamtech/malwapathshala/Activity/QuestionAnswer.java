package com.eziamtech.malwapathshala.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eziamtech.malwapathshala.Adapter.SelectLanguageAdapter;
import com.eziamtech.malwapathshala.Interface.RecycleViewClickListener;
import com.eziamtech.malwapathshala.Model.QuestionLanguage.QuestionLanguageModel;
import com.eziamtech.malwapathshala.Model.QuestionModel.QuestionModel;
import com.eziamtech.malwapathshala.Model.QuestionModel.Result;
import com.eziamtech.malwapathshala.Model.SuccessModel.SuccessModel;
import com.eziamtech.malwapathshala.Model.language.GetLanguage;
import com.eziamtech.malwapathshala.R;
import com.eziamtech.malwapathshala.Util.LocaleUtils;
import com.eziamtech.malwapathshala.Util.PrefManager;
import com.eziamtech.malwapathshala.Util.Utility;
import com.eziamtech.malwapathshala.Webservice.AppAPI;
import com.eziamtech.malwapathshala.Webservice.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iwgang.countdownview.CountdownView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionAnswer extends AppCompatActivity implements View.OnClickListener,
        CountdownView.OnCountdownEndListener, CountdownView.OnCountdownIntervalListener {

    private PrefManager prefManager;
    Map<String, String> map;

    ShimmerFrameLayout shimmer;

    private ExtendedFloatingActionButton efbSelectLanguageQna;
    private FloatingActionButton fbEnglishQna, fabHindiQna, fabUrdhuQna;

    ImageView ivQuestion;
    TextView txtToolbarTitle, txtQuestion, txtLevelNumber, txtQueNumber, txtTotalQue,
            txtRightAnswers, txtWrongAnswers, txtA, txtB, txtC, txtD, txtOptionA, txtOptionB,
            txtOptionC, txtOptionD, txtTickA, txtTickB, txtTickC, txtTickD,
            txtAnswerit, txtNext, txtAnswerStatus, tvUrdhuQna, tvHindiQna, tvEnglishQna;

    CountdownView countdownTimer;

    List<Result> questionList;

    LinearLayout lyToolbar, lyBack, lyBottom, lyOptionA, lyOptionB, lyOptionC, lyOptionD;

    String levelID, catID, strAnswer = "", TotalLevel, currentLevel;
    int QueNo = 0, rightAnswers = 0, wrongAnswers = 0, attendedQue = 0;

    Boolean isAllFabVisible;

    String currentLanguage = "en";

    MediaPlayer mpWrong, mpCorrect;
    Vibrator vibe;

    boolean isAnswered = false, isSelected = false;

    public static Handler handler;
    public static Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);
        PrefManager.forceRTLIfSupported(getWindow(), QuestionAnswer.this);
        vibe = (Vibrator) QuestionAnswer.this.getSystemService(Context.VIBRATOR_SERVICE);

        init();
        GetQuestionByLevel();

        // get current language
        currentLanguage = prefManager.getValue("select_language");
        Log.e("lan_currentLan", "" + currentLanguage);

        currentLanguage = LocaleUtils.getSelectedLanguageId();
        Log.e("currentLanguage", "" + currentLanguage);

        txtToolbarTitle.setText("" + getResources().getString(R.string.questions));

        lyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionAnswer.this.finish();
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                isAnswered = true;
                if (isAnswered && isSelected) {
                    attendedQue = attendedQue + 1;
                    isSelected = false;
                }
                updateUI();

                if (txtNext.getText().toString().equalsIgnoreCase(getString(R.string.finish))) {
                    Log.e("lavel_id ==>", "" + levelID);
                    Log.e("questions_attended ==>", "" + attendedQue);
                    Log.e("total_questions ==>", "" + questionList.size());
                    Log.e("correct_answers ==>", "" + rightAnswers);
                    Log.e("user_id ==>", "" + prefManager.getLoginId());
                    Save_Question_Report();

                } else {
                    Log.e("QueNo ==>", "" + QueNo);
                    if (questionList.size() > 1) {
                        if (QueNo == (questionList.size() - 2)) {
                            txtNext.setText(getResources().getString(R.string.finish));
                        }

                        QueNo = QueNo + 1;

                        if (!questionList.get(QueNo).getImage().equalsIgnoreCase("")) {
                            ivQuestion.setVisibility(View.VISIBLE);
                            Picasso.get().load(questionList.get(QueNo).getImage()).into(ivQuestion);
                        } else {
                            ivQuestion.setVisibility(View.GONE);
                        }
                        txtQuestion.setText("" + questionList.get(QueNo).getQuestion());
                        txtOptionA.setText("" + questionList.get(QueNo).getOptionA());
                        txtOptionB.setText("" + questionList.get(QueNo).getOptionB());
                        if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                            lyOptionC.setVisibility(View.INVISIBLE);
                        } else {
                            lyOptionC.setVisibility(View.VISIBLE);
                            txtOptionC.setText("" + questionList.get(QueNo).getOptionC());
                        }
                        if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                            lyOptionD.setVisibility(View.INVISIBLE);
                        } else {
                            lyOptionD.setVisibility(View.VISIBLE);
                            txtOptionD.setText("" + questionList.get(QueNo).getOptionD());
                        }
                        txtLevelNumber.setText("" + currentLevel);
                        txtQueNumber.setText("" + (QueNo + 1));
                        txtTotalQue.setText(" / " + questionList.size());
                        countdownTimer.start(31000);
                    } else {
                        txtNext.setText(getResources().getString(R.string.finish));
                    }
                }
            }
        };

    }

    private void init() {
        try {
            prefManager = new PrefManager(QuestionAnswer.this);
            map = new HashMap<>();
            map = Utility.GetMap(QuestionAnswer.this);

            Intent intent = getIntent();
            if (intent.hasExtra("catID") && intent.hasExtra("levelID")) {
                catID = intent.getStringExtra("catID");
                levelID = intent.getStringExtra("levelID");
                TotalLevel = intent.getStringExtra("TotalLevel");
                currentLevel = intent.getStringExtra("currentLevel");
                Log.e("catID ==>", "" + catID);
                Log.e("levelID ==>", "" + levelID);
                Log.e("TotalLevel ==>", "" + TotalLevel);
                Log.e("currentLevel ==>", "" + currentLevel);
            }

            shimmer = findViewById(R.id.shimmer);
            lyBottom = findViewById(R.id.lyBottom);

            lyToolbar = findViewById(R.id.lyToolbar);
            lyToolbar.setVisibility(View.VISIBLE);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            countdownTimer = findViewById(R.id.countdown_timer);
            countdownTimer.setOnCountdownEndListener(this);
            countdownTimer.setOnCountdownIntervalListener(10000, this);

            ivQuestion = findViewById(R.id.ivQuestion);
            txtLevelNumber = findViewById(R.id.txtLevelNumber);
            txtQueNumber = findViewById(R.id.txtQueNumber);
            txtTotalQue = findViewById(R.id.txtTotalQue);
            txtQuestion = findViewById(R.id.txtQuestion);
            txtRightAnswers = findViewById(R.id.txtRightAnswers);
            txtWrongAnswers = findViewById(R.id.txtWrongAnswers);
            txtA = findViewById(R.id.txtA);
            txtB = findViewById(R.id.txtB);
            txtC = findViewById(R.id.txtC);
            txtD = findViewById(R.id.txtD);
            txtTickA = findViewById(R.id.txtTickA);
            txtTickB = findViewById(R.id.txtTickB);
            txtTickC = findViewById(R.id.txtTickC);
            txtTickD = findViewById(R.id.txtTickD);
            txtOptionA = findViewById(R.id.txtOptionA);
            txtOptionB = findViewById(R.id.txtOptionB);
            txtOptionC = findViewById(R.id.txtOptionC);
            txtOptionD = findViewById(R.id.txtOptionD);
            txtAnswerStatus = findViewById(R.id.txtAnswerStatus);
            txtAnswerit = findViewById(R.id.txtAnswerit);
            txtNext = findViewById(R.id.txtNext);

            lyOptionA = findViewById(R.id.lyOptionA);
            lyOptionB = findViewById(R.id.lyOptionB);
            lyOptionC = findViewById(R.id.lyOptionC);
            lyOptionD = findViewById(R.id.lyOptionD);

            efbSelectLanguageQna = findViewById(R.id.efbSelectLanguageQna);
            fbEnglishQna = findViewById(R.id.fbEnglishQna);
            fabHindiQna = findViewById(R.id.fabHindiQna);
            fabUrdhuQna = findViewById(R.id.fabUrdhuQna);
            tvHindiQna = findViewById(R.id.tvHindiQna);
            tvEnglishQna = findViewById(R.id.tvEnglishQna);
            tvUrdhuQna = findViewById(R.id.tvUrdhuQna);

            //floating action buttons
            fbEnglishQna.setVisibility(View.GONE);
            fabUrdhuQna.setVisibility(View.GONE);
            fabHindiQna.setVisibility(View.GONE);
            tvEnglishQna.setVisibility(View.GONE);
            tvHindiQna.setVisibility(View.GONE);
            tvUrdhuQna.setVisibility(View.GONE);
            isAllFabVisible = false;
            efbSelectLanguageQna.shrink();

            efbSelectLanguageQna.setOnClickListener(this);

            lyBack.setOnClickListener(this);
            txtAnswerit.setOnClickListener(this);
            txtNext.setOnClickListener(this);
            lyOptionA.setOnClickListener(this);
            lyOptionB.setOnClickListener(this);
            lyOptionC.setOnClickListener(this);
            lyOptionD.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init Exception =>", "" + e);
        }
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                QuestionAnswer.this.finish();
                break;

            case R.id.txtAnswerit:
                Log.e("countdownTimer ==>", "" + countdownTimer.getSecond());
                Log.e("QueNo ==>", "" + QueNo);
                Log.e("strAnswer ==>", "" + strAnswer);
                Log.e("Answer ==>", "" + questionList.get(QueNo).getAnswer());
                onAnswerIt();
                break;

            case R.id.txtNext:
                onNextFinish();
                break;

            case R.id.efbSelectLanguageQna:
                showAndHideLanguage();
                break;

            case R.id.fbEnglishQna:
                if (!questionList.get(0).getImage().equalsIgnoreCase("")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Picasso.get().load(questionList.get(0).getImage()).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
                txtQuestion.setText("" + questionList.get(0).getQuestion());
                txtOptionA.setText("" + questionList.get(0).getOptionA());
                txtOptionB.setText("" + questionList.get(0).getOptionB());
                if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                    lyOptionC.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionC.setVisibility(View.VISIBLE);
                    txtOptionC.setText("" + questionList.get(0).getOptionC());
                }
                if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                    lyOptionD.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionD.setVisibility(View.VISIBLE);
                    txtOptionD.setText("" + questionList.get(0).getOptionD());
                }

                txtLevelNumber.setText("" + currentLevel);
                txtQueNumber.setText("" + (QueNo + 1));
                txtRightAnswers.setText(getResources().getString(R.string.right) + " " + rightAnswers);
                txtWrongAnswers.setText(getResources().getString(R.string.wrong) + " " + wrongAnswers);

                txtTotalQue.setText(" / " + questionList.size());

                if (questionList.size() == 1) {
                    txtNext.setText(getResources().getString(R.string.finish));
                }

                // set all option to unselect and start timer
                modifyOptions();
                countdownTimer.start(31000);

                showAndHideLanguage();
                break;

            case R.id.fabHindiQna:
                changeQuestionLanguage("3", questionList.get(QueNo).getId());
                showAndHideLanguage();
                break;

            case R.id.fabUrdhuQna:
                changeQuestionLanguage("6", questionList.get(QueNo).getId());
                showAndHideLanguage();
                break;

            case R.id.lyOptionA:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "1";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickA.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtA.setTextColor(getResources().getColor(R.color.white));
                    txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionA.setTextColor(getResources().getColor(R.color.white));
                    txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;

            case R.id.lyOptionB:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "2";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickB.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtB.setTextColor(getResources().getColor(R.color.white));
                    txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionB.setTextColor(getResources().getColor(R.color.white));
                    txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;

            case R.id.lyOptionC:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "3";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickC.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtC.setTextColor(getResources().getColor(R.color.white));
                    txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.white));
                    txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;

            case R.id.lyOptionD:
                if (!isAnswered) {
                    isSelected = true;
                    strAnswer = "4";
                    txtAnswerStatus.setVisibility(View.GONE);
                    lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_primary));
                    lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
                    lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

                    txtTickD.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                    txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
                    txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

                    txtD.setTextColor(getResources().getColor(R.color.white));
                    txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionD.setTextColor(getResources().getColor(R.color.white));
                    txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
                    txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
                break;
        }
    }


    private void showAndHideLanguage() {

        /*// set visibility
        if (!isAllFabVisible) {
            fbEnglishQna.setVisibility(View.VISIBLE);
            fabUrdhuQna.setVisibility(View.VISIBLE);
            fabHindiQna.setVisibility(View.VISIBLE);
            tvEnglishQna.setVisibility(View.VISIBLE);
            tvHindiQna.setVisibility(View.VISIBLE);
            tvUrdhuQna.setVisibility(View.VISIBLE);
            isAllFabVisible = true;
            efbSelectLanguageQna.extend();
        } else {
            fbEnglishQna.setVisibility(View.GONE);
            fabUrdhuQna.setVisibility(View.GONE);
            fabHindiQna.setVisibility(View.GONE);
            tvEnglishQna.setVisibility(View.GONE);
            tvHindiQna.setVisibility(View.GONE);
            tvUrdhuQna.setVisibility(View.GONE);
            isAllFabVisible = false;
            efbSelectLanguageQna.shrink();
        }

        // click listener on floating action buttons
        fbEnglishQna.setOnClickListener(this);
        fabHindiQna.setOnClickListener(this);
        fabUrduQna.setOnClickListener(this);*/

        View view = getLayoutInflater().inflate(R.layout.select_language, null);
        RecyclerView recyclerView = view.findViewById(R.id.rvLanguageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle("Select your language")
                .setView(view)
                .setCancelable(true);

        AlertDialog builder = dialog.create();

        // get language list
        Call<GetLanguage> call = BaseURL.getVideoAPI().getLanguage();
        call.enqueue(new Callback<GetLanguage>() {
            @Override
            public void onResponse(Call<GetLanguage> call, Response<GetLanguage> response) {
                try {
                    if(response.code() == 200 && response.body().getStatus() == 200){
                        if(response.body().getResult().size() > 0){
                            recyclerView.setAdapter(new SelectLanguageAdapter(getApplicationContext(), response.body().getResult(), position -> {
                                if(response.body().getResult().get(position).getLanguage().equals("English")) setEnglishLanguage();
                                    // Toast.makeText(getApplicationContext(), response.body().getResult().get(position).getId()+" item clicked", Toast.LENGTH_SHORT).show();
                                else {
                                    changeQuestionLanguage(response.body().getResult().get(position).getId(), questionList.get(QueNo).getId());
                                }
                                builder.dismiss();
                            }));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetLanguage> call, Throwable t) {

            }
        });


        builder.show();
    }

    private void setEnglishLanguage() {
        if (!questionList.get(0).getImage().equalsIgnoreCase("")) {
            ivQuestion.setVisibility(View.VISIBLE);
            Picasso.get().load(questionList.get(0).getImage()).into(ivQuestion);
        } else {
            ivQuestion.setVisibility(View.GONE);
        }
        txtQuestion.setText("" + questionList.get(0).getQuestion());
        txtOptionA.setText("" + questionList.get(0).getOptionA());
        txtOptionB.setText("" + questionList.get(0).getOptionB());
        if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
            lyOptionC.setVisibility(View.INVISIBLE);
        } else {
            lyOptionC.setVisibility(View.VISIBLE);
            txtOptionC.setText("" + questionList.get(0).getOptionC());
        }
        if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
            lyOptionD.setVisibility(View.INVISIBLE);
        } else {
            lyOptionD.setVisibility(View.VISIBLE);
            txtOptionD.setText("" + questionList.get(0).getOptionD());
        }

        txtLevelNumber.setText("" + currentLevel);
        txtQueNumber.setText("" + (QueNo + 1));
        txtRightAnswers.setText(getResources().getString(R.string.right) + " " + rightAnswers);
        txtWrongAnswers.setText(getResources().getString(R.string.wrong) + " " + wrongAnswers);

        txtTotalQue.setText(" / " + questionList.size());

        if (questionList.size() == 1) {
            txtNext.setText(getResources().getString(R.string.finish));
        }

        // set all option to unselect and start timer
        modifyOptions();
        countdownTimer.start(31000);

    }

    private void changeQuestionLanguage(String languageId, String questionId) {
        Call<QuestionLanguageModel> questionLanguageModelCall = BaseURL.getVideoAPI().getChangedLanguageQuestion(questionId, languageId );
        questionLanguageModelCall.enqueue(new Callback<QuestionLanguageModel>() {
            @Override
            public void onResponse(Call<QuestionLanguageModel> call, Response<QuestionLanguageModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        if (response.body().getResult().size() > 0) {
                            List<com.eziamtech.malwapathshala.Model.QuestionLanguage.Result> responseData = response.body().getResult();

                            // set question image if available otherwise hide image view
                            if (!responseData.get(0).getImage().equalsIgnoreCase("")) {
                                ivQuestion.setVisibility(View.VISIBLE);
                                Picasso.get().load(responseData.get(0).getImage()).into(ivQuestion);
                            } else {
                                ivQuestion.setVisibility(View.GONE);
                            }

                            // set question and its options
                            txtQuestion.setText("" + responseData.get(0).getQuestion());
                            txtOptionA.setText("" + responseData.get(0).getOptionA());
                            txtOptionB.setText("" + responseData.get(0).getOptionB());
                            if (responseData.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                                lyOptionC.setVisibility(View.INVISIBLE);
                            } else {
                                lyOptionC.setVisibility(View.VISIBLE);
                                txtOptionC.setText("" + responseData.get(0).getOptionC());
                            }
                            if (responseData.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                                lyOptionD.setVisibility(View.INVISIBLE);
                            } else {
                                lyOptionD.setVisibility(View.VISIBLE);
                                txtOptionD.setText("" + responseData.get(0).getOptionD());
                            }

                            // set all option to unselect and start timer
                            modifyOptions();
                            countdownTimer.start(31000);
                        }
                    } else {
                        Toasty.info(getApplicationContext(), "Question is not available in this language", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<QuestionLanguageModel> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void modifyOptions() {
        txtAnswerStatus.setVisibility(View.GONE);
        lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

        txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

        txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtD.setTextColor(getResources().getColor(R.color.text_color_primary));

        txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));
    }

    //get_lavel API call
    private void GetQuestionByLevel() {
        Utility.shimmerShow(shimmer);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<QuestionModel> call = bookNPlayAPI.get_question_by_lavel("" + catID, "" + levelID);
        call.enqueue(new Callback<QuestionModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<QuestionModel> call, @NonNull Response<QuestionModel> response) {
                try {
                    Log.e("get_question_by_lavel status =>", "" + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        questionList = response.body().getResult();
                        if (response.body().getResult().size() > 0) {
                            questionList = new ArrayList<>();
                            questionList = response.body().getResult();
                            Log.e("questionList size", "" + questionList.size());

                            if (!questionList.get(0).getImage().equalsIgnoreCase("")) {
                                ivQuestion.setVisibility(View.VISIBLE);
                                Picasso.get().load(questionList.get(0).getImage()).into(ivQuestion);
                            } else {
                                ivQuestion.setVisibility(View.GONE);
                            }
                            txtQuestion.setText("" + questionList.get(0).getQuestion());
                            txtOptionA.setText("" + questionList.get(0).getOptionA());
                            txtOptionB.setText("" + questionList.get(0).getOptionB());
                            if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                                lyOptionC.setVisibility(View.INVISIBLE);
                            } else {
                                lyOptionC.setVisibility(View.VISIBLE);
                                txtOptionC.setText("" + questionList.get(0).getOptionC());
                            }
                            if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                                lyOptionD.setVisibility(View.INVISIBLE);
                            } else {
                                lyOptionD.setVisibility(View.VISIBLE);
                                txtOptionD.setText("" + questionList.get(0).getOptionD());
                            }

                            txtLevelNumber.setText("" + currentLevel);
                            txtQueNumber.setText("" + (QueNo + 1));
                            txtRightAnswers.setText(getResources().getString(R.string.right) + " " + rightAnswers);
                            txtWrongAnswers.setText(getResources().getString(R.string.wrong) + " " + wrongAnswers);

                            txtTotalQue.setText(" / " + questionList.size());

                            if (questionList.size() == 1) {
                                txtNext.setText(getResources().getString(R.string.finish));
                            }
                            countdownTimer.start(31000);
                        }

                    } else {
                        Log.e("get_question_by_lavel massage =>", "" + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_question_by_lavel API error==>", "" + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<QuestionModel> call, @NonNull Throwable t) {
                Log.e("get_question_by_lavel Failure", "That didn't work!!!" + t.getMessage());
                Utility.shimmerHide(shimmer);
            }
        });
    }

    @Override
    public void onEnd(CountdownView cv) {
        Log.e("cv ==>", "" + cv.getSecond());
        Log.e("onEnd QueNo ==>", "" + QueNo);
        Log.e("onEnd strAnswer ==>", "" + strAnswer);
        Log.e("onEnd rightAnswers ==>", "" + rightAnswers);
        Log.e("onEnd wrongAnswers ==>", "" + wrongAnswers);

        isAnswered = true;
        if (isAnswered && isSelected) {
            attendedQue = attendedQue + 1;
            isSelected = false;
        }

        updateUI();

        if (txtNext.getText().toString().equalsIgnoreCase(getString(R.string.finish))) {
            Log.e("lavel_id ==>", "" + levelID);
            Log.e("questions_attended ==>", "" + attendedQue);
            Log.e("total_questions ==>", "" + questionList.size());
            Log.e("correct_answers ==>", "" + rightAnswers);
            Log.e("user_id ==>", "" + prefManager.getLoginId());
            Save_Question_Report();
        }

        if (QueNo != (questionList.size() - 1)) {

            if (questionList.size() > 1) {
                Log.e("QueNo ==>", "" + QueNo);
                if (QueNo == (questionList.size() - 2)) {
                    txtNext.setText(getResources().getString(R.string.finish));
                }

                QueNo = QueNo + 1;

                if (!questionList.get(QueNo).getImage().equalsIgnoreCase("")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Picasso.get().load(questionList.get(QueNo).getImage()).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
                txtQuestion.setText("" + questionList.get(QueNo).getQuestion());
                txtOptionA.setText("" + questionList.get(QueNo).getOptionA());
                txtOptionB.setText("" + questionList.get(QueNo).getOptionB());
                if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                    lyOptionC.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionC.setVisibility(View.VISIBLE);
                    txtOptionC.setText("" + questionList.get(QueNo).getOptionC());
                }
                if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                    lyOptionD.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionD.setVisibility(View.VISIBLE);
                    txtOptionD.setText("" + questionList.get(QueNo).getOptionD());
                }
                txtLevelNumber.setText("" + currentLevel);
                txtQueNumber.setText("" + (QueNo + 1));
                txtTotalQue.setText(" / " + questionList.size());
                countdownTimer.start(31000);
            } else {
                txtNext.setText(getResources().getString(R.string.finish));
            }

        } else {
            if (cv.getSecond() == 0) {
                isAnswered = true;
                txtAnswerStatus.setVisibility(View.VISIBLE);
                txtAnswerStatus.setTextColor(getResources().getColor(R.color.Red));
                txtAnswerStatus.setText(getResources().getString(R.string.time_over_status));
            }
        }
    }

    @Override
    public void onInterval(CountdownView cv, long remainTime) {
        Log.e("==>remainTime", "" + remainTime / 1000);
    }

    private void onNextFinish() {
        handler.removeCallbacks(runnable);
        isAnswered = true;
        if (isAnswered && isSelected) {
            attendedQue = attendedQue + 1;
            isSelected = false;
        }
        updateUI();

        if (txtNext.getText().toString().equalsIgnoreCase(getString(R.string.finish))) {
            Log.e("lavel_id ==>", "" + levelID);
            Log.e("questions_attended ==>", "" + attendedQue);
            Log.e("total_questions ==>", "" + questionList.size());
            Log.e("correct_answers ==>", "" + rightAnswers);
            Log.e("user_id ==>", "" + prefManager.getLoginId());
            Save_Question_Report();

        } else {
            Log.e("QueNo ==>", "" + QueNo);
            if (questionList.size() > 1) {

                if (QueNo == (questionList.size() - 2)) {
                    txtNext.setText(getResources().getString(R.string.finish));
                }
                QueNo = QueNo + 1;

                if (!questionList.get(QueNo).getImage().equalsIgnoreCase("")) {
                    ivQuestion.setVisibility(View.VISIBLE);
                    Picasso.get().load(questionList.get(QueNo).getImage()).into(ivQuestion);
                } else {
                    ivQuestion.setVisibility(View.GONE);
                }
                txtQuestion.setText("" + questionList.get(QueNo).getQuestion());
                txtOptionA.setText("" + questionList.get(QueNo).getOptionA());
                txtOptionB.setText("" + questionList.get(QueNo).getOptionB());
                if (questionList.get(QueNo).getOptionC().equalsIgnoreCase("")) {
                    lyOptionC.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionC.setVisibility(View.VISIBLE);
                    txtOptionC.setText("" + questionList.get(QueNo).getOptionC());
                }
                if (questionList.get(QueNo).getOptionD().equalsIgnoreCase("")) {
                    lyOptionD.setVisibility(View.INVISIBLE);
                } else {
                    lyOptionD.setVisibility(View.VISIBLE);
                    txtOptionD.setText("" + questionList.get(QueNo).getOptionD());
                }
                txtLevelNumber.setText("" + currentLevel);
                txtQueNumber.setText("" + (QueNo + 1));
                txtTotalQue.setText(" / " + questionList.size());
                countdownTimer.start(31000);

            } else {
                txtNext.setText(getResources().getString(R.string.finish));
            }
        }
    }

    private void onAnswerIt() {
        if (countdownTimer.getSecond() != 0) {
            if (!strAnswer.equalsIgnoreCase("")) {
                if (!isAnswered) {
                    isAnswered = true;
                    txtAnswerStatus.setVisibility(View.VISIBLE);
                    Log.e("answer==>", "" + countdownTimer.getSecond());
                    if (strAnswer.equalsIgnoreCase(questionList.get(QueNo).getAnswer())) {
                        mpCorrect = MediaPlayer.create(QuestionAnswer.this, R.raw.correct_ans_sound);
                        if (prefManager.getBool("SOUND") == true) {
                            mpCorrect.start();
                        }
                        if (prefManager.getBool("VIBRATION") == true) {
                            vibe.vibrate(100);
                        }

                        txtAnswerStatus.setTextColor(getResources().getColor(R.color.text_blue));
                        txtAnswerStatus.setText(getResources().getString(R.string.correct_answer_status));
                    } else {
                        mpWrong = MediaPlayer.create(QuestionAnswer.this, R.raw.wrong_ans_sound);
                        if (prefManager.getBool("SOUND") == true) {
                            mpWrong.start();
                        }
                        if (prefManager.getBool("VIBRATION") == true) {
                            vibe.vibrate(500);
                        }

                        txtAnswerStatus.setTextColor(getResources().getColor(R.color.Red));
                        txtAnswerStatus.setText(getResources().getString(R.string.wrong_answer_status));
                    }

                    CorrectUI();
                    handler.postDelayed(runnable, 3000);
                }
            } else {
                Toasty.info(QuestionAnswer.this, "" + getResources().getString(R.string.please_select_any_option),
                        Toasty.LENGTH_SHORT).show();
            }
        } else {
            txtAnswerStatus.setVisibility(View.VISIBLE);
            txtAnswerStatus.setTextColor(getResources().getColor(R.color.Red));
            txtAnswerStatus.setText(getResources().getString(R.string.time_over_status));
        }
    }

    private void updateUI() {
        txtAnswerStatus.setVisibility(View.GONE);
        lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));
        lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_light_primary));

        txtTickA.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickB.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickC.setBackground(getResources().getDrawable(R.drawable.round_bor_options));
        txtTickD.setBackground(getResources().getDrawable(R.drawable.round_bor_options));

        txtA.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtB.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtC.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtD.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionA.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionB.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionC.setTextColor(getResources().getColor(R.color.text_color_primary));
        txtOptionD.setTextColor(getResources().getColor(R.color.text_color_primary));

        if (!strAnswer.equalsIgnoreCase("")) {
            if (strAnswer.equalsIgnoreCase(questionList.get(QueNo).getAnswer())) {
                rightAnswers = rightAnswers + 1;
            } else {
                wrongAnswers = wrongAnswers + 1;
            }
            Log.e("UpdateUI right ==>", "" + rightAnswers);
            Log.e("UpdateUI wrong ==>", "" + wrongAnswers);

            txtRightAnswers.setText(getResources().getString(R.string.right) + " " + rightAnswers);
            txtWrongAnswers.setText(getResources().getString(R.string.wrong) + " " + wrongAnswers);
            strAnswer = "";
        }
        isAnswered = false;
    }

    private void CorrectUI() {
        if (!questionList.get(QueNo).getAnswer().equalsIgnoreCase("")) {
            if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("1")) {
                lyOptionA.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickA.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtA.setTextColor(getResources().getColor(R.color.white));
                txtOptionA.setTextColor(getResources().getColor(R.color.white));
            } else if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("2")) {
                lyOptionB.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickB.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtB.setTextColor(getResources().getColor(R.color.white));
                txtOptionB.setTextColor(getResources().getColor(R.color.white));
            } else if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("3")) {
                lyOptionC.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickC.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtC.setTextColor(getResources().getColor(R.color.white));
                txtOptionC.setTextColor(getResources().getColor(R.color.white));
            } else if (questionList.get(QueNo).getAnswer().equalsIgnoreCase("4")) {
                lyOptionD.setBackground(getResources().getDrawable(R.drawable.round_bg_green));
                txtTickD.setBackground(getResources().getDrawable(R.drawable.ic_checked));
                txtD.setTextColor(getResources().getColor(R.color.white));
                txtOptionD.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.shimmerHide(shimmer);
        countdownTimer.stop();
        handler.removeCallbacks(runnable);
    }

    //save_question_report API call
    private void Save_Question_Report() {
        Utility.ProgressBarShow(QuestionAnswer.this);

        AppAPI bookNPlayAPI = BaseURL.getVideoAPI();
        Call<SuccessModel> call = bookNPlayAPI.save_question_report(catID, "" + levelID,
                "" + attendedQue, "" + questionList.size(),
                "" + rightAnswers, "" + prefManager.getLoginId());
        call.enqueue(new Callback<SuccessModel>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(@NonNull Call<SuccessModel> call, @NonNull Response<SuccessModel> response) {
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("save_question_report massage =>", "" + response.body().getMessage());
                        Log.e("==>levelID", "" + levelID);
                        Intent intent = new Intent(QuestionAnswer.this, LevelResult.class);
                        intent.putExtra("levelID", "" + levelID);
                        intent.putExtra("currentLevel", "" + currentLevel);
                        intent.putExtra("TotalLevel", "" + TotalLevel);
                        startActivity(intent);
                        finish();
                        handler.removeCallbacks(runnable);
                    } else {
                        Log.e("save_question_report massage =>", "" + response.body().getMessage());
                    }
                    countdownTimer.allShowZero();
                    countdownTimer.stop();
                } catch (Exception e) {
                    Log.e("save_question_report API error==>", "" + e);
                }
                Utility.ProgressbarHide();
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(@NonNull Call<SuccessModel> call, @NonNull Throwable t) {
                Log.e("save_question_report Failure", "That didn't work!!!" + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.shimmerHide(shimmer);
        countdownTimer.stop();
        handler.removeCallbacks(runnable);
    }

}