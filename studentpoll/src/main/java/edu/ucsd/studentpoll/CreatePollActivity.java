package edu.ucsd.studentpoll;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Poll;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.rest.RESTException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kdhuynh on 5/12/15.
 */
public class CreatePollActivity extends ActionBarActivity {

    private static final String TAG = "CreatePollActivity";

    private static final String[] QUESTION_TYPES = {"Multiple Choice (Single Answer)",
                                                    "Multiple Choice (Multi Answer)",
                                                    "Rank",
                                                    "Schedule" };

    private static final int REQ_CODE_ADD_QUESTION = 1;

    private RecyclerView questionsView;

//    private QuestionsAdapter questionsAdapter;

    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_poll_activity);
        questions = new ArrayList<>();

//        questionsView = (RecyclerView) findViewById(R.id.questionsView);
//        questionsView.setLayoutManager(new LinearLayoutManager(this));
//
//        questionsAdapter = new QuestionsAdapter(Collections.<Question>emptyList());
//        questionsView.setAdapter(questionsAdapter);

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
            }
//            // what to do if the activity was cancelled
//            if (resultStatus == RESULT_CANCELED) {
//                // no op?
//            }
        }
    }

    public void createPoll(View view) {
        String name = ((EditText)findViewById(R.id.pollName)).getText().toString();

        // if no name is provided, use name of first question
        if(name == "") {
            name = questions.get(0).getTitle();
        }

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

//    private static class QuestionsAdapter extends RecyclerView.Adapter<QuestionsViewHolder> {
//
//        private List<Question> questions;
//
//        public QuestionsAdapter(List<Question> question) {
//            this.questions = question;
//        }
//
//        public void setQuestions(List<Question> question) {
//            this.questions = questions;
//            this.notifyDataSetChanged();
//        }
//
//        @Override
//        public QuestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            CardView groupCard = (CardView) LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.create_poll_question, parent, false);
//            QuestionsViewHolder viewHolder = new QuestionsViewHolder(groupCard);
//            // you don't have to set the content here before you return the viewHolder
//            // onBindViewHolder will get called next and set the content for us
//            // so just make and return an empty view
//            return viewHolder;
//        }
//
//        @Override
//        public void onBindViewHolder(QuestionsViewHolder holder, int position) {
//            holder.setContent(questions.get(position));
//        }
//
//        @Override
//        public int getItemCount() {
//            return questions.size();
//        }
//    }
//
//    private static class QuestionsViewHolder extends RecyclerView.ViewHolder {
//
//        private CardView questionCard;
//
//        public QuestionsViewHolder(CardView questionCard) {
//            super(questionCard);
//            this.questionCard = questionCard;
//        }
//
//        public void setContent(Question question) {
////            ((TextView)questionCard.findViewById(R.id.title)).setText(group.getName());
////            String timeText = randInt(2, 21) + " minutes ago";
////            String voteText = randInt(0, 7) + "/" + randInt(7, 10) + " votes";
////            ((TextView)questionCard.findViewById(R.id.time)).setText(timeText);
////            ((TextView)questionCard.findViewById(R.id.votes)).setText(voteText);
//        }
//    }


}
