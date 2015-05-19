package edu.ucsd.studentpoll;

import android.app.AlertDialog;
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
import edu.ucsd.studentpoll.models.ChoiceQuestion;
import edu.ucsd.studentpoll.models.ChoiceResponse;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.rest.RESTException;

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

    @Override
    protected void onActivityResult(int requestCode, int resultStatus, Intent data) {
        if (requestCode == REQ_CODE_ADD_QUESTION) {
            if(resultStatus == RESULT_OK){
                Question question = data.getParcelableExtra("question");
                questions.add(question);
                questionsAdapter.setQuestions(questions);

                if(questions.size() == 1 && ((EditText)findViewById(R.id.pollName)).getText().toString().equals("")) {
                    ((EditText)findViewById(R.id.pollName)).setText(question.getTitle());
                }
            }
        }
    }

    public void createPoll(View view) {
        if(questions.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No Questions Added.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = ((EditText)findViewById(R.id.pollName)).getText().toString();

        final Poll poll = new Poll.Builder().withTitle(name).withQuestions(questions).build();

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

                }
            }
        }.execute();

        finish();
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

    public void createScheduleQuestion() {
        Intent intent = new Intent(this, CreateSchedulePoll.class);
        startActivityForResult(intent, REQ_CODE_ADD_QUESTION);
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
            holder.setContent(questions.get(position));
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }
    }

    private static class QuestionsViewHolder extends RecyclerView.ViewHolder {

        private CardView questionCard;

        public QuestionsViewHolder(CardView questionCard) {
            super(questionCard);
            this.questionCard = questionCard;
        }

        public void setContent(Question question) {
            ((TextView)questionCard.findViewById(R.id.questionTitle)).setText(question.getTitle());

            LinearLayout settingsList = (LinearLayout) questionCard.findViewById(R.id.settingsList);
            LinearLayout choicesList = (LinearLayout) questionCard.findViewById(R.id.choicesList);

            choicesList.removeAllViews();
            settingsList.removeAllViews();

            if(question instanceof ChoiceQuestion) {
                ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
                for(String option: choiceQuestion.getOptions() ) {
                    TextView textView = new TextView(questionCard.getContext());
                    textView.setText(option);
                    choicesList.addView(textView);
                }

                TextView questionType = new TextView(questionCard.getContext());
                questionType.setText("Multiple Choice");
                settingsList.addView(questionType);

                if(choiceQuestion.getAllowCustom()) {
                    TextView customAllowed = new TextView(questionCard.getContext());
                    customAllowed.setText("Custom Allowed");
                    settingsList.addView(customAllowed);
                }

                if(choiceQuestion.getAllowMultiple()) {
                    TextView textView = new TextView(questionCard.getContext());
                    textView.setText("Choose Many");
                    settingsList.addView(textView);
                } else {
                    TextView textView = new TextView(questionCard.getContext());
                    textView.setText("Choose One");
                    settingsList.addView(textView);
                }
            }
        }
    }


}