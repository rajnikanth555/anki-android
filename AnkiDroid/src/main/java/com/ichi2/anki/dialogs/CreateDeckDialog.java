/****************************************************************************************
 * Copyright (c) 2021 Akshay Jadhav <jadhavakshay0701@gmail.com>                        *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/

package com.ichi2.anki.dialogs;

import android.content.Context;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ichi2.anki.AnkiActivity;
import com.ichi2.anki.DeckPicker;
import com.ichi2.anki.R;
import com.ichi2.anki.UIUtils;
import com.ichi2.anki.exception.FilteredAncestor;
import com.ichi2.libanki.Decks;
import com.ichi2.ui.FixedEditText;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import timber.log.Timber;

public class CreateDeckDialog {

    private final EditText mDialogEditText;
    public final MaterialDialog.Builder mBuilder;
    private final Context mContext;
    private final int mTitle;
    private final AnkiActivity mAnkiActivity;

    public CreateDeckDialog(@NonNull Context context, @NonNull int title) {
        this.mContext = context;
        this.mTitle = title;
        this.mDialogEditText = new FixedEditText(context);
        mAnkiActivity = new AnkiActivity();
        mBuilder = new MaterialDialog.Builder(context);
    }

    public void createFliteredDeck(){
        Timber.i("DeckPicker:: New filtered deck button pressed");
        ArrayList<String> names = mAnkiActivity.getCol().getDecks().allNames();
        int n = 1;
        String name = String.format(Locale.getDefault(), "%s %d", mContext.getResources().getString(R.string.filtered_deck_name), n);
        while (names.contains(name)) {
            n++;
            name = String.format(Locale.getDefault(), "%s %d", mContext.getResources().getString(R.string.filtered_deck_name), n);
        }
        mDialogEditText.setText(name);

       createDeck();
    }

    public String getDeckName() {
        return mDialogEditText.getText().toString();
    }

    public MaterialDialog createDeck() {
        mDialogEditText.setSingleLine(true);

        return mBuilder.title(mTitle)
                .positiveText(R.string.dialog_ok)
                .customView(mDialogEditText, true)
                .negativeText(R.string.dialog_cancel)
                .build().getBuilder().show();
    }

    public void onCancelled() {
        mBuilder.build().dismiss();
    }

    public boolean setCreateSubDeckOf(@NonNull long did) {
        String deckName = getDeckName();
        String deckNameWithParentName = mAnkiActivity.getCol().getDecks().getSubdeckName(did, deckName);
        return onNewDeckCreated(deckNameWithParentName);
    }

    public boolean onNewDeckCreated(@NonNull String deckName) {
        if (Decks.isValidDeckName(deckName)) {
            return onValidDeck(deckName);
        } else {
            Timber.d("configureFloatingActionsMenu::addDeckButton::onPositiveListener - Not creating invalid deck name '%s'", deckName);
            UIUtils.showThemedToast(mContext, mContext.getString(R.string.invalid_deck_name), false);
            return false;
        }
    }

    private boolean onValidDeck(@NonNull String deckName) {
        try {
            mAnkiActivity.getCol().getDecks().id(deckName);
            Timber.i("DeckPicker:: Creating new deck...");
        } catch (FilteredAncestor filteredAncestor) {
            UIUtils.showThemedToast(mContext, mContext.getString(R.string.decks_rename_filtered_nosubdecks), false);
            return false;
        }
        return true;
    }
}
