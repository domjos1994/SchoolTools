package de.domjos.schooltools.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import de.domjos.schooltools.core.model.learningCard.LearningCard;
import de.domjos.schooltools.core.model.learningCard.LearningCardQuery;

import java.util.List;

public class LearningCardQueryFragmentAdapter extends FragmentStatePagerAdapter {
    private LearningCardQuery query;
    private List<LearningCard> learningCards;
    private FragmentManager manager;

    public LearningCardQueryFragmentAdapter(FragmentManager fm, Context context, LearningCardQuery query) {
        super(fm);
        this.query = query;
        this.manager = fm;
        if(this.query!=null) {
            this.learningCards = this.query.loadLearningCards(context);
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
