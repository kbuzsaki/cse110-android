package edu.ucsd.studentpoll;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.*;
import edu.ucsd.studentpoll.rest.RESTException;
import edu.ucsd.studentpoll.view.NewlineInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kdhuynh on 5/12/15.
 */
public class CreatePollActivity extends ActionBarActivity {

    private static final String TAG = "CreatePollActivity";

    private static final String[] QUESTION_TYPES = {"Multiple Choice (Choose One)",
                                                    "Multiple Choice (Choose Many)",
                                                    "Rank",
                                                    "Schedule" };

    private static final int REQ_CODE_ADD_QUESTION = 1;
    private static final int REQ_CODE_EDIT_QUESTION = 2;

    private RecyclerView questionsView;

    private QuestionsAdapter questionsAdapter;

    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_poll_activity);
        questions = new ArrayList<>();

        questionsView = (RecyclerView) findViewById(R.id.questionsView);
        questionsView.setLayoutManager(new LinearLayoutManager(this));

        questionsAdapter = new QuestionsAdapter(Collections.<Question>emptyList());
        questionsView.setAdapter(questionsAdapter);

        ((EditText)findViewById(R.id.pollName)).setOnEditorActionListener(new NewlineInterceptor());

        addQuestion(null);
    }

    public void addQuestion(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Type of Question: ");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setItems(QUESTION_TYPES, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        createSingleChoiceQuestion();
                        break;
                    case 1:
                        createMultipleChoiceQuestion();
                        break;
                    case 2:
                        createRankQuestion();
                        break;
                    case 3:
                        createScheduleQuestion();
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createSingleChoiceQuestion() {
        Intent intent = new Intent(this, CreateChoiceQuestionActivity.class);
        intent.putExtra("allowMultiple", false);
        startActivityForResult(intent, REQ_CODE_ADD_QUESTION);
    }

    public void createMultipleChoiceQuestion() {
        Intent intent = new Intent(this, CreateChoiceQuestionActivity.class);
        intent.putExtra("allowMultiple", true);
        startActivityForResult(intent, REQ_CODE_ADD_QUESTION);
    }

    public void createRankQuestion() {
        Intent intent = new Intent(this, CreateRankQuestionActivity.class);
        startActivityForResult(intent, REQ_CODE_ADD_QUESTION);
    }

    public void createScheduleQuestion() {
        Intent intent = new Intent(this, CreateSchedulePoll.class);
        startActivityForResult(intent, REQ_CODE_ADD_QUESTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultStatus, Intent data) {

        if(resultStatus == RESULT_OK) {
            Question question = data.getParcelableExtra("question");

            if (requestCode == REQ_CODE_ADD_QUESTION) {
                questions.add(question);

            } else if (requestCode == REQ_CODE_EDIT_QUESTION) {
                int index = data.getIntExtra("index", 0);
                questions.set(index, question);

            }

            questionsAdapter.setQuestions(questions);

            if (questions.size() == 1 && getPollTitle().equals("") ) {
                setPollTitle(question.getTitle());
            }
        }
    }

    public void setPollTitle(String title) {
        ((EditText) findViewById(R.id.pollName)).setText(title);
    }

    public String getPollTitle() {
        return ((EditText) findViewById(R.id.pollName)).getText().toString();
    }

    public Group getGroup() {
        return getIntent().getParcelableExtra("group");
    }

    public void createPoll(View view) {
        if(questions.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Questions Added.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = getPollTitle();
        Group group = getGroup();

        final Poll poll = new Poll.Builder().withTitle(name).withQuestions(questions).withGroup(group).build();

        new AsyncTask<Object, Object, Poll>() {
            @Override
            protected Poll doInBackground(Object[] params) {
                try {
                    return Poll.postPoll(poll);
                }
                catch (RESTException e) {
                    Log.w(TAG, "Failed to post poll", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Poll poll) {
                if(poll == null) {
                    Toast.makeText(getApplicationContext(), "Failed to make poll.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i(TAG, "broadcast activity about to start");
                    startBroadcast(poll);
                    Log.i(TAG, "broadcast activity started");
                }
                finish();
            }
        }.execute();
    }

    public void startBroadcast(Poll poll) {
        Intent intent = new Intent(this, PollActivity.class);
        intent.putExtra("startBroadcast", true);
        intent.putExtra("poll", poll);
        startActivity(intent);
    }

    private static class QuestionsAdapter extends RecyclerView.Adapter<QuestionsViewHolder> {

        private List<Question> questions;

        public QuestionsAdapter(List<Question> questions) {
            this.questions = questions;
        }

        public void setQuestions(List<Question> questions) {
            this.questions = questions;
            this.notifyDataSetChanged();
        }

        @Override
        public QuestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView groupCard = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.create_poll_question, parent, false);
            QuestionsViewHolder viewHolder = new QuestionsViewHolder(groupCard);
            // you don't have to set the content here before you return the viewHolder
            // onBindViewHolder will get called next and set the content for us
            // so just make and return an empty view
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(QuestionsViewHolder holder, int position) {
            holder.setContent(questions.get(position), position);
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }
    }

    private static class QuestionsViewHolder extends RecyclerView.ViewHolder {

        private CardView questionCard;
        private Context context;

        public QuestionsViewHolder(CardView questionCard) {
            super(questionCard);
            this.questionCard = questionCard;
            this.context = questionCard.getContext();
        }

        public void setContent(final Question question, final int position) {
            ((TextView)questionCard.findViewById(R.id.questionTitle)).setText(question.getTitle());

            LinearLayout settingsList = (LinearLayout) questionCard.findViewById(R.id.settingsList);
            LinearLayout choicesList = (LinearLayout) questionCard.findViewById(R.id.choicesList);

            choicesList.removeAllViews();
            settingsList.removeAllViews();

            if(question instanceof ChoiceQuestion) {
                // Populate the options
                ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
                for(String option: choiceQuestion.getOptions() ) {
                    TextView textView = new TextView(context);
                    textView.setText(option);
                    choicesList.addView(textView);
                }

                // Populate the settings
                TextView questionType = new TextView(context);
                questionType.setText("Multiple Choice");
                settingsList.addView(questionType);

                if(choiceQuestion.getAllowCustom()) {
                    TextView customAllowed = new TextView(context);
                    customAllowed.setText("Custom Allowed");
                    settingsList.addView(customAllowed);
                }

                if(choiceQuestion.getAllowMultiple()) {
                    TextView textView = new TextView(context);
                    textView.setText("Choose Many");
                    settingsList.addView(textView);
                } else {
                    TextView textView = new TextView(context);
                    textView.setText("Choose One");
                    settingsList.addView(textView);
                }
            }

            else if (question instanceof RankQuestion) {
                // Populate the options
                RankQuestion choiceQuestion = (RankQuestion) question;
                for(String option: choiceQuestion.getOptions() ) {
                    TextView textView = new TextView(context);
                    textView.setText(option);
                    choicesList.addView(textView);
                }

                // Populate the settings
                TextView questionType = new TextView(context);
                questionType.setText("Rank Choices");
                settingsList.addView(questionType);
            }

            // clicking on a question card lets you edit the question
            questionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = null;

                    if(question instanceof ChoiceQuestion) {
                        intent = new Intent(context, CreateChoiceQuestionActivity.class);
                        intent.putExtra("question", question);
                        intent.putExtra("index", position);
                    }
                    else if(question instanceof RankQuestion) {
                        intent = new Intent(context, CreateRankQuestionActivity.class);
                        intent.putExtra("question", question);
                        intent.putExtra("index", position);
                    }
                    else {

                    }

                    ((Activity) context).startActivityForResult(intent, REQ_CODE_EDIT_QUESTION);
                }
            });
        }
    }


}
