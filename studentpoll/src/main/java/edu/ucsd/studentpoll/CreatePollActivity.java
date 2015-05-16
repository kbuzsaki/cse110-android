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
import android.widget.Toast;
import edu.ucsd.studentpoll.models.Group;
import edu.ucsd.studentpoll.models.Model;
import edu.ucsd.studentpoll.models.Question;
import edu.ucsd.studentpoll.models.User;
import edu.ucsd.studentpoll.rest.RESTException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kdhuynh on 5/12/15.
 */
public class CreatePollActivity extends ActionBarActivity {

    private static final String[] QUESTION_TYPES = {"Multiple Choice (Single Answer)",
                                                    "Multiple Choice (Multi Answer)",
                                                    "Rank",
                                                    "Schedule" };

    private RecyclerView questionsView;

//    private QuestionsAdapter questionsAdapter;

    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_poll_activity);

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
            public void onClick (DialogInterface dialog,int which){
                switch (which) {
                    case 0:
                        createSingleChoicePoll();
                        break;
                    case 1:
                        createMultipleChoicePoll();
                        break;
                    case 2:
                        break;
                    case 3:
                        createScheduleChoicePoll();
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createSingleChoicePoll() {
        Intent intent = new Intent(this, CreateChoicePoll.class);
        intent.putExtra("allowMultiple", false);
        startActivity(intent);
    }

    public void createMultipleChoicePoll() {
        Intent intent = new Intent(this, CreateChoicePoll.class);
        intent.putExtra("allowMultiple", true);
        startActivity(intent);
    }

    public void createScheduleChoicePoll() {
        Intent intent = new Intent(this, CreateSchedulePoll.class);
        startActivity(intent);
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
