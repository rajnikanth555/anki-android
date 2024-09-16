/*
 *  Copyright (c) 2021 Mrudul Tora <mrudultora@gmail.com>
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

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.google.common.annotations.VisibleForTesting;
import com.ichi2.anki.lint.utils.Constants;
import com.intellij.psi.PsiMethod;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UCallExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * This custom Lint rules will raise an error if a developer uses the {@link android.app.Activity#onCreateOptionsMenu} method instead
 * of using the {@link android.app.Activity#onPrepareOptionsMenu}.
 */

public class OnPrepareOptionsMenuUsage extends Detector implements SourceCodeScanner {

    @VisibleForTesting
    static final String ID = "OnPrepareOptionsMenuUsage";

    @VisibleForTesting
    static final String DESCRIPTION = "Use onCreateOptionsMenu rather than using onPrepareOptionsMenu";

    private static final String EXPLANATION = "menu.findItem would return null on Android 8 if onPrepareOptionsMenu is used.";

    private static final Implementation implementation = new Implementation(OnPrepareOptionsMenuUsage.class, Scope.JAVA_FILE_SCOPE);

    public static final Issue ISSUE = Issue.create(
            ID,
            DESCRIPTION,
            EXPLANATION,
            Constants.ANKI_OPTIONS_MENU_CATEGORY,
            Constants.ANKI_OPTIONS_MENU_PRIORITY,
            Constants.ANKI_OPTIONS_MENU_SEVERITY,
            implementation
    );


    public OnPrepareOptionsMenuUsage() {

    }


    @Nullable
    @Override
    public List<String> getApplicableMethodNames() {
        List<String> forbiddenMethods = new ArrayList<>();
        forbiddenMethods.add("onPrepareOptionsMenu");
        return forbiddenMethods;
    }


    @Override
    public void visitMethodCall(@NotNull JavaContext context, @NotNull UCallExpression node, @NotNull PsiMethod method) {
        super.visitMethodCall(context, node, method);
        JavaEvaluator javaEvaluator = context.getEvaluator();
        if (javaEvaluator.isMemberInSubClassOf(method, "android.app.Activity", true)) {
            context.report(
                    ISSUE,
                    context.getCallLocation(node, true, true),
                    DESCRIPTION
            );
        }
    }
}
