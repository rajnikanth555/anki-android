/*
 *  Copyright (c) 2021 David Allison <davidallisongithub@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify it under
 *  the terms of the GNU General Public License as published by the Free Software
 *  Foundation; either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ichi2.anki.lint.rules;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.ichi2.anki.lint.utils.Constants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UField;
import org.jetbrains.uast.UVariable;
import org.jetbrains.uast.UastVisibility;

import java.util.Collections;
import java.util.List;

/**
 * https://github.com/ankidroid/Anki-Android/wiki/Code-style#non-public-non-static-field-names-should-start-with-m
 */
public class FieldNamingPatternDetector extends Detector implements Detector.UastScanner {

    private static final Implementation implementation = new Implementation(FieldNamingPatternDetector.class, Scope.JAVA_FILE_SCOPE);

    public static Issue ISSUE = Issue.create(
            "FieldNamingPatternDetector",
            "non-public non-static naming",
            "Non-public, non-static field names should start with m",
            Constants.ANKI_STYLE_CATEGORY,
            Constants.ANKI_STYLE_PRIORITY,
            Constants.ANKI_STYLE_SEVERITY,
            implementation
            );

    @Nullable
    @Override
    public UElementHandler createUastHandler(@NotNull JavaContext context) {
        return new VariableNamingHandler(context);
    }


    @Nullable
    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return Collections.singletonList(UVariable.class);
    }

    private class VariableNamingHandler extends UElementHandler {

        private final JavaContext mContext;


        public VariableNamingHandler(JavaContext context) {
            this.mContext = context;
        }


        @Override
        public void visitVariable(@NotNull UVariable node) {
            // HACK: Using visitField didn't return any results
            if (!(node instanceof UField)) {
                return;
            }

            if (node.isStatic() || node.getVisibility() == UastVisibility.PUBLIC) {
                return;
            }

            String variableName = node.getName();

            if (variableName == null) {
                return;
            }

            if (variableName.length() < 2) {
                // cast the node as it's ambiguous between two interfaces
                UElement uNode = node;
                mContext.report(ISSUE, uNode, mContext.getNameLocation(uNode), "Variable name is too short");
                return;
            }

            if (variableName.startsWith("m") && Character.isUpperCase(variableName.charAt(1))) {
                return;
            }

            // we have a problem: either we don't have an m, or we do, and the next value is uppercase
            String prefix = "m";

            String replacement = prefix + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);

            // TODO: A fix should be possible, but it requires a rename operation

            // cast the node as it's ambiguous between two interfaces
            UElement uNode = node;
            mContext.report(ISSUE, uNode, mContext.getNameLocation(uNode), "Field should be named: '" + replacement + "'");
        }
    }
}