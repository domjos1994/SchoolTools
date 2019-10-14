/*
 * Copyright (C)  2019 Domjos
 * This file is part of UniTrackerMobile <https://github.com/domjos1994/UniTrackerMobile>.
 *
 * UniTrackerMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UniTrackerMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UniTrackerMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.schooltools.custom;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import de.domjos.schooltools.R;
import de.domjos.schooltools.core.model.objects.BaseDescriptionObject;


public class SwipeRefreshDeleteList extends SwipeRefreshLayout {
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ReloadListener reloadListener;
    private DeleteListener deleteListener;
    private ClickListener clickListener;
    private LinearLayoutManager manager;
    private Drawable icon;

    public SwipeRefreshDeleteList(@NonNull Context context) {
        super(context);

        this.icon = null;
        this.context = context;
        this.initDefault();
        this.initAdapter();
    }

    public SwipeRefreshDeleteList(@NonNull Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.SwipeRefreshDeleteList, 0, 0);
        this.icon = a.getDrawable(R.styleable.SwipeRefreshDeleteList_itemIcon);
        this.context = context;
        this.initDefault();
        this.initAdapter();
    }

    public RecyclerAdapter getAdapter() {
        return this.adapter;
    }

    private void initDefault() {
        this.recyclerView = new RecyclerView(this.context);
        this.recyclerView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(this.recyclerView);
    }

    private void initAdapter() {
        this.adapter = new RecyclerAdapter(this.recyclerView, (Activity) this.context, this.icon);
        this.recyclerView.setAdapter(this.adapter);
        this.manager = new LinearLayoutManager(this.context);
        this.recyclerView.setLayoutManager(this.manager);
        this.adapter.notifyDataSetChanged();

        this.adapter.onSwipeListener(new SwipeToDeleteCallback(this.context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (deleteListener != null) {
                    deleteListener.onDelete(adapter.getItem(viewHolder.getAdapterPosition()));
                }
                if (viewHolder.getAdapterPosition() != -1) {
                    getAdapter().deleteItem(viewHolder.getAdapterPosition());
                }
            }
        });

        this.adapter.setClickListener(v -> {
            int position = this.recyclerView.indexOfChild(v);
            int firstPosition = this.manager.findFirstVisibleItemPosition();
            if (clickListener != null) {
                int currentPosition = firstPosition + position;
                if(currentPosition!=this.adapter.noEntryItem) {
                    clickListener.onClick(this.adapter.getItem(currentPosition));
                }
            }
        });

        this.setOnRefreshListener(() -> {
            if (this.reloadListener != null) {
                this.reloadListener.onReload();
            }
            this.setRefreshing(false);
        });
    }

    public void reload(ReloadListener reloadListener) {
        this.reloadListener = reloadListener;
    }

    public void deleteItem(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void click(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setContextMenu(int menuId) {
        this.adapter.setContextMenu(menuId);
    }

    public abstract static class ReloadListener {
        public abstract void onReload();
    }

    public abstract static class DeleteListener {
        public abstract void onDelete(BaseDescriptionObject listObject);
    }

    public abstract static class ClickListener {
        public abstract void onClick(BaseDescriptionObject listObject);
    }
}
