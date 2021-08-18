package com.example.codeforces_info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static com.google.common.primitives.Ints.max;

public class Friend_profile_view extends AppCompatActivity {

    int accepted=0, wrong =0, tle=0,cpe=0,rte=0;
    HashMap<String,Integer> mpp_tags=new HashMap<String, Integer>();
    HashMap<Integer,Integer>mpp_ratings=new HashMap<Integer, Integer>();
    HashMap<String,Integer>mpp_level=new HashMap<String, Integer>();
    HashMap<String,Integer>vis=new HashMap<String, Integer>();
    int max_tag=0;
    PieChart pieChart;
    HorizontalBarChart barChart_tags;
    BarChart barChart_levels;
    BarChart barChart_ratings;

    //ratings change
    Integer maxRank=1000000,minRank=0,maxpos=0,maxneg=1000000,total_contests=0;
    TextView t1,t2,t3,t4,t5;
    String surl = "https://codeforces.com/api/";
    String purl = "https://codeforces.com/api/";

    //sneak button
    FrameLayout sneak_btn;


    String CF_handle="";
    String qurl = "https://codeforces.com/api/";
    ProgressBar pb;
    View showHiddenProfile;
    TextView emptyView;
    View fragment_show_when_ready;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_2);

        Intent i=getIntent();
        CF_handle=i.getStringExtra("handle");

        fragment_show_when_ready=findViewById(R.id.fragment2_view);
        pb = findViewById(R.id.progress_profile);
        Sprite foldingCube = new FoldingCube();
        pb.setIndeterminateDrawable(foldingCube);


        emptyView = findViewById(R.id.f2empty_view);
        showHiddenProfile=findViewById(R.id.LLtohide);

        //charts
        pieChart=findViewById(R.id.pie_chart_verdicts);
        barChart_tags=findViewById(R.id.bar_chart_tags);
        barChart_levels=findViewById(R.id.bar_chart_levels);
        barChart_ratings=findViewById(R.id.bar_chart_ratings);

        //textview ratings change
        t1=findViewById(R.id.totalcontests);
        t2=findViewById(R.id.bestrank);
        t3=findViewById(R.id.worstrank);
        t4=findViewById(R.id.maxpos);
        t5=findViewById(R.id.maxneg);

        sneak_btn=findViewById(R.id.sneak);

        sneak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Sneak_submissions.class));
            }
        });

        startProcess();

    }

    private void startProcess() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {



            /*-------------------------------*/


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(qurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            user_info_retro api = retrofit.create(user_info_retro.class);
            String extraUrl = "user.info?handles=" + CF_handle;
            Log.i(TAG, "onSuccess:----><"+qurl+extraUrl);
            Call<user_info_model> call = api.getModels(extraUrl);


            call.enqueue(new Callback<user_info_model>() {
                @Override
                public void onResponse(Call<user_info_model> call, Response<user_info_model> response) {
                    try {

                        List<result_user_info_model> data = response.body().getResult();

                        pb.setVisibility(GONE);
                        fragment_show_when_ready.setVisibility(View.VISIBLE);

                        result_user_info_model result = data.get(0);

                        TextView i1, i2;
                        i1 = findViewById(R.id.item_text_1);
                        i2 = findViewById(R.id.item_text_2);

                        if (result.getHandle() != null) {
                            i1.setText(result.getHandle());

                        }


                        if (result.getRank() != null) {
                            i2.setText(result.getRank());

                            int c = 0;
                            switch (result.getRank().toLowerCase()) {
                                case "newbie":
                                    c = R.color.newbie;
                                    break;
                                case "pupil":
                                    c = R.color.pupil;
                                    break;
                                case "specialist":
                                    c = R.color.specialist;
                                    break;
                                case "expert":
                                    c = R.color.expert;
                                    break;
                                case "candidate master":
                                    c = R.color.cm;
                                    break;
                                case "master":
                                case "international master":
                                    c = R.color.master;
                                    break;
                                case "grandmaster":
                                case "international grandmaster":
                                case "legendary grandmaster":
                                    c = R.color.gmaster;
                                    break;
                                default:
                                    c = R.color.unrated;

                            }
                            i2.setTextColor(getResources().getColor(c));
                        } else {
                            i2.setText(result.getRank());
                            i2.setTextColor(getResources().getColor(R.color.unrated));
                        }

                        TextView i1v, i2v, i3v;
                        i1v = findViewById(R.id.item_1_value);
                        if (result.getContribution() != null)
                            i1v.setText(result.getContribution().toString());
                        else i1v.setText("0");

                        i2v = findViewById(R.id.item_2_value);
                        if (result.getRating() != null)
                            i2v.setText(result.getRating().toString());
                        else i2v.setText("0");

                        i3v = findViewById(R.id.item_3_value);
                        if (result.getMaxRank() != null)
                            i3v.setText(result.getMaxRank().toString());
                        else i3v.setText("unrated");

                        int c = 0;
                        switch (result.getMaxRank().toLowerCase()) {
                            case "newbie":
                                c = R.color.newbie;
                                break;
                            case "pupil":
                                c = R.color.pupil;
                                break;
                            case "specialist":
                                c = R.color.specialist;
                                break;
                            case "expert":
                                c = R.color.expert;
                                break;
                            case "candidate master":
                                c = R.color.cm;
                                break;
                            case "master":
                            case "international master":
                                c = R.color.imaster;
                                break;
                            case "grandmaster":
                            case "international grandmaster":
                            case "legendary grandmaster":
                                c = R.color.gmaster;
                                break;
                            default:
                                c = R.color.unrated;

                        }
                        i3v.setTextColor(getResources().getColor(c));

                        ImageView image_profile = findViewById(R.id.item_profile_picture);

                        Picasso.get().load(result.getTitlePhoto()).into(image_profile);




                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*-------------------------------*/
                    //box 2nd
                    Retrofit retrofit_submissions = new Retrofit.Builder()
                            .baseUrl(surl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();


                    submissions_retro api_submissions = retrofit_submissions.create(submissions_retro.class);
                    String extraUrl_submissions = "user.status?handle=" + CF_handle;
                    Call<submissions> call_submissions = api_submissions.getModels(extraUrl_submissions);


                    call_submissions.enqueue(new Callback<submissions>() {
                        @Override
                        public void onResponse(Call<submissions> call, Response<submissions> response) {
                            try {

                                List<Result> result = response.body().getResult() ;

                                Log.i(TAG, "onResponse: 😍😍😍😍😍______OOOOOOO>"+result.size());
                                for(int i=0;i<result.size();i++)
                                {
                                    String s=result.get(i).getVerdict().trim();
                                    switch (s)
                                    {
                                        case "OK": accepted++;
                                            break;
                                        case "COMPILATION_ERROR": cpe++;
                                        break;
                                        case "RUNTIME_ERROR":rte++;
                                        break;
                                        case"WRONG_ANSWER": wrong++;
                                        break;
                                    }
                                    Log.i(TAG, "onResponse: 😍😍😍😍😍______OOOOOOO>"+s);
                                    if(!s.equals("OK"))continue;

                                    Problem problem=result.get(i).getProblem();

                                    String problem_id=problem.getContestId()+problem.getIndex();
                                    Log.i(TAG, "onResponse:--> "+problem_id);
                                    if(vis.containsKey(problem_id))
                                        continue;

                                    vis.put(problem_id,1);

                                    List<String> tags=problem.getTags();

                                    for(int j=0;j<tags.size();j++)
                                    {
                                        if(mpp_tags.containsKey(tags.get(j).trim()))
                                        mpp_tags.put(tags.get(j).trim(),mpp_tags.get(tags.get(j).trim())+1);
                                        else
                                        {
                                            mpp_tags.put(tags.get(j).trim(),1);
                                        }

                                        max_tag=max(mpp_tags.get(tags.get(j).trim()),max_tag);
                                    }

                                    String level=problem.getIndex();

                                    if(level!=null && mpp_level.containsKey(level))
                                        mpp_level.put(level,mpp_level.get(level)+1);
                                    else if(level!=null)
                                    {
                                        mpp_level.put(level,1);
                                    }
                                    int rating=0;
                                    if(problem.getRating()!=null)rating=problem.getRating();

                                    if( mpp_ratings.containsKey(rating))
                                    {
                                        mpp_ratings.put(rating,mpp_ratings.get(rating)+1);

                                    }
                                    else  mpp_ratings.put(rating,1);


                                }

                                create_verdicts();
                                create_tags();
                                create_levels();
                                create_ratings();



                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<submissions> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    /*-------------------------------*/
                    /*-------------------------------*/
                    //max +ve wala

                    Retrofit retrofit_b = new Retrofit.Builder()
                            .baseUrl(purl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();


                    ratings_api api_b = retrofit_b.create(ratings_api.class);
                    String extraUrl_b = "user.rating?handle=" + CF_handle;
                    Call<ratings_change> call_b = api_b.getModels(extraUrl_b);


                    call_b.enqueue(new Callback<ratings_change>() {
                        @Override
                        public void onResponse(Call<ratings_change> call, Response<ratings_change> response) {
                            try {

                                List<results_rating_change> result = response.body().getResult() ;


                                total_contests=result.size();
                                for(int i=0;i<result.size();i++)
                                {
                                     maxRank=Math.min(result.get(i).getRank(),maxRank);
                                     minRank=Math.max(result.get(i).getRank(),minRank);
                                     maxpos=Math.max(result.get(i).getNewRating()-result.get(i).getOldRating(),maxpos);
                                     maxneg=Math.min(result.get(i).getNewRating()-result.get(i).getOldRating(),maxneg);

                                }
                                Log.i(TAG, "onResponse: ratings change"+total_contests);

                                t1.setText(total_contests.toString());
                                t2.setText(maxRank.toString());
                                t3.setText(minRank.toString());
                                t4.setText(maxpos.toString());
                                t5.setText(maxneg.toString());

                                pb.setVisibility(GONE);
                                fragment_show_when_ready.setVisibility(View.VISIBLE);
                                showHiddenProfile.setVisibility(View.VISIBLE);



                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<ratings_change> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                        }
                    });


                    /*-------------------------------*/

                }

                @Override
                public void onFailure(Call<user_info_model> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pb.setVisibility(View.GONE);
            emptyView.setText("No internet connectivity");
            emptyView.setVisibility(View.VISIBLE);
        }


    }

    private void create_ratings() {
        ArrayList<Integer> temp=new ArrayList<Integer>();
        for(Map.Entry<Integer,Integer> entry : mpp_ratings.entrySet())
        {
            temp.add(entry.getKey());
        }
        Collections.sort(temp);

        barChart_ratings.getDescription().setEnabled(false);
        barChart_ratings.setFitBars(true);
        int count =mpp_ratings.size();
        ArrayList<BarEntry>val=new ArrayList<>();
        Integer[]labels=new Integer[mpp_ratings.size()];
        int i=1;
        for(Integer entry : temp)
        {   if(entry==0)continue;
            val.add(new BarEntry(i,mpp_ratings.get(entry)));
            labels[i-1]=entry;
            i++;
        }

        barChart_ratings.getDescription().setEnabled(false);
        BarDataSet set1=new BarDataSet(val,"Question Ratings");
        set1.setColors(ColorTemplate.rgb("#1976D2"));
        set1.setValueTextSize(7f);
        set1.setDrawValues(true);

        barChart_ratings.getXAxis().setDrawGridLines(false);
        BarData data=new BarData(set1);
        //To set components of x axis
        XAxis xAxis = barChart_ratings.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setTextSize(7f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart_ratings.getXAxis().setLabelCount(count);
        barChart_ratings.getXAxis().setValueFormatter(new MyratingsYAxisValueFormatter(labels));



        barChart_ratings.setDrawGridBackground(false);
        barChart_ratings.setData(data);
        barChart_ratings.invalidate();
    }

    private void create_levels() {
        for(char i='A';i<'J';i++)
        {   String s=""+i;
            if(mpp_level.containsKey(s))continue;
            else mpp_level.put(s,0);
        }
        barChart_levels.getDescription().setEnabled(false);
        barChart_levels.setFitBars(true);
        int count =mpp_level.size();
        ArrayList<BarEntry>val=new ArrayList<>();
        String[]labels=new String[mpp_level.size()];
        int i=1;
        for(Map.Entry<String,Integer> entry : mpp_level.entrySet())
        {
            val.add(new BarEntry(i,entry.getValue()));
            labels[i-1]=entry.getKey();
            i++;
            if(i==10)break;
        }

        barChart_levels.getDescription().setEnabled(false);
        BarDataSet set1=new BarDataSet(val,"Question Levels");
        set1.setColors(ColorTemplate.rgb("#1976D2"));
        set1.setValueTextSize(10f);
        set1.setDrawValues(true);

        barChart_levels.getXAxis().setDrawGridLines(false);
        BarData data=new BarData(set1);
        //To set components of x axis
        XAxis xAxis = barChart_levels.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart_levels.getXAxis().setLabelCount(count);
        barChart_levels.getXAxis().setValueFormatter(new MyYAxisValueFormatter(labels));



        barChart_levels.setDrawGridBackground(false);
        barChart_levels.setData(data);
        barChart_levels.invalidate();



    }

    private void create_tags() {

        int count= mpp_tags.size();
        int range=max_tag+20;

        ArrayList<BarEntry> val=new ArrayList<>();
        String[]labels=new String[mpp_tags.size()];
        float space=4f;
        float barwidth=9f;
        int i=1;
        for (Map.Entry<String,Integer> entry : mpp_tags.entrySet())
        {
            val.add(new BarEntry(i,entry.getValue()));
            labels[i-1]=entry.getKey();
            if(entry.getKey().equals("number theory"))
                Log.i(TAG, "create_tags: ss"+entry.getValue());
            i++;
        }



        barChart_tags.getDescription().setEnabled(false);
        BarDataSet set1=new BarDataSet(val,"Tags");
        set1.setColors(ColorTemplate.MATERIAL_COLORS);
        set1.setValueTextSize(10f);
        barChart_tags.getAxisRight().setDrawGridLines(false);
        barChart_tags.getAxisLeft().setDrawGridLines(false);
        barChart_tags.getXAxis().setDrawGridLines(false);
        BarData data=new BarData(set1);
        //To set components of x axis
        XAxis xAxis = barChart_tags.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        barChart_tags.getXAxis().setLabelCount(count);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(labels));
        xAxis.setDrawGridLines(false);



        barChart_tags.setDrawGridBackground(false);
        barChart_tags.setData(data);
        barChart_tags.invalidate();


    }



    void create_verdicts() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        // pieChart.setDescription();
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.96f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setCenterText("Verdicts");
        pieChart.setCenterTextSize(20);

        ArrayList<PieEntry> val=new ArrayList<>();

        val.add(new PieEntry(accepted,"AC"));
        val.add(new PieEntry(wrong,"WA"));
        val.add(new PieEntry(tle,"TLE"));
        val.add(new PieEntry(cpe,"CPE"));
        val.add(new PieEntry(rte,"RTE"));

        PieDataSet dataSet=new PieDataSet(val,"");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> color=new ArrayList<>();
        color.add(ContextCompat.getColor(getApplicationContext(),R.color.pupil));
        color.add(ContextCompat.getColor(getApplicationContext(), R.color.accent));
        color.add(Color.GRAY);
        color.add(ContextCompat.getColor(getApplicationContext(),R.color.primary_dark));
        color.add(Color.MAGENTA);
        dataSet.setColors(color);

        PieData data= new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        Log.i(TAG, "create_verdicts: ");
        pieChart.setData(data);



        pieChart.invalidate();


    }
}