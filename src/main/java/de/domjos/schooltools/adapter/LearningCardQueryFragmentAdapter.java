package de.domjos.schooltools.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardQueryTraining;
import de.domjos.schooltools.fragment.LearningCardFragment;

import java.util.List;

public class LearningCardQueryFragmentAdapter extends FragmentStatePagerAdapter {
    private LearningCardQueryTraining training;
    private List<LearningCard> learningCards;
    private Context context;

    public LearningCardQueryFragmentAdapter(FragmentManager fm, Context context, LearningCardQueryTraining training) {
        super(fm);
        this.context = context;
        this.setQuery(training);
    }

    public final void setQuery(LearningCardQueryTraining training) {
        this.training = training;
        if(this.training!=null) {
            this.learningCards = this.training.getLearningCardQuery().loadLearningCards(this.context);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(this.training!=null) {
            return this.learningCards.get(position).getTitle();
        }
        return "";
    }

    @Override
    public Fragment getItem(int position) {
        if(this.training!=null) {
            LearningCardFragment fragment = new LearningCardFragment();
            fragment.setLearningCard(this.learningCards.get(position));
            fragment.setLearningCardQueryTraining(this.training);
            fragment.setContext(this.context);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        if(this.training==null) {
            return 0;
        } else {
            return this.learningCards.size();
        }
    }
}
