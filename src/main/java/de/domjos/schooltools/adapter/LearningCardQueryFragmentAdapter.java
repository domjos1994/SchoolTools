package de.domjos.schooltools.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;
import de.domjos.schooltools.fragment.LearningCardFragment;

import java.util.List;

public class LearningCardQueryFragmentAdapter extends FragmentStatePagerAdapter {
    private LearningCardQuery query;
    private List<LearningCard> learningCards;
    private Context context;

    public LearningCardQueryFragmentAdapter(FragmentManager fm, Context context, LearningCardQuery query) {
        super(fm);
        this.context = context;
        this.setQuery(query);
    }

    public final void setQuery(LearningCardQuery query) {
        this.query = query;
        if(this.query!=null) {
            this.learningCards = this.query.loadLearningCards(this.context);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(this.query!=null) {
            return this.learningCards.get(position).getTitle();
        }
        return "";
    }

    @Override
    public Fragment getItem(int position) {
        if(this.query!=null) {
            LearningCardFragment fragment = new LearningCardFragment();
            fragment.setLearningCard(this.learningCards.get(position));
            fragment.setLearningCardQuery(this.query);
            fragment.setContext(this.context);
            return fragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        if(this.query==null) {
            return 0;
        } else {
            return this.learningCards.size();
        }
    }
}
